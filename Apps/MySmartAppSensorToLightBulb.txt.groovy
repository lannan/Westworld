/**
 *  Let There Be Light!
 *  Turn your lights on when an open/close sensor opens and off when the sensor closes.
 *
 *  Author: SmartThings
 */
definition(
	name: " Let There Be Light!",
	namespace: "Light Control",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("When the door opens/closes...") {
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Turn on/off a light...") {
		input "switch1", "capability.switch"
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
		switch1.on()
	} else{ 
		if (evt.value == "closed") {
			switch1.off()
		}
	}
}
