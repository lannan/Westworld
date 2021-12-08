/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths4",
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
	
	section("When the door state changes") {
        paragraph "Send a SmartThings notification when the coop's door jammed and did not close."
		input "doorSensor", "capability.doorControl", title: "Select CoopBoss", required: true, multiple: false            
        input("recipients", "contact", title: "Recipients", description: "Send notifications to") {
        	input "phone", "phone", title: "Phone number?", required: true}
	}
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(doorSensor, "doorState", coopDoorStateHandler)
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def coopDoorStateHandler(evt) {
	if (evt.value == "jammed"){
        def msg = "WARNING ${doorSensor.displayName} door is jammed and did not close!"
        log.debug "WARNING ${doorSensor.displayName} door is jammed and did not close, texting $phone"

        if (location.contactBookEnabled) {
            sendNotificationToContacts(msg, recipients)
        }
        else {
            sendPush(msg)
            if (phone) {
                sendSms(phone, msg)
            }
        }
	} 

	def currentHumidity = humiditySensor1.currentHumidity
	
	def deltaMinutes = 10 
	
	def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
	
	def recentEvents = humiditySensor1.eventsSince(timeAgo)
	
	log.trace "Found ${recentEvents?.size() ?: 0} events in the last ${deltaMinutes} minutes"
	
	def alreadySentSms = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) >= input1 } > 1 || recentEvents.count { Double.parseDouble(it.value.replace("%", "")) <= input2 } > 1
	
	def loc = getLocation()
	
	def curMode = loc.getCurrentMode()
	
	def weather = getTwcConditions(zipCode) 
    
    if (weather) currentHumidity = weather.obs.relativeHumidity
      
	
	def d =  input1 * input2;
	
	def e = d + 10;

	
	if(alreadySentSms && input1 > currentHumidity)
	{
		if(d > e)
		{
			f = 20;
		}
	} else {
    	c = c/2
    }
    
    
	if(curMode == "Home" && d > 15)
	{
		if(input1 > e)
		{
			d = 25;
		}
        
        
	}
	
	if(curMode == "Home" && d == 20)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
	else
	{
		if(d == 25)
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

