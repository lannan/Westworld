/**
 *  Darken Behind Me
 *
 *  Author: SmartThings
 */
definition(
	name: "Darken Behind Me",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	section("When there's no movement...") {
		input "motion1", "capability.motionSensor", title: "Where?"
	}
	section("Turn off a light...") {
		input "switch1", "capability.switch"
	}
}

def installed()
{
	subscribe(motion1, "motion.inactive", motionInactiveHandler)
}

def updated()
{
	unsubscribe()
	subscribe(motion1, "motion.inactive", motionInactiveHandler)
}

def motionInactiveHandler(evt) {
	switch1.off()
}