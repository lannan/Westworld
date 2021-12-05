/**
 *  Test for paths with systemAPI
 */
definition(
	name: "? paths",
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
	
	def e =  aa;
	
	int flag = 10;
	
	if(a>b)
	{
		if(a>c)
		{
			flag = flag + 10;
		}
		else
		{
			flag = flag - 10;
		}
		
		if(a>d)
		{
			flag = flag + 10;
			if(d<e)
			{
				flag = flag + 5;
			}
		}
	}
	else
	{
		if(a>c)
		{
			flag = flag + 5;
			if(c<e)
			{
				flag = flag + 5;
			}
		}
		else
		{
			flag = flag - 10;
		}
			
		if(a>b)
		{
			flag = flag + 5;
		}
	}
	
	if(flag == 20)
	{
		sendSms( phone1, "good" )
	}
	else
	{
		sendSms( phone1, "bad" )
	}
	
	
}
