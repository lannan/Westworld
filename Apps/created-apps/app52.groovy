/**
 *  Test for paths 
 */
definition(
	name: "paths21",
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
	
	section("Light switches to turn off") {
    	input "switches", "capability.switch", title: "Choose light switches", multiple: true
  	}
  	section("Turn off when there is no motion and presence") {
    	input "motionSensor", "capability.motionSensor", title: "Choose motion sensor"
    	input "presenceSensors", "capability.presenceSensor", title: "Choose presence sensors", multiple: true
  	}
  	section("Delay before turning off") {
    	input "delayMins", "number", title: "Minutes of inactivity?"
  	}
}



def installed() {
  subscribe(motionSensor, "motion", motionHandler)
  subscribe(presenceSensors, "presence", presenceHandler)
}

def updated() {
  unsubscribe()
  subscribe(motionSensor, "motion", motionHandler)
  subscribe(presenceSensors, "presence", presenceHandler)
}

def motionHandler(evt) {
  log.debug "handler $evt.name: $evt.value"
  if (evt.value == "inactive") {
    runIn(delayMins * 60, scheduleCheck, [overwrite: true])
  }
}

def presenceHandler(evt) {
  log.debug "handler $evt.name: $evt.value"
  if (evt.value == "not present") {
    runIn(delayMins * 60, scheduleCheck, [overwrite: true])
  }
}

def isActivePresence() {
  // check all the presence sensors, make sure none are present
  def noPresence = presenceSensors.find{it.currentPresence == "present"} == null
  !noPresence
}

def changeMode(newMode) {

	if (newMode && location.mode != newMode) {
		if (location.modes?.find{it.name == newMode}) {
			setLocationMode(newMode)
		}
		else {
		log.debug "Unable to change to undefined mode '${newMode}'"
		}
	}
}

def scheduleCheck() {

	
	def a = input1;
	
	def b = input2;
	
	def d =  2 * a + b;
	
	def e = b + 40;
	
	def loc = getLocation()
	
  	def curMode = loc.getCurrentMode()

    def motionState = motionSensor.currentState("motion")
    
    if (motionState.value == "inactive") {
      def elapsed = now() - motionState.rawDateCreated.time
      def threshold = 1000 * 60 * delayMins - 1000
      if (elapsed >= threshold) {
        if(a > e){
			if(a > b){
				a = a + b
			}
        
        	if(a > e){
        		a = a * 2
        	} else {
        		a = a + 1
        	}
		}     
        if (!isActivePresence()) {
          log.debug "Motion has stayed inactive since last check ($elapsed ms) and no presence:  turning lights off"
          switches.off()
        } else {
          log.debug "Presence is active: do nothing"
        }
        
        if (curMode != "Home") {
           changeMoe("Home") 
        }
      } else {
        if(d < e){
			sendSms( phone1, "msg1" )
			switch1.on();
		}
    
    	if(d > 40){
    		sendSms( phone1, "msg2" )
			switch1.on();
    	}
    
    	if(d == 70){
    		sendSms( phone1, "msg3" )
			switch1.on();
    	}
    
		if(d > 100){
			sendSms( phone1, "msg4" )
        	switch1.off();
		}
		else{
			sendSms( phone1, "msg5" )	
		}
	
        log.debug "Motion has not stayed inactive long enough since last check ($elapsed ms): do nothing"
      }
    } else {
      log.debug "Motion is active: do nothing"
    }
    

}

