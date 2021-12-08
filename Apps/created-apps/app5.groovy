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
		input "delay", "number", title: "Polling delay (minutes):"
	}
	section( "Notifications" ) {
		input "phone1", "phone", title: "Send a Text Message?", required: false
	}
	section("Control this switch:") {
		input "switch1", "capability.switch", required: true
	}
}

def installed() {
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def updated() {
	unsubscribe()
	subscribe(humiditySensor1, "humidity", humidityHandler)
}

def foo (a, b) {
    if (a > b) {
        return a - b
    } else
    	return b - a 
}

def humidityHandler(evt) {
	
	def currentHumidity = Double.parseDouble(evt.value.replace("%", ""))

	def timeAgo = new Date(now() - (1000 * 60 * delay).toLong())
	def recentEvents = humiditySensor1.eventsSince(timeAgo)
	def alreadySentSms1 = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) >= humidityHigh1 } > 1
	
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	def teminput1 = input1 * 3 - input2
	def teminput2 = foo(input1, input2)
	
	if(alreadySentSms1 || teminput1 > currentHumidity)
	{
		if (curMode == "Home") {
			switch1.on();
		}
	}
	
	if(!alreadySentSms1 || teminput2 < 20)
	{
		if (curMode == "Away") { 
			sendSms( phone1, "good" )
			switch1.off();
		}
	}
	
}
