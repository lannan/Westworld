/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths27",
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
	
	def d =  a * 3;
	
	def e = b / 2;

	
	if(a >= 80){
    
		if(a > b){
        	a = a * 2
		}
        else{
        	a = a / 2
        }
	}
	
	if(d > e){
    
		if(d == 40){
			d = d + 25;
		}
        
        if(d < 20){
        	d = d - 25
        }
        
        else{
        	d = e + 5
        }
	}
	
	if(a == 60){
		sendSms(phone1, "msg1")
		switch1.on()
	}
    
    if(a == 70){
		sendSms(phone1, "msg2")
		switch1.on()
	}
    
    if(a >= 75){
		sendSms(phone1, "msg3")
		switch1.on()
	}
	else{
		if(e == 50){
			sendSms(phone1, "msg4")
		}
		else{
			sendSms(phone1, "msg5")
			switch1.off()
		}
	}
	
	
}
