/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths19",
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
	
	def d =  b * 4;
	
	def e = d - 10;

	def f = 0
	
	if(a<b)
	{
		if(d>e)
		{
			f = f * 2;
		}
	}
    
    if(a>b){
    	if(d<e){
        	f = f / 2
        }
        
        if(a>d){
        	f = f + 20
        }
        
        else{
        	f = f - 15
        }
        	
    }
	
	if(a > d)
	{
		if(d>40)
		{
			d = d - 35;
		}
        
        else{
        	d = d - 10
        }
	}
	
	if(a == 15)
	{
		sendSms( phone1, "good" )
		switch1.on();
	}
    
    if(a == 10)
	{
		sendSms( phone1, "good2" )
		switch1.on();
	}
    
	else
	{
		if(a == b)
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
