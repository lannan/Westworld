/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths15",
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
	}
	section("input2:") {
		input "input2", "number", title: "integer ?"
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

def humidityHandler(evt) {
	
	def a = input1;
	
	def b = input2;
	
	def d =  a + b;
	
	def e = b + 10;

	
	if(a>b)
	{
		if(d>e)
		{
			e = 20;
		}
	}
	
	if(e > 22)
	{
		if(a>b)
		{
			a = a / 2;
		}
	}
	
	if(a == 20)
	{
		sendSms( phone1, "good1" )
		switch1.on();
	}
    
    if(a > 40){
    	sendSms( phone1, "good2" )
		switch1.on();
    }
    
    if(a >= 50){
    	sendSms( phone1, "good3" )
		switch1.on();
    }
	else
	{
		if(b > a)
		{
			sendSms( phone1, "normal" )
		}
		else
		{
			sendSms( phone1, "bad" )
			switch1.off();
		}
	}
	
	
}
