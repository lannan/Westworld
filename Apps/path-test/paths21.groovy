/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths21",
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
	
	def d =  2 * a + b;
	
	def e = b + 40;

	
	if(e == 41) {
		e = e + 15
	}
    
    else{
    	e = e - 9
    }
	
	if(a > e){
		if(a > b){
			a = a + b
		}
        
        if(a > e){
        	a = a * 2
        }
        
        else{
        	a = a + 1
        }
	}
    
    else{
    	a = a - b
    }
	
	if(d < e){
		sendSms( phone1, "msg1" )
		switch1.on();
	}
    
    if(d > 40){
    	sendSms( phone1, "msg2" )
		switch1.on();
    }
    
    if(d == 70){
    	sendSms( phone1, "msg3" )
		switch1.on();
    }
    
	if(d > 100){
		sendSms( phone1, "msg4" )
        switch1.off();
	}
	else{
		sendSms( phone1, "msg5" )	
	}
	
	
	
}
