/**
 *  Test for paths with systemAPI
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
	section("Monitor the humidity of:") {
		input "humiditySensor1", "capability.relativeHumidityMeasurement"
	}
	section("input1:") {
		input "input1", "number", title: "integer ?"
		input "input2", "number", title: "integer ?"
		input "newMode", "mode", title: "Change mode to?"
	}
	section( "Notifications" ) {
		input "phone1", "phone", title: "Send a Text Message?", required: false
	}
	section("Control this switch:") {
		input "switch1", "capability.switch", required: true
	}
	section ("Zip code (optional, defaults to location coordinates)...") {
		input "zipCode", "text", required: false
	}
}

def installed() {
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def updated() {
	unsubscribe()
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def humidityHandler(evt) {
	
        def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)

	def now = new Date()
	def riseTime = s.sunrise
	def setTime = s.sunset
	
	def e = input1 + 10;
   
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(now() > riseTime.time)
	{
		switch1.on()
		if (newMode && curMode != newMode) {
			if (location.modes?.find{it.name == newMode}) {
				setLocationMode(newMode)
			}
		}
	}
	else{
		d = d + 20
	}
	
	
	if(d > e && now() < setTime.time)
	{
		switch1.off()
		changeMode()
	}
	
}
