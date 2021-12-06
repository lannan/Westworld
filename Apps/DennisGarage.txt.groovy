/**
 *  Turn It On When I'm Here
 *
 *  Author: SmartThings
 */
definition(
	name: "Turn It On When I'm Here",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When I arrive ..."){
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
	}
	section("Monem..."){
		input "switch1", "capability.switch", multiple: true
	}
	section("prescenceValue"){
		input "aboolean", "bool", title: "input a bool"
	}
}

def installed()
{
	subscribe(presence1, "presence", presenceHandler)
}

def updated()
{
	unsubscribe()
	subscribe(presence1, "presence", presenceHandler)
}

def presenceHandler(evt)
{
	log.debug "presenceHandler $evt.name: $evt.value"
	def current = presence1.currentValue("presence")

	def presenceValue = aboolean

	if(presenceValue){
		switch1.on()

	}
	else{
		switch1.off() 
	}
}
