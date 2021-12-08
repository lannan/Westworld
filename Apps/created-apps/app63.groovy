/**
 *  Test for paths
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
	section("When the door opens/closes...") {
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Turn on/off a light...") {
		input "switch1", "capability.switch"
	}
	section("input1:") {
		input "input1", "number", title: "integer ?"
	}
    section("input2:") {
		input "input2", "number", title: "integer ?"
	}
}

def installed()
{
	subscribe(contact1, "contact", contactHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact", contactHandler)
}

def contactHandler(evt) {
	
	def f = Math.max(input1,input2);
	
	for(int i = 0; i<5;i++) 
	{
		f = f + i*5;
	}
	
	
	log.debug "$evt.value: $evt"
	if (f>55 && evt.value == "open") {
		switch1.on(delay: 5000)
	} 
	if(evt.value!="open"){
		if(evt.value == "closed"){
			switch1.off(delay: 5000)
		}
	}
}
