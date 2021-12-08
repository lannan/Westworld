/**
 *  Test for paths
 */
definition(
	name: "paths26",
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
	
	def d =  a + 19;
	
	def e = b + 10;

	
	if(b > a){
		a = a + 5
		if(d > e){
			a = a - 19
		}
	}
	
	if(d > 50){
	
		if(a > e){
			a = a + 60
		}
	}
	
	if(a == 80){
		sendSms( phone1, "msg1" )
		switch1.on()
	}
	
	if(a < 200){
		sendSms( phone1, "msg2" )
		switch1.on()
	}
	
	else{
		sendSms( phone1, "msg3" )
		switch1.off()
	}

}
