/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths23",
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
	
	def e = a * 3;

	
	if(e > b){
		b = b - 40;
	}
	else{
    	b = b + 5
    }
    
    
	if(b > a){
    
		if(b > 100){
			b = b / 2;
		}
        
        if(a < 50){
        	a = a + 30
        }
        
        else{
        	a = 45
            b = 35
        }
	}
	
	if(a == 45){
		sendSms( phone1, "msg1" )
		switch1.on();
	}
    if(b == 35){
		sendSms( phone1, "msg2" )
		switch1.on();
	}
	else{
		if(b > 80){
			sendSms(phone1, "msg3")
		}
		else{
			sendSms(phone1, "msg4")
			switch1.off();
		}
	}
	
	
}
