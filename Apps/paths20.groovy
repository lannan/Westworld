/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths20",
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
	
	def d =  a * a;
	
	def e = b * 2;

	
	if(b > a)
	{
		if(d>e)
		{
			a = 10;
		}
	}
	
	if(d < 36) {
		if(a>b){
			d = d / a;
		}
        
        else{
        	d = d / 2
        }
	}
    
    if(e > 6){
    	e = b
    }
    
    else{
    	e = e * 2
    }
    
    
    if(b > q){
    	b = b % 2
    }
    
    else{
    	b = b * 2
    }
	
	if(d >= e)
	{
		sendSms( phone1, "msg1" )
		switch1.on();
	}
    
    if(d <= e)
	{
		sendSms( phone1, "msg2" )
		switch1.on();
	}
    
    if(d == e)
	{
		sendSms( phone1, "msg3" )
		switch1.on();
	}
	else
	{
		if(a < 15)
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
