/**
 *  Brighten My Path
 *
 *  Author: SmartThings
 */
definition(
	name: "Brighten My Path",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When there's movement...") {
		input "motion1", "capability.motionSensor", title: "Where?", multiple: true
		input "light1", "capability.illuminanceMeasurement"
	}
	section("Turn on a light...") {
		input "switch1", "capability.switch", multiple: true
	}
}

def installed()
{
	subscribe(motion1, "motion.active", motionActiveHandler)
}

def updated()
{
	unsubscribe()
	subscribe(motion1, "motion.active", motionActiveHandler)
}

def motionActiveHandler(evt) {
	def light = light1.latestValue("illuminance")
	def montionState = montion1.currentState("motion")
	def elapsed = noew() = motionState.rawDateCreated.time
	def threshold = 100
	if (light <= 10 && elapsed >= threshold) {

		switch1.on()
	
	}

	
}
