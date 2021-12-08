/**
 *  Test for paths 
 */
definition(
	name: "paths27",
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
    section("Select the door lock:") {
        input "lock1", "capability.lock", required: true
    }
    section("Select the door contact sensor:") {
    	input "contact1", "capability.contactSensor", required: true
    }
	section("Automatically lock the door when closed...") {
        input "minutesLater", "number", title: "Delay (in minutes):", required: true
    }
    section("Automatically unlock the door when open...") {
        input "secondsLater", "number", title: "Delay (in seconds):", required: true
    }
	section( "Push notification?" ) {
		input "sendPushMessage", "enum", title: "Send push notification?", metadata:[values:["Yes", "No"]], required: false
   	}
    section( "Text message?" ) {
    	input "sendText", "enum", title: "Send text message notification?", metadata:[values:["Yes", "No"]], required: false
       	input "phoneNumber", "phone", title: "Enter phone number:", required: false
   	}
}

def installed()
{
    initialize()
}

def updated()
{
    unsubscribe()
    unschedule()
    initialize()
}

def initialize()
{
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler, [filterEvents: false])
    subscribe(lock1, "unlock", doorHandler, [filterEvents: false])  
    subscribe(contact1, "contact.open", doorHandler)
	subscribe(contact1, "contact.closed", doorHandler)
}

def lockDoor()
{
	if (lock1.latestValue("lock") == "unlocked")
    	{
    	log.debug "Locking $lock1..."
    	lock1.lock()
        log.debug ("Sending Push Notification...") 
    	if (sendPushMessage != "No") sendPush("$lock1 locked after $contact1 was closed for $minutesLater minute(s)!")
    	log.debug("Sending text message...")
		if ((sendText == "Yes") && (phoneNumber != "0")) sendSms(phoneNumber, "$lock1 locked after $contact1 was closed for $minutesLater minute(s)!")
        }
	else if (lock1.latestValue("lock") == "locked")
    	{
        log.debug "$lock1 was already locked..."
        }
}

def unlockDoor()
{
	if (lock1.latestValue("lock") == "locked")
    	{
    	log.debug "Unlocking $lock1..."
    	lock1.unlock()
        log.debug ("Sending Push Notification...") 
    	if (sendPushMessage != "No") sendPush("$lock1 unlocked after $contact1 was open for $secondsLater seconds(s)!")
    	log.debug("Sending text message...")
		if ((sendText == "Yes") && (phoneNumber != "0")) sendSms(phoneNumber, "$lock1 unlocked after $contact1 was open for $secondsLater seconds(s)!")        
        }
	else if (lock1.latestValue("lock") == "unlocked")
    	{
        log.debug "$lock1 was already unlocked..."
        }
}

def doorHandler(evt)
{
    def a = input1;
	
	def b = input2;
	
	def d =  a * 3;
	
	def e = b / 2;
	
	def loc = getLocation()
	
    def curMode = loc.getCurrentMode()
	
    if ((contact1.latestValue("contact") == "open") && (evt.value == "locked"))
    	{
        def delay = secondsLater
        runIn (delay, unlockDoor)
        if(a >= 80){
    
		if(curMode == "Home" && a > b){
        	a = a * 2
		}
        else{
        	a = a / 2
        }
		}
    	}
    else if ((contact1.latestValue("contact") == "open") && (evt.value == "unlocked"))
    	{
        unschedule (unlockDoor)
		}
    else if ((contact1.latestValue("contact") == "closed") && (evt.value == "locked"))
    	{
        unschedule (lockDoor)
    	}   
    else if ((contact1.latestValue("contact") == "closed") && (evt.value == "unlocked"))
    	{
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
        def delay = (minutesLater * 60)
        runIn (delay, lockDoor)
    	}
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "open"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	}
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "closed"))
    	{
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
        def delay = (minutesLater * 60)
        runIn (delay, lockDoor)
    	}
	else if ((lock1.latestValue("lock") == "locked") && (evt.value == "open"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	if(curMode == "Home" && d > e){
    
		if(d == 40){
			d = d + 25;
		}
        
        if(d < 20){
        	d = d - 25
        }
        
        else{
        	d = e + 5
        }
		}
    	}
    else if ((lock1.latestValue("lock") == "locked") && (evt.value == "closed"))
    	{
        unschedule (lockDoor)
        log.debug "Unlocking $lock1..."
    	lock1.unlock()
    	}
    else
    	{
        log.debug "Problem with $lock1, the lock might be jammed!"
        unschedule (lockDoor)
        unschedule (unlockDoor)
    	}
    	
    if(a == 70){
		sendSms(phone1, "msg2")
		switch1.on()
	}
    
    if(a >= 75){
		sendSms(phone1, "msg3")
		switch1.on()
	}
	else{
		if(e == 50){
			sendSms(phone1, "msg4")
		}
		else{
			sendSms(phone1, "msg5")
			switch1.off()
		}
	}	
}

