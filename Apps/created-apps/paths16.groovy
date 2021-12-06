/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths16",
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
	
	def d =  a / b;
	
	def e = d + 10;

	def f = a * b
    
	if(a == b)
	{
		if(d > f)
		{
			d = d - 20
		}
	}
	
	if(a > b)
	{
		if(a>e)
		{
			d = 25;
		}
        
        if(b > e){
        	d = d + 15
        }
        
        else{
        	d = d * 5
        }
	}
	
	if(d > 20)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
    
    if(d < 30){
    	switch1.on()
    }
	else
	{
		if(a < 39)
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
