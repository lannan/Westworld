/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths1",
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
	
	section("People to watch for?") {
    input "people", "capability.presenceSensor", multiple: true
  }

  section("Front Door?") {
    input "sensors", "capability.contactSensor", multiple: true
  }

  section("Hall Light?") {
    input "lights", "capability.switch", title: "Switch Turned On", multilple: true
  }

  section("Presence Delay (defaults to 30s)?") {
    input name: "presenceDelay", type: "number", title: "How Long?", required: false
  }

  section("Door Contact Delay (defaults to 10s)?") {
    input name: "contactDelay", type: "number", title: "How Long?", required: false
  }
}


def installed() {
  init()
  subscribe(humiditySensor1, "humidity", humidityHandler)
}

def updated() {
  unsubscribe()
  init()
}

def init() {
  state.lastClosed = now()
  subscribe(people, "presence.present", presence)
  subscribe(sensors, "contact.open", doorOpened)
}

def presence(evt) {
  def delay = contactDelay ?: 10

  state.lastPresence = now()

  if(now() - (delay * 1000) < state.lastContact) {
    log.info('Presence was delayed, but you probably still want the light on.')
    lights?.on()
  }
  
  def loc = getLocation()
	
  def curMode = loc.getCurrentMode()
	
  if(contactDelay == input1 * input2){
    	sendSms( phone1, "good" )
		switch1.on();  
  }
  else 
  {
	if (curMode == "Home") {
		sendSms( phone1, "bad" )
		switch1.off();
	}
  }
	
}

def doorOpened(evt) {
  def delay = presenceDelay ?: 30

  state.lastContact = now()

  if(now() - (delay * 1000) < state.lastPresence) {
    log.info('Welcome home!  Let me get that light for you.')
    lights?.on()
  }
}


