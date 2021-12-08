/**
 *  Test for paths 
 */
definition(
	name: "paths11",
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

	def f = 5
	
	if(e > 15)
	{
		if(d>e)
		{
			f = 20;
		}
	}
    else{
    	d = d + 20
    }
	
    
    if(e < 60){
    	e + 30
    }
    else{
    	e = e/2
    }
    
    if(d < 25){
    	a = 5
    	b = 5
    }
    
    else{
    	a = a - 5
    	b = b + a
    }
    
    
	if(d > 15)
	{
		if(a>e)
		{
			d = 25;
		}
	}
	
	if(b >= 20)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
    
    if(a >= 20){
    	sendSms( phone1, "msg2" )
		switch1.on();
    }
	else
	{
		if(d < 70)
		{
			sendSms( phone1, "msg3" )
		}
		else
		{
			sendSms( phone1, "msg4" )
			switch1.off();
		}
	}
	
	
}
