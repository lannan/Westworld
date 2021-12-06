/**
 *  Thermostat Auto Home
 *
 *  Author: sidjohn1@gmail.com
 *  Date: 2013-07-23
 */

// Automatically generated. Make future change here.
definition(
    name: "Thermostat Auto Home",
    namespace: "sidjohn1",
    author: "sidjohn1@gmail.com",
    description: "Simply marks any thermostat home after someone arrives. Great for Nest",
    category: "Green Living",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home1-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home1-icn@2x.png",
    oauth: false
)

preferences {

	section("When one of these people arrive at home") {
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true, required: true
	}
	section("Set this thermostat staus to home") {
		input "thermostat1", "capability.thermostat", title: "Which?", multiple: true, required: true
    }
	section("prescence1"){
		input "prescence1", "bool", title: "input a boolean"
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

	def presenceValue = prescence1
	
	if(presenceValue){
    	
	}
}
