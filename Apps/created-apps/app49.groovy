/**
 *  Test for paths
 */
definition(
	name: "paths17",
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
	section("My Medicine Draw/Cabinet"){
		input "deviceContactSensor", "capability.contactSensor", title: "Opened Sensor" 
	} 

    section("Remind me to take my medicine at"){
        input "reminderTime", "time", title: "Time"
    }

    section("My LED Light"){
    	input "deviceLight", "capability.colorControl", title: "Smart light"
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

    subscribe(deviceContactSensor, "contact", contactHandler)

    state.minutesToCheckOpenDraw = 60

    state.ledNotificationTriggered = false
    
    schedule(reminderTime, checkOpenDrawInPast)
   
}


def contactHandler(evt){
    def a = input1;
	
	def b = input2;
	
	def d =  a * b;
	
	def e = d + 10;
	
	
	if (evt.value == "open") {
        log.debug "Cabinet opened"
        if (state.ledNotificationTriggered) {
            resetLEDNotification()
        }
	}
	
	if(a>b){
    	a = b + 9
	}
	
	else{
		if(a>e){
			a = b + 15;
		}
        else{
        	a = b + 50
        }
	}
	
}


def checkOpenDrawInPast(){
	log.debug "Checking past 60 minutes of activity from $reminderTime"

    def cabinetOpened = isOpened(state.minutesToCheckOpenDraw)
	log.debug "Cabinet found opened: $cabinetOpened"

    if (!cabinetOpened) {    
    	sendNotification("Hi, please remember to take your meds in the cabinet")
 
        def reminderTimePlus10 = new Date(now() + (10 * 60000))

        runOnce(reminderTimePlus10, checkOpenDrawAfterReminder)
    }
}


def checkOpenDrawAfterReminder(){
	log.debug "Checking additional 10 minutes of activity from $reminderTime"

    def cabinetOpened = isOpened(10)    
    
   	log.debug "Cabinet found opened: $cabinetOpened"

    if (!cabinetOpened) {
    	log.debug "Set LED to Notification color"
        setLEDNotification()
    }
    
}


def sendNotification(msg){
        log.debug "Message Sent: $msg"
        sendPush(msg)
}


def isOpened(minutes){ 
    def previousDateTime = new Date(now() - (minutes * 60000))

    def evts = deviceContactSensor.eventsSince(previousDateTime)   
    def cabinetOpened = false
    if (evts.size() > 0) {
        evts.each{
            if(it.value == "open") {
                cabinetOpened = true 
            }
        }
	}
	
	if(input1 > 20)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
	else
	{
		if(input1 > 50)
		{
			sendSms( phone1, "normal" )
		}
		else
		{
			sendSms( phone1, "bad" )
			switch1.off();
		}
	}
    
    return cabinetOpened
}


def setLEDNotification(){

	state.ledNotificationTriggered = true
 	state.ledState = deviceLight.currentValue("switch")  
    state.origColor = deviceLight.currentValue("hue")
    deviceLight.on()
    deviceLight.setHue(100)
    
    log.debug "LED set to RED. Original color stored: $state.origColor"

}


def resetLEDNotification(){

	state.ledNotificationTriggered = false

    log.debug "Reset LED color to: $state.origColor"
    if (state.origColor != null) {
    	deviceLight.setHue(state.origColor)
    }

    if (state.ledState == "off") {
    	deviceLight.off()
    }

}
