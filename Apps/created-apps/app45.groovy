/**
 *  Test for paths
 */
 
definition(
	name: "paths15",
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
	
	section("At this time every day") {
    	input "time", "time", title: "Time of Day"
  	}
  	section("Make sure this is locked") {
    	input "lock","capability.lock"
  	}
  	section("Make sure it's closed first..."){
    	input "contact", "capability.contactSensor", title: "Which contact sensor?", required: false
  	}
  	section( "Notifications" ) {
    	input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes", "No"]], required: false
    	input "phone", "phone", title: "Send a text message?", required: false
  	}
}


def installed() {
  schedule(time, "setTimeCallback")

}

def updated(settings) {
  unschedule()
  schedule(time, "setTimeCallback")
}

def setTimeCallback() {
  if (contact) {
    doorOpenCheck()
  } else {
    lockMessage()
    lock.lock()
  }
}
def doorOpenCheck() {
    def a = input1;
	
	def b = input2;
	
	def d =  a + b;
	
	def e = b + 10;


  def currentState = contact.contactState
  if (currentState?.value == "open") {
    def msg = "${contact.displayName} is open.  Scheduled lock failed."
    log.debug msg
    if (sendPushMessage) {
    	if(a>b)
		{
			if(d>e)
			{
				e = 20;
			}
		}
      sendPush msg
    }
    if (phone) {
      sendSms phone, msg
      if(a >= 50){
    	sendSms( phone1, "good3" )
		switch1.on();
      }
	  else {
		if(b > a)
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
  } else {
    lockMessage()
    lock.lock()
  }
}

def lockMessage() {
  def msg = "Locking ${lock.displayName} due to scheduled lock."
  log.debug msg
  if (sendPushMessage) {
    sendPush msg
  }
  if (phone) {
    sendSms phone, msg
  }
}
	
