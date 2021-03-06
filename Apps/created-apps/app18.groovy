/**
 *  Test for paths with systemAPI
 */
definition(
	name: "paths22",
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
	
	def d =  55;
	
	def e = 30;

	
	if(a>d){
	
		if(a > 55){
			d = d + b;
		}
		
		if(a < 50){
			d = d + a
		}
		
		else{
			a = a + 50
		}
	}
	
	if(b > e){
	
		if(b > 35){
			b = b + e;
		}
		
		if(b > 40){
			b = b + d
		}
		
		else{
			b = b + 50
		}
	}
	
	if(a == 60){
		sendSms( phone1, "msg1" )
		switch1.on();
	}
	if(a == 70){
		sendSms( phone1, "msg2" )
		switch1.on();
	}
	
	
	
}
