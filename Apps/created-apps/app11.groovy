/**
 *  Test apps
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
    page (name:"configActions")
}

def configActions() {
    dynamicPage(name: "configActions", title: "Configure Actions", uninstall: true, install: true) {
        section ("When this person") {
            input "person", "capability.presenceSensor", title: "Who?", multiple: false, required: true
        }
        section ("Still at home past") {
            input "timeOfDay", "time", title: "What time?", required: true
        }

		section("Control this switch:") {
			input "input1", "number", title: "integer ?"
			input "input2", "number", title: "integer ?"
			input "switch1", "capability.switch", required: true
		}
		
        def phrases = location.helloHome?.getPhrases()*.label
        if (phrases) {
            phrases.sort()
            section("Perform this action") {
                input "wfhPhrase", "enum", title: "\"Hello, Home\" action", required: true, options: phrases
            }
        }

        section (title: "More options", hidden: hideOptions(), hideable: true) {
            input "sendPushMessage", "bool", title: "Send a push notification?"
            input "phone", "phone", title: "Send a Text Message?", required: false
            input "days", "enum", title: "Set for specific day(s) of the week", multiple: true, required: false,
                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        }

        section([mobileOnly:true]) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
        }
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def initialize() {
    schedule(timeToday(timeOfDay, location?.timeZone), "checkPresence")
    if (customName) {
      app.setTitle(customName)
    }
}

def checkPresence() {
    if (daysOk && modeOk) {
        if (person.latestValue("presence") == "present") {
            log.debug "${person} is present, triggering WFH action."
            location.helloHome.execute(settings.wfhPhrase)
            def message = "${location.name} executed '${settings.wfhPhrase}' because ${person} is home."
            send(message)
        }
    }
    
    if (inputs1 > input2) {
    	switch1.on()
    }
}

private send(msg) {
    if (sendPushMessage != "No") {
        sendPush(msg)
    }

    if (phone) {
        sendSms(phone, msg)
    }

    log.debug msg
}

private getModeOk() {
    def result = !modes || modes.contains(location.mode)
    result
}

private getDaysOk() {
    def result = true
    if (days) {
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
        }
        def day = df.format(new Date())
        result = days.contains(day)
    }
    result
}

private hideOptions() {
    (days || modes)? false: true
}


