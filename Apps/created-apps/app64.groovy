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
	section("When I leave...") {
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
	}
	section("Lock the lock...") {
		input "lock1","capability.lock", multiple: true
		input "unlock", "enum", title: "Unlock when presence is detected?", options: ["Yes","No"]
        input("recipients", "contact", title: "Send notifications to") {
            input "spam", "enum", title: "Send Me Notifications?", options: ["Yes", "No"]
        }
	}
	section("prescence2"){
		input "prescence2", "bool", title: "input a bool"
	}
	section("number0"){
		input "number0", "number", title: "input a number"
	}
	section("number1"){
		input "number1", "number", title: "input a number"
	}
	
	section("Monitor the humidity...") {
		input "humidity", "capability.relativeHumidityMeasurement", title: "Humidity", required: true
	}
	section("Turn on a light...") {
		input "lights", "capability.switch"
	}
	
	section("input1:") {
		input "input1", "number", title: "integer ?"
	}
	
	section("When water is detected") {
		input "alarm", "capability.waterSensor", title: "Where?"
	}
}

def installed()
{
	subscribe(presence1, "presence", presence)
}

def updated()
{
	unsubscribe()
	subscribe(presence1, "presence", presence)
}


def presence(evt)
{
	def a = evt.value
	def b = unlock
	
	def curhumidity = humidity.currentHumidity
	
	def water = alarm.currentValue("water")

	def switchState = switch1?.currentValue("switch")
	
	if(water == "wet"){
		log.debug "It is raining!"
		
		if(switchState != "on") {
			log.debug "Activating $switch1"
			switch1?.on()
		}
	} else {
		log.debug "The sensor is dry. It is not currently raining."
		
		if(switchState != "off") {
		log.debug "Deactivating $switch1"
		switch1?.off()
		}
	}
	
	
	if (curhumidity > input1 &&  a == "present") {
		if (b == "Yes") {
			def anyLocked = number1
			if (anyLocked == 0) {
				
				lock1.unlock()
				
			}
		}
	}
	else {
		def nobodyHome = prescence2
		if (nobodyHome) {
			def anyUnlocked = number0
			if (anyUnlocked == 0) {
				lock1.unlock()
			}
		}
	}
}


