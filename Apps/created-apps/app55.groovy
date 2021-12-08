/**
 *  Test for paths 
 */
definition(
	name: "paths25",
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
	section("Control this switch:") {
		input "switch1", "capability.switch", required: true
	}
	
	section("When the humidity rises above:") {
		input "humidityHigh", "number", title: "Percentage ?"
	}
    section("When the humidity drops below:") {
		input "humidityLow", "number", title: "Percentage ?"
	}
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone1", "phone", title: "Send a Text Message?", required: false
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
	log.trace "humidity: $evt.value"
    log.trace "set high point: $humidityHigh"
    log.trace "set low point: $humidityLow"

	def currentHumidity = Double.parseDouble(evt.value.replace("%", ""))
	def humidityHigh1 = humidityHigh 
    def humidityLow1 = humidityLow 
	def mySwitch = settings.switch1
	
    def a = input1;
	
	def b = input2;
	
	def d =  b * 4;
	
	def e = d + 30;

	if (currentHumidity >= humidityHigh1) {
		log.debug "Checking how long the humidity sensor has been reporting >= $humidityHigh1"

		if(a > d){
    
		if(a > 100){
			a = a / 2;
		}
        if(a > 200){
			a = a / 3;
		}
        if(a > 300){
			a = a / 4;
		}
        if(a > 400){
			a = a / 5;
		}
        if(a == 450){
			a = 50;
		}      
		}
		def deltaMinutes = 10 
		def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
		def recentEvents = humiditySensor1.eventsSince(timeAgo)
		log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaMinutes minutes"
		def alreadySentSms1 = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) >= humidityHigh1 } > 1

		if (alreadySentSms1) {
			log.debug "Notification already sent within the last $deltaMinutes minutes"

		} else {
         	if (state.lastStatus != "off") {
                log.debug "Humidity Rose Above $humidityHigh1:  sending SMS and deactivating $mySwitch"
                send("${humiditySensor1.label} sensed high humidity level of ${evt.value}, turning off ${switch1.label}")
                switch1?.off()
                state.lastStatus = "off"
            }
		}
	}
    else if (currentHumidity <= humidityLow1) {
		log.debug "Checking how long the humidity sensor has been reporting <= $humidityLow1"

		if(d > 50){
			sendSms( phone1, "msg1" )
			switch1.on();
		}
    	if(e > 100){
			sendSms( phone1, "msg2" )
			switch1.on();
		}
   	 	if(e > 150){
    		if(d > 150){
				sendSms( phone1, "msg3" )
				switch1.on();
        	}
		}
		else{
			if(a < 25){
				sendSms(phone1, "msg4")
			}
			else{
				sendSms(phone1, "msg5")
				switch1.off();
			}
		}
		def deltaMinutes = 10 
		def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
		def recentEvents = humiditySensor1.eventsSince(timeAgo)
		log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaMinutes minutes"
		def alreadySentSms2 = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) <= humidityLow1 } > 1

		if (alreadySentSms2) {
			log.debug "Notification already sent within the last $deltaMinutes minutes"

		} else {
        	if (state.lastStatus != "on") {
                log.debug "Humidity Dropped Below $humidityLow1:  sending SMS and activating $mySwitch"
                send("${humiditySensor1.label} sensed low humidity level of ${evt.value}, turning on ${switch1.label}")
                switch1?.on()
                state.lastStatus = "on"
            }
		}
	}
    else {
    	//log.debug "Humidity remained in threshold:  sending SMS to $phone1 and activating $mySwitch"
		//send("${humiditySensor1.label} sensed humidity level of ${evt.value} is within threshold, keeping on ${switch1.label}")
    	//switch1?.on()
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

