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
	section( "Open the blinds at what time of day?" ) {
		input "openTime", "enum", title: "Time to let in sunlight?", metadata:[values:["Morning","Afternoon"]], required:true
	}
	
	section("Blinds to control?") {
		input "blinds", "capability.switch"
	}
	
	section("Lux sensor?") {
		input "light1", "capability.illuminanceMeasurement"
	}
	section ("Zip code (optional, defaults to location coordinates)...") {
		input "zipCode", "text", required: false
	}
}

def installed() {
	subscribe(light1, "illuminance", plantHandler)
}

def updated() {
	unsubscribe()
	subscribe(light1, "illuminance", plantHandler)
}



def plantHandler(evt){
	String time = openTime
	def luxLevel = 0.0

	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)

	def now = new Date()
	def riseTime = s.sunrise
	def setTime = s.sunset

	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(now() > riseTime.time && curMode == "Home")
	{
			blinds.off()
	}
			
	if(time == "Morning"){
		log.debug "Time is morning."
		luxLevel = 400.0
	}

	if(time == "Afternoon")
	{
		log.debug "Time is afternoon."
		luxLevel = 1000.0
	}


	if(evt.doubleValue >= luxLevel) {
		log.debug "It is time to open the blinds."
		blindsStatus = true
	}
	
	if(evt.doubleValue < luxLevel) {
		log.debug "The lux level is too low."
		blindsStatus = false
	}
	
	
	if(blindsStatus == false) {
		log.debug "Closing the blinds."
		blinds.off()
	}
	
	if(blindsStatus == true) {
		log.debug "Opening the blinds."
		blinds.on()
	}
	
}
