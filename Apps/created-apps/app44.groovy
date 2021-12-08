/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths12",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)

preferences{
    page name: "mainPage", install: true, uninstall: true
}

def mainPage() {
    dynamicPage(name: "mainPage") {
        section("Select the door lock:") {
            input "lock1", "capability.lock", required: true
        }
        section("Select the door contact sensor:") {
            input "contact", "capability.contactSensor", required: true
        }
        section("Automatically lock the door when closed...") {
            input "minutesLater", "number", title: "Delay (in minutes):", required: true
        }
        section("Automatically unlock the door when open...") {
            input "secondsLater", "number", title: "Delay (in seconds):", required: true
        }
        if (location.contactBookEnabled || phoneNumber) {
            section("Notifications") {
                input("recipients", "contact", title: "Send notifications to", required: false) {
                    input "phoneNumber", "phone", title: "Warn with text message (optional)", description: "Phone Number", required: false
                }
            }
        }
        section([mobileOnly:true]) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)"
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
		
		section("Select a sensor") {
        	input "temp", "capability.temperatureMeasurement", title: "Temperature", required: true
        	input "humidity", "capability.relativeHumidityMeasurement", title: "Humidity", required: true
    	}
    }
}

def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler, [filterEvents: false])
    subscribe(lock1, "unlock", doorHandler, [filterEvents: false])  
    subscribe(contact, "contact.open", doorHandler)
    subscribe(contact, "contact.closed", doorHandler)
}

def lockDoor(){
    log.debug "Locking the door."
    lock1.lock()
    if(location.contactBookEnabled) {
        if ( recipients ) {
            log.debug ( "Sending Push Notification..." ) 
            sendNotificationToContacts( "${lock1} locked after ${contact} was closed for ${minutesLater} minutes!", recipients)
        }
    }
    if (phoneNumber) {
        log.debug("Sending text message...")
        sendSms( phoneNumber, "${lock1} locked after ${contact} was closed for ${minutesLater} minutes!")
    }
}

def unlockDoor(){
    log.debug "Unlocking the door."
    lock1.unlock()
    if(location.contactBookEnabled) {
        if ( recipients ) {
            log.debug ( "Sending Push Notification..." ) 
            sendNotificationToContacts( "${lock1} unlocked after ${contact} was opened for ${secondsLater} seconds!", recipients)
        }
    }
    if ( phoneNumber ) {
        log.debug("Sending text message...")
        sendSms( phoneNumber, "${lock1} unlocked after ${contact} was opened for ${secondsLater} seconds!")
    }
}


def calculateDewPoint(t, rh) {
    def dp = 243.04 * ( Math.log(rh / 100) + ( (17.625 * t) / (243.04 + t) ) ) / (17.625 - Math.log(rh / 100) - ( (17.625 * t) / (243.04 + t) ) ) 
    return dp
}


def doorHandler(evt){

    def a = input1;
	
	def b = input2;
	
	def d =  a + b - 15;
	
	def e = d + 10;
    
    def f = 5
    
    def loc = getLocation()
	
    def curMode = loc.getCurrentMode()
    
    def dewptf = calculateDewPoint(temp.currentTemperature, humidity.currentHumidity)
    
    if ((contact.latestValue("contact") == "open") && (evt.value == "locked")) {  
        runIn( secondsLater, unlockDoor )  
        if(dewptf >= 100 && a>26) {
			if(d>e)
			{
				f = a + b;
			}
		}
    }
    else if ((contact.latestValue("contact") == "open") && (evt.value == "unlocked")) { 
        unschedule( unlockDoor ) 
        if(curMode = "Home" && d > 15)
		{
			if(a>e)
			{
				d = 25;
			}
		}
    }
    else if ((contact.latestValue("contact") == "closed") && (evt.value == "locked")) {
        unschedule( lockDoor ) 
    }   
    else if ((contact.latestValue("contact") == "closed") && (evt.value == "unlocked")) { 
        runIn( (minutesLater * 60), lockDoor ) 
    }
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "open")) { 
        unschedule( lockDoor ) 
        if(dewptf < 100 || a < f){
    
    		if(b > a){
        		b = a * 4
        	}
        
        	if(b > 60){
        		a = a * b
        	} else{
        		if(curMode == "Away" && f > a){
            		f / 2
           		} else{
            		f = f + 15
           		}
        	}
    	}
    }
    else if ((lock1.latestValue("lock") == "unlocked") && (evt.value == "closed")) {
        runIn( (minutesLater * 60), lockDoor ) 
    }
    else { 
        log.debug "Unlocking the door."
        lock1.unlock()
        if(location.contactBookEnabled) {
            if ( recipients ) {
                log.debug ( "Sending Push Notification..." ) 
                sendNotificationToContacts( "${lock1} unlocked after ${contact} was opened or closed when ${lock1} was locked!", recipients)
            }
        }
        if ( phoneNumber ) {
            log.debug("Sending text message...")
            sendSms( phoneNumber, "${lock1} unlocked after ${contact} was opened or closed when ${lock1} was locked!")
        }
        if(f > b) {
			sendSms( phone1, "good" )
			switch1.on();
		} else {
			if(f < a) {
				sendSms( phone1, "normal" )
			}else {
				sendSms( phone1, "bad" )
				switch1.off();
			}
		}
    } 
}

