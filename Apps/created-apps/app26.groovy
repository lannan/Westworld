/**
 *  Test for paths
 */
definition(
	name: "? paths",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)

preferences {
  section("People to watch for?") {
    input "people", "capability.presenceSensor", multiple: true
  }

  section("Monitor the humidity of:") {
		input "humiditySensor1", "capability.relativeHumidityMeasurement"
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
  
  section("input1:") {
	input "input1", "number", title: "integer ?"
  }
  section("input2:") {
	input "input2", "number", title: "integer ?"
  }
}

def installed() {
  init()
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

def foo (a, b) {
    if (a > b) {
        return a - b
    } else
    	return b - a 
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
	
	def teminput1 = input1 * 3 - input2
	def teminput2 = foo(input1, input2)
	
	def weather = getTwcConditions(zipCode) 
    def humidityval = input2
    
    if (weather) humidityval = weather.obs.relativeHumidity

	if(teminput1 > humidityval)
	{
		if((curMode == "Home") && (f==10))
		{
			switch1?.off();
		}
	}
	
	if(teminput2 < 20 || curMode == "Away")
	{
		sendSms( phone1, "good" )
		switch1?.on();
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

