/**
 *  Test apps
 */
 
 definition(
	name: "? paths",
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
	section("When the humidity rises above:") {
		input "humidity1", "number", title: "Percentage ?"
	}
    section("When the humidity falls below:") {
		input "humidity2", "number", title: "Percentage ?"
	}
	
	section("input1:") {
		input "input1", "number", title: "integer ?"
	}
	section("input2:") {
		input "input2", "number", title: "integer ?"
	}
	
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone1", "phone", title: "Send a Text Message?", required: false
    }
	section("Control this switch:") {
		input "switch1", "capability.switch", required: false
	}
}

def installed() {
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def updated() {
	unsubscribe()
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def humidityHandler(evt) {
	log.trace "humidity: ${evt.value}"
    log.trace "set point: ${humidity1}"

	def currentHumidity = Double.parseDouble(evt.value.replace("%", ""))
	def tooHumid = humidity1 
    def notHumidEnough = humidity2
	def mySwitch = settings.switch1
	def deltaMinutes = 10 
    
    def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
	def recentEvents = humiditySensor1.eventsSince(timeAgo)
	log.trace "Found ${recentEvents?.size() ?: 0} events in the last ${deltaMinutes} minutes"
	def alreadySentSms = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) >= humidity1 } > 1 || recentEvents.count { Double.parseDouble(it.value.replace("%", "")) <= humidity2 } > 1
    
	if (currentHumidity >= tooHumid) {
		log.debug "Checking how long the humidity sensor has been reporting >= ${tooHumid}"

		// Don't send a continuous stream of text messages
		


		if (alreadySentSms) {
			log.debug "Notification already sent within the last ${deltaMinutes} minutes"
			
		} else {
			log.debug "Humidity Rose Above ${tooHumid}:  sending SMS and activating ${mySwitch}"
			send("${humiditySensor1.label} sensed high humidity level of ${evt.value}")
			switch1?.on()
		}
	}

    if (currentHumidity <= notHumidEnough) {
		log.debug "Checking how long the humidity sensor has been reporting <= ${notHumidEnough}"

		if (alreadySentSms) {
			log.debug "Notification already sent within the last ${deltaMinutes} minutes"
			
		} else {
			log.debug "Humidity Fell Below ${notHumidEnough}:  sending SMS and activating ${mySwitch}"
			send("${humiditySensor1.label} sensed high humidity level of ${evt.value}")
			switch1?.off()
		}
	}
	
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	def f = input1 - input2;
	
	if(input1 > f)
	{
		if((curMode == "Home") && (f==10))
		{
			f = 20;
		}
	}
	
	if((f == 20) || curMode == "Away")
	{
		sendSms( phone1, "good" )
		switch1?.on();
	}
	
}


private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phone1 ) {
        log.debug( "sending text message" )
        sendSms( phone1, msg )
    }

    log.debug msg
}

