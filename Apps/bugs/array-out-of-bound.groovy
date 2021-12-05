/**
 *  Turn It On When I'm Here
 *
 *  Author: SmartThings
 */
definition(
	name: " Turn It On When I'm Here",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When I arrive and leave..."){
		input "presence1", "capability.presenceSensor", title: "Who?"
	}
	section("Turn on/off a light..."){
		input "switch1", "capability.switch"
	}

        section {
            app(name: "childApps", appName: "Child App", namespace: "mynamespace", title: "New Child
        }
}


def installed()
{
	subscribe(presence1, "presence", presenceHandler)
}

def updated()
{
	unsubscribe()
	subscribe(presence1, "presence", presenceHandler)
}

def presenceHandler(evt)
{
	def children = getChildApps()
        def child = children[3]

	def current = evt.value
	if (current == "present") {
		settings.switch1.on()
	}
	else{ 
		if (current == "not present") {
			settings.switch1.off()
		}
	} 
}



