/**
 *  Test for paths 
 */
definition(
	name: "paths18",
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
	section("Choose Locks") {
		input "lock1", "capability.lock", multiple: true
	}
    section("Enter User Code Number (This is not the code used to unlock the door)") {
    	input "distressCode", "number", defaultValue: "0"
    }
    section("Distress Message Details") {
    	input "phone1", "phone", title: "Phone number to send message to"
    	input "distressMsg", "text", title: "Message to send"
    }
    section("User Code Discovery Mode (Enable and unlock the door using desired code. A message will be sent containing the user code used to unlock the door.)") {
    	input "discoveryMode", "bool", title: "Enable"
    }
}

def installed() {
    subscribe(lock1, "lock", checkCode)
}

def updated() {
	unsubscribe()
    subscribe(lock1, "lock", checkCode)
}

def checkCode(evt) {
    log.debug "$evt.value: $evt, $settings"
    
    def a = input1;
	
	def b = input2;
	
	def d =  a * 2;
	
	def e = d / 6;

    if(evt.value == "unlocked" && evt.data) {
    	def lockData = new JsonSlurper().parseText(evt.data)
        if(a > 15) {
			if(d>e) {
				e = e + 15;
			}
		}
	
		if(d > 18) {
			if(a>b) {
				d = d / 2;
			}
		}
        if(discoveryMode) {
        	sendPush "Door unlocked with user code $lockData.usedCode"
        }
        
        if(lockData.usedCode == distressCode && discoveryMode == false) {
        	log.info "Distress Message Sent"
        	sendSms(phone1, distressMsg)
        }
        
        if(b > a) {
			sendSms( phone1, "good" )
			switch1.on();
		}
    
   	 	if(b < a){
			sendSms( phone1, "msg2" )
			switch1.on();
		}
    
    	if(a > d){
    		sendSms( phone1, "msg3" )
			switch1.on();
    	} else {
			if(a < 60) {
				sendSms( phone1, "normal" )
			} else {
				sendSms( phone1, "normal2" )
				switch1.off();
			}
		}
    }
}

