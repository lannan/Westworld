/**
 *  Virtual Garage Triggers Outlet
 *
 *  Author: chrisb
 */
definition(
	name: "Virtual Garage Triggers Outlet",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When a Virtual Garage Door is tapped..."){
		input "GarageSensor1", "capability.contactSensor", title: "Which?"
	}
	section("Trigger which outlet?"){
		input "switches", "capability.switch"
	}
}


def installed()
{
	subscribe(GarageSensor1, "contact.open", contactOpenHandler)
}

def updated()
{
	unsubscribe()
	subscribe(GarageSensor1, "contact.open", contactOpenHandler)
}

def contactOpenHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "Turning on switches: $switches"
	switches.on()
    log.trace "Turning off switches: $switches"
    switches.off(delay: 4000)
}

