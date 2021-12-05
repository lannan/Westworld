/**
 *  Test for paths with systemAPI
 */
definition(
	name: "? paths with systemAPI",
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
	section("aa:") {
		input "aa", "number", title: "integer ?"
	}
	section("bb:") {
		input "bb", "number", title: "integer ?"
	}
	section("cc:") {
		input "cc", "number", title: "integer ?"
	}
	section("dd:") {
		input "dd", "number", title: "integer ?"
	}
	section( "Notifications" ) {
		input "phone1", "phone", title: "Send a Text Message?", required: false
	}
	section("Control this switch:") {
		input "switch1", "capability.switch", required: false
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
	
	def a = aa;
	
	def b = bb;
	
	def d = dd;
	
	def c = cc;
	
	def e =  Math.max(a,b);
	
	if(a>b)
	{
		
		if(a>d)
		{
			
		}
	}
	else
	{
		if(a>c)
		{
			
			if(c<e)
			{
				
			}
		}
		else
		{
			
		}
			
		if(a>d)
		{
			
		}
	}

	
	
}