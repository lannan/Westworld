/**
 *  Light Up The Night
 *
 *  Author: SmartThings 
 *  ADW added support for lumin value
 *      and off value will be lumin + 100 
 */

// Automatically generated. Make future change here.
definition(
    name: "Let There Be Dark!",
    namespace: "Dooglave",
    author: "Dooglave",
    description: "Turn your lights off when a Contact Sensor is opened and turn them back on when it is closed, ONLY if the Lights were previouly on",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png")

preferences {
	section("Monitor the luminosity...") {
		input "lightSensor", "capability.illuminanceMeasurement"
	}
	section("Turn on a light...") {
		input "lights", "capability.switch"
	}
    section("For how dark...") {
		input "lumins", "number", title: "Illuminance?"
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
	def lumins2 = lumins
	def lumins3 = lumins + 100

    def lumin = evt.integerValue
	if (lastStatus != "on" ) {
		if(lumin < lumins3){
				lights.on()
		}
	}
	else{
		if (lastStatus != "off" ){
			if(lumin > lumins2){
				lights.off()
			}
		}
	}
}
