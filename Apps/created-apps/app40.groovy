/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths9",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)


preferences {
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
	
	section("Turn off when there's movement..."){
		input "motion1", "capability.motionSensor", title: "Where?", multiple: true
	}
	section("And on when there's been no movement for..."){
		input "minutes1", "number", title: "Minutes?"
	}
	section("Turn off/on light(s)..."){
		input "switches", "capability.switch", multiple: true
	}
	
	section ("Zip code (optional, defaults to location coordinates)...") {
		input "zipCode", "text", required: false
	}
	section ("newMode") {
		input "newMode", "mode", title: "Change mode to?"
	}	
}


def installed()
{
	subscribe(motion1, "motion", motionHandler)
	schedule("0 * * * * ?", "scheduleCheck")
}

def updated()
{
	unsubscribe()
	subscribe(motion1, "motion", motionHandler)
	unschedule()
	schedule("0 * * * * ?", "scheduleCheck")
}

def motionHandler(evt) {
	log.debug "$evt.name: $evt.value"

	if (evt.value == "active") {
		log.debug "turning on lights"
		switches.off()
		state.inactiveAt = null
	} else if (evt.value == "inactive") {
		if (!state.inactiveAt) {
			state.inactiveAt = now()
		}
	}
	
	
	def e = input1 + 10;
   
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if (newMode && curMode != newMode) {
			if (location.modes?.find{it.name == newMode}) {
				setLocationMode(newMode)
			}
		}
	}
    
    if(e == 30){
    	if(getLocation().getCurrentMode() == "Home"){
        	sendSms( phone1, "good" )
			switch1.on();
        }
    } else {
    	sendSms( phone1, "bad" )
		switch1.off();
    }
}

def scheduleCheck() {
	log.debug "schedule check, ts = ${state.inactiveAt}"
	if (state.inactiveAt) {
		def elapsed = now() - state.inactiveAt
		def threshold = 1000 * 60 * minutes1
		if (elapsed >= threshold) {
			log.debug "turning off lights"
			switches.on()
			state.inactiveAt = null
		}
		else {
			log.debug "${elapsed / 1000} sec since motion stopped"
		}
	}
}

