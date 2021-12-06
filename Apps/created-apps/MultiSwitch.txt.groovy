/**
 *  Multi Switch
 *
 *  Author: SmartThings
 */
definition(
	name: "Multi Switch",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When this switch is toggled...") {
		input "master", "capability.switch", title: "Where?"
	}
	section("Turn on/off these switches...") {
		input "switches", "capability.switch", multiple: true
	}
}

def installed()
{
  	subscribe(master, "switch.On", switchOn)
    subscribe(master, "switch.Off", switchOff)
}

def updated()
{
	unsubscribe()
	subscribe(master, "switch.On", switchOn)
    subscribe(master, "switch.Off", switchOff)
}

def switchOn(evt) {
	log.debug "Switches On"
        switches?.on()
}
def switchOff(evt) {
        def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(curMode == "Home" ){
		log.debug "Switches Off"
        	switches?.off()
        }
}

