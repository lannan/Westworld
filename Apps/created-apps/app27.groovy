/**
 *  Test for paths with systemAPI
 */
definition(
	name: "? paths3",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)

preferences {
    section("About") {
        paragraph "This app is designed simply to turn on your coffee machine " +
            "while you are taking a shower."
    }
	section("Bathroom humidity sensor") {
		input "bathroom", "capability.relativeHumidityMeasurement", title: "Which humidity sensor?"
	}
    section("Coffee maker to turn on") {
    	input "coffee", "capability.switch", title: "Which switch?"
    }
    section("Humidity level to switch coffee on at") {
    	input "relHum", "number", title: "Humidity level?", defaultValue: 50
    }
    
    section("input1:") {
		input "input1", "number", title: "integer ?"
	}
	section("input2:") {
		input "input2", "number", title: "integer ?"
	}
	section( "Notifications" ) {
		input "phone1", "phone", title: "Send a Text Message?", required: false
	}
	
	section ("Zip code (optional, defaults to location coordinates)...") {
		input "zipCode", "text", required: false
	}
	
}

def installed() {
	subscribe(bathroom, "humidity", coffeeMaker)
}

def updated() {
	unsubscribe()
	subscribe(bathroom, "humidity", coffeeMaker)
}

def coffeeMaker(shower) {

	def a = input1;
	
	def b = input2;
	
	def d =  a * b;
	
	def e = d + 10;

	def currentHumidity = shower.value.toInteger()
	
	def deltaMinutes = 10 
	
	def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
	
	def recentEvents = humiditySensor1.eventsSince(timeAgo)
	
	log.trace "Found ${recentEvents?.size() ?: 0} events in the last ${deltaMinutes} minutes"
	
	def alreadySentSms = recentEvents.count { Double.parseDouble(it.value.replace("%", "")) >= tooHumid } > 1 || recentEvents.count { Double.parseDouble(it.value.replace("%", "")) <= notHumidEnough } > 1
	
	def loc = getLocation()
	
	def curMode = loc.getCurrentMode()
	
	def weather = getTwcConditions(zipCode) 
    
    if (weather) currentHumidity = weather.obs.relativeHumidity
    
	
	if(alreadySentSms && a > currentHumidity)
	{
		if(curMode == "Home" && d > e)
		{
			f = 20;
		}
	}
	
	if(d > 15)
	{
		if(currentHumidity > e)
		{
			d = 25;
		}
	}
	
	if(alreadySentSms && d == 20)
	{
		sendSms( phone1, "good" )
		coffee.on();
	}
	else
	{
		if(curMode == "Home" && d == 25)
		{
			sendSms( phone1, "normal" )
		}
		else
		{
			sendSms( phone1, "bad" )
			coffee.off();
		}
	}
	
	
	log.info "Humidity value: $shower.value"
	if (shower.value.toInteger() > relHum) {
		coffee.on()
    } 
}

