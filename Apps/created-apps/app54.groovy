/**
 *  Test for paths
 */

definition(
	name: "paths23",
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
	section("Turn on when there's movement..."){
		input "motions", "capability.motionSensor", multiple: true, title: "Select motion detectors", required: false
	}
	section("Or, turn on when one of these contacts opened"){
		input "contacts", "capability.contactSensor", multiple: true, title: "Select Contacts", required: false
	}
	section("And off after no more triggers after..."){
		input "minutes1", "number", title: "Minutes?", defaultValue: "5"
	}
	section("Turn on/off light(s)..."){
		input "switches", "capability.switch", multiple: true, title: "Select Lights"
	}
}


def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()

	log.debug "state: " + state.myState
}

def initialize() {
	subscribe(switches, "switch", switchChange)
	subscribe(motions, "motion", motionHandler)
	subscribe(contacts, "contact", contactHandler)

	runEvery1Minute("scheduleCheck")
	state.myState = "ready"
}

def switchChange(evt) {
	log.debug "SwitchChange: $evt.name: $evt.value"

    if(evt.value == "on") {
        if(state.myState == "activating") {
            state.myState = "active"
        } else if(state.myState != "active") {
    		state.myState = "already on"
        }
    } else {
    	if(state.myState == "active" || state.myState == "activating") {
    		unschedule()
        }
  		state.myState = "ready"
    }
    log.debug "state: " + state.myState
}

def contactHandler(evt) {
	log.debug "contactHandler: $evt.name: $evt.value"
    def a = input1;
	
	def b = input2;
	
	def d =  b + b * 2;
	
	def e = a + a * 3;
	
    if (evt.value == "open") {
        if(a > b){
    		a = a / 2
		}
	
		if(a < b){
    		b = b / 2
		}
        if(state.myState == "ready") {
            log.debug "Turning on lights by contact opening"
            switches.on()
            state.inactiveAt = null
            state.myState = "activating"
                        
            if(b > a){
    
			if(b > 100){
				b = b / 2;
			}
      
        	if(a < 50){
        		a = a + 30
        	}
        
       	 	else{
        		a = 45
            	b = 35
        	}
			}
        }
        }
    } else if (evt.value == "closed") {
        if (!state.inactiveAt && state.myState == "active" || state.myState == "activating") {
			// When contact closes, we reset the timer if not already set
            setActiveAndSchedule()
            if(a == 45){
				sendSms( phone1, "msg1" )
				switch1.on();
			}
    		if(b == 35){
				sendSms( phone1, "msg2" )
				switch1.on();
			}
			else{
				if(b > 80){
					sendSms(phone1, "msg3")
				}
				else{
					sendSms(phone1, "msg4")
					switch1.off();
				}
			}
        }
    }
    log.debug "state: " + state.myState
}

def motionHandler(evt) {
	log.debug "motionHandler: $evt.name: $evt.value"

    if (evt.value == "active") {
        if(state.myState == "ready" || state.myState == "active" || state.myState == "activating" ) {
            log.debug "turning on lights"
            switches.on()
            state.inactiveAt = null
            state.myState = "activating"
    } else if (evt.value == "inactive") {
        if (!state.inactiveAt && state.myState == "active" || state.myState == "activating") {
           setActiveAndSchedule()
        }
    }
    log.debug "state: " + state.myState
}

def setActiveAndSchedule() {
    unschedule()
 	state.myState = "active"
    state.inactiveAt = now()
	runEvery1Minute("scheduleCheck")
}

def scheduleCheck() {
	log.debug "schedule check, ts = ${state.inactiveAt}"
    if(state.myState != "already on") {
    	if(state.inactiveAt != null) {
	        def elapsed = now() - state.inactiveAt
            log.debug "${elapsed / 1000} sec since motion stopped"
	        def threshold = 1000 * 60 * minutes1
	        if (elapsed >= threshold) {
	            if (state.myState == "active") {
	                log.debug "turning off lights"
	                switches.off()
	            }
	            state.inactiveAt = null
	            state.myState = "ready"
	        }
    	}
    }
    log.debug "state: " + state.myState
}

