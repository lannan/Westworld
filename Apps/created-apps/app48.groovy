/**
 *  Test for paths 
 */
definition(
	name: "paths18",
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
	
	def d =  a * 2;
	
	def e = d / 6;

	
	if(a > 15)
	{
		if(d>e)
		{
			e = e + 15;
		}
	}
	
	if(d > 18)
	{
		if(a>b)
		{
			d = d / 2;
		}
	}
    
    if(a < b){
    	a = a * 2
    }
	
    if(a > b){
   		b = b * 2
    }
    
    if(b > d){
    	d = d / 2
    }
    
    else{
    	a = a + 16
    }
    
    
	if(b > a)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
    
    if(b < a){
		sendSms( phone1, "msg2" )
		switch1.on();
	}
    
    if(a > d){
    	sendSms( phone1, "msg3" )
		switch1.on();
    }
    
	else
	{
		if(a < 60)
		{
			sendSms( phone1, "normal" )
		}
		else
		{
			sendSms( phone1, "normal2" )
			switch1.off();
		}
	}
	
	
}
