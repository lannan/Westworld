/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths25",
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
	
	def e = d + 30;

	
	if(a > d){
    
		if(a > 100){
			a = a / 2;
		}
        if(a > 200){
			a = a / 3;
		}
        if(a > 300){
			a = a / 4;
		}
        if(a > 400){
			a = a / 5;
		}
        if(a == 450){
			a = 50;
		}
        
	}
	
	if(e > 200){
    	e = e - 200
	}
    else{
    	e = e + 100
    }
	
	if(d > 50){
		sendSms( phone1, "msg1" )
		switch1.on();
	}
    if(e > 100){
		sendSms( phone1, "msg2" )
		switch1.on();
	}
    if(e > 150){
    	if(d > 150){
			sendSms( phone1, "msg3" )
			switch1.on();
        }
	}
	else{
		if(a < 25){
			sendSms(phone1, "msg4")
		}
		else{
			sendSms(phone1, "msg5")
			switch1.off();
		}
	}
	
	
}
