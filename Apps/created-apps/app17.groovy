/**
 *  Test app
 */
 
 definition(
	name: "? paths",
	namespace: "tests",
	author: "boubou",
	description: "Test app",
    category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)


preferences {
	section("When the door opens") {
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Turn off a light") {
		input "switch1", "capability.switch"
	}
	
	section("input1:") {
		input "input1", "number", title: "integer ?"
	}
	
	section("Control this switch:") {
		input "switch1", "capability.switch", required: true
	}
}

def installed() {
	subscribe(contact1, "contact", contactHandler)
}

def updated() {
	unsubscribe()
	subscribe(contact1, "contact", contactHandler)
}

def contactHandler(evt) {
	log.debug "$evt.value"
	if (evt.value == "open") {
        state.wasOn = switch1.currentValue("switch") == "on"
		switch1.off()
	}	

	if (evt.value == "closed") {
		if(state.wasOn)switch1.on()
	}
	
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(curMode == "Home")
	{
		switch1.on();
	}	
}

