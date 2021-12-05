/**
 *  Push Me When There's Motion
 *
 *  Author: andre.xavier@bhs.com.br
 *  Date: 2013-10-10
 */
definition(
	name: "Brighten My Path",
	namespace: "",
	author: "SmartThings",
	description: "aaa",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)
preferences {
	section("When movement is detected...") {
		input "motionSensor", "capability.motionSensor", title: "Where?"
	}
	section("Send this message (optional, sends standard status message if not specified)"){
		input "messageText", "text", title: "Message Text", required: false
	}
}

def installed() {
	subscribe(motionSensor, "motion.active", motionActiveHandler)
}

def updated() {
	unsubscribe()
	subscribe(motionSensor, "motion.active", motionActiveHandler)
}

def motionActiveHandler(evt) {

	def msg = "Motion detected on $motionSensor sensor!"
    sendPush(msg)
	log.debug "$motionSensor has moved"
}