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
	
	if(a > currentHumidity)
	{
		if(d > e)
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
	
	if(d == 20)
	{
		sendSms( phone1, "good" )
		coffee.on();
	}
	else
	{
		if(d == 25)
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

