/**
 *  Lock It When It Closes
 *
 *  Author: Jeremy R. Whittaker
 */
definition(
	name: "Lock It When It Closes",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When the door closes..."){
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Lock the lock...") {
		input "lock1","capability.lock", multiple: true
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
	if (evt.value == "closed") {
		lock1.lock()
        log.debug "door locked"
	}
}

