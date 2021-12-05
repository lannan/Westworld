/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths10",
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
	
	def d =  a * b;
	
	def e = d + 10;
    
    def f = 1

	
	if(a>b)
	{
		if(d>e)
		{
			f = 20;
		}
	}
	
	if(d > 15)
	{
		if(a>e)
		{
			d = 25;
		}
	}
    
    if(d >= 100){
    	if(a == 5){
        	a = a % 2
        }
        
        if(a < 80){
        	a = a / 2
        }
        
        else{
        	b = b + a
        }
    }
	
    if(f <= a){
    	f = a + b
    }
    
    if(f < d){
    	f = f + 5
    }
    else{
    	f = f - 5
    }
    
	if(d == 20)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
    
    if(b > 20){
    	sendSms( phone1, "good2" )
		switch1.on();
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
			switch1.off();
		}
	}
	
	
}
