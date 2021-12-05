/**
 *  Light Up The Night
 *
 *  Author: SmartThings
 */
definition(
	name: "Light Up The Night",
	namespace: "mcowger",
	author: "SmartThings",
	description: "SmartThings",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Monitor the luminosity...") {
		input "lightSensor", "capability.illuminanceMeasurement"
	}
	section("Turn on a light...") {
		input "lights", "capability.switch"
	}
}

def installed() {
	subscribe(lightSensor, "illuminance", illuminanceHandler)
}

def updated() {
	unsubscribe()
	subscribe(lightSensor, "illuminance", illuminanceHandler)
}

// New aeon implementation
def illuminanceHandler(evt) {
	def lastStatus = lights.latestValue("switch")
    def lumin = evt.integerValue
	if (lastStatus != "on" ) {
		if(lumin < 30){
			lights.on()

		} else {
			if (lastStatus == "on" ) {			
				log.debug "lumin is enough, last stateus is on, no action needed."
			}
		}
	}
	else{ 
		if (lastStatus != "off" ) {
			if(lumin > 50){
				lights.off()
			}
		}
	}
}
