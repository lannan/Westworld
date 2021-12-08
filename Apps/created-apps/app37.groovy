/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)

preferences {

	section("Enable / Disable the following functionality:") {
        input "app_enabled", "bool", title: "Auto Humidity Vent", required:true, defaultValue:true
        input "fan_control_enabled", "bool", title: "Vent Fan Control", required:true, defaultValue:true
	}

	section("Choose a humidity sensor...") {
		input "humidity_sensor", "capability.relativeHumidityMeasurement", title: "Humidity Sensor", required: true
	}
	section("Enter the relative humudity level (%) above which the vent fans will activate:") {
		input "humidity_a", "number", title: "Humidity Activation Level", required: true, defaultValue:70
	}
	section("Enter the relative humudity level (%) below which the vent fans will deactivate:") {
		input "humidity_d", "number", title: "Humidity Deactivation Level", required: true, defaultValue:65
	}

	section("Select the vent fans to control...") {
		input "fans", "capability.switch", title: "Vent Fans", multiple: true, required: true
	}

	section("Select the vent fan energy meters to monitor...") {
		input "emeters", "capability.energyMeter", title: "Energy Meters", multiple: true, required: false
		input "price_kwh", "decimal", title: "Cost in cents per kWh (12 is US avg)", required: true, defaultValue:12
	}

	section("Set notification options:") {
        input "sendPushMessage", "bool", title: "Push notifications", required:true, defaultValue:false
        input "phone", "phone", title: "Send text messages to", required: false
    }
    
    section("Monitor the humidity of:") {
		input "humiditySensor1", "capability.relativeHumidityMeasurement"
	}
	section("input1:") {
		input "input1", "number", title: "integer ?"
	}
	section("input2:") {
		input "input2", "number", title: "integer ?"
	}
	section( "Notifications" ) {
		input "phone1", "phone", title: "Send a Text Message?", required: false
	}
	section("Control this switch:") {
		input "switch1", "capability.switch", required: true
	}

}


def installed() {

	log.debug "${app.label} installed with settings: ${settings}"

	state.app_enabled = false
	state.fan_control_enabled = false

	state.fansOn = false
	state.fansOnTime = now()
	state.fansLastRunTime = 0

	initialize()
	
	subscribe(humiditySensor1, "humidity", humidityHandler)

}


def uninstalled()
{

   	send("${app.label} uninstalled.")
    
	state.app_enabled = false

	set_fans(false)

	state.fan_control_enabled = false

}


def updated() {

	log.debug "${app.label} updated with settings: ${settings}"

	unsubscribe()

	initialize()

}


def initialize() {

	if (settings.fan_control_enabled) {
		if(state.fan_control_enabled == false) {
			send("Vent Fan Control Enabled.")
        } else {
        	log.debug "Vent Fan Control Enabled."
        }

		state.fan_control_enabled = true
	} else {
		if(state.fan_control_enabled == true) {
			send("Vent Fan Control Disabled.")
        } else {
        	log.debug "Vent Fan Control Disabled."
        }

		state.fan_control_enabled = false
	}

	if (settings.app_enabled) {
		if(state.app_enabled == false) {
			send("${app.label} Enabled.")
        } else {
        	log.debug "${app.label} Enabled."
        }

		subscribe(humidity_sensor, "humidity", "handleThings")

		state.app_enabled = true
	} else {
		if(state.app_enabled == true) {
			send("${app.label} Disabled.")
        } else {
        	log.debug "${app.label} Disabled."
        }

		state.app_enabled = false
    }

    handleThings()

}


def handleThings(evt) {

	log.debug "handleThings()"

	if(evt) {
		log.debug "$evt.descriptionText"
    }
        
	def h = 0.0 as BigDecimal
	if (settings.app_enabled) {
	    h = settings.humidity_sensor.currentValue('humidity')
	}

	log.debug "Humidity: $h%, Activate: $humidity_a%, Deactivate: $humidity_d%"

    def activateFans = false
    def deactivateFans = false
    
	if (settings.app_enabled) {
        
		if (state.fansOn) {
            if (h > humidity_d) {
                log.debug "Humidity not sufficient to deactivate vent fans: $h > $humidity_d"
            } else {
                log.debug "Humidity sufficient to deactivate vent fans: $h <= $humidity_d"
                deactivateFans = true
            }
        } else {
            if (h < humidity_a) {
                log.debug "Humidity not sufficient to activate vent fans: $h < $humidity_a"
            } else {
                log.debug "Humidity sufficient to activate vent fans: $h >= $humidity_a"
                activateFans = true
            }
        }
	}

	if(activateFans) {
		set_fans(true)
    }
	if(deactivateFans) {
		set_fans(false)
    }
    
	
	def d =  input1 * input2;

	def f = 15
	
	def loc = getLocation()
	
    def curMode = loc.getCurrentMode()
    
	if(curMode == "Home" && input1 > humidity_d)
	{
		if(state.fansOn)
		{
			f = 20;
			setLocationMode("Home")
		}
	}
	
    if(input1 < humidity_a){
    	f = f -3
    }
    
    if(input2 < (humidity_a + humidity_d)){
    	d = d + 15
    	setLocationMode("Away")
    }    
    else {
    	f = f + 30
    }

	if(input1 < input2)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
	else
	{
		if(curMode == "Home" && d < 25)
		{
			sendSms( phone1, "normal" )
		}
		else
		{
			sendSms( phone1, "bad" )
			switch1.off();
		}
	}

}


def set_fans(fan_state) {

	if (fan_state) {
    	if (state.fansOn == false) {
            send("${app.label} fans On.")
            state.fansOnTime = now()
            if (settings.fan_control_enabled) {
                if (emeters) {
                    emeters.reset()
                }
                fans.on()
            } else {
                send("${app.label} fan control is disabled.")
            }
            state.fansOn = true
		} else {
            log.debug "${app.label} fans already On."
		}        
    } else {
    	if (state.fansOn == true) {
	    	send("${app.label} fans Off.")
            state.fansLastRunTime = (now() - state.fansOnTime)

		    BigInteger ms = new java.math.BigInteger(state.fansLastRunTime)
			int seconds = (BigInteger) (((BigInteger) ms / (1000I))                  % 60I)
			int minutes = (BigInteger) (((BigInteger) ms / (1000I * 60I))            % 60I)
			int hours   = (BigInteger) (((BigInteger) ms / (1000I * 60I * 60I))      % 24I)
			int days    = (BigInteger)  ((BigInteger) ms / (1000I * 60I * 60I * 24I))

			def sb = String.format("${app.label} cycle: %d:%02d:%02d:%02d", days, hours, minutes, seconds)
			
		    send(sb)

			if (settings.fan_control_enabled) {
    	    	fans.off()
                if (emeters) {
                    log.debug emeters.currentValue('energy')
                    emeters.poll() 
                    state.fansLastRunEnergy = emeters.currentValue('energy').sum()
                    state.fansLastRunCost = ((state.fansLastRunEnergy * price_kwh) / 100.0) 
                    send("${app.label} cycle: ${state.fansLastRunEnergy}kWh @ \$${state.fansLastRunCost}")
                }
	        } else {
    	    	send("${app.label} fan control is disabled.")
            }
	        state.fansOn = false
            state.fansHoldoff = now()
        } else {
            log.debug "${app.label} fans already Off."
        }
    }

}


private send(msg) {

	if (sendPushMessage) {
        sendPush(msg)
    }

    if (phone) {
        sendSms(phone, msg)
    }

    log.debug(msg)
}

