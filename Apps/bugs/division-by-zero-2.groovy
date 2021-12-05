
/**
 *  Delayed Command Execution
 *
 *  Author: SmartThings
 */
definition(
	name: "Delayed Command Execution",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When the door opens/closes...") {
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Turn on/off a light...") {
		input "switch1", "capability.switch"
	}
}

def installed()
{
	subscribe(contact1, "contact", contactHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact", contactHandler)
}

def contactHandler(evt) {
	log.debug "$evt.value: $evt"
        def num = state.mycount
        if (100/num < 1) {
		log.debug "num is larger than 100"
	}

	if (evt.value == "open") {
		switch1.on(delay: 5000)
		state.mycount = state.mycount + 1
	} 
	if(evt.value!="open"){
		if(evt.value == "closed"){
			switch1.off(delay: 5000)
		}
	}
}
