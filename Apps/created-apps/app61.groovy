/**
 *  Test for paths
 */
definition(
	name: "? paths",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)



preferences {
	section("Blinds to control?") {
		input "blinds", "capability.switch", multiple: true
	}
	
	section("Illuminance sensor to watch?") {
		input "light1", "capability.illuminanceMeasurement"
	}
		
}

def installed() {
	
	subscribe(light1, "illuminance", activateBlinds)
	

}

def updated(settings) {
	unsubscribe()
	subscribe(light1, "illuminance", activateBlinds)
	
}




def activateBlinds(evt) {
	def impossibleVal = 0
	boolean raiseBlinds = false
	int blindsState = evt.integerValue
	
	log.debug "evt.value: ${blindsState} "
 
	if(blindsState > 10) {
		raiseBlinds = true
	}
	
	if(blindsState < 10){
		raiseBlinds = false
	}
	
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(curMode != "Home" && blindsState < impossibleVal){
		blinds.off()
	}
	
	
	if(curMode == "Home" && raiseBlinds){
		blinds.on()
	}
	
	
	

}
