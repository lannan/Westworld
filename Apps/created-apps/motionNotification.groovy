/**
 *  Motion Notification
 *
 *  Copyright 2019 Steven Maxwell
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
	name: "Motion Notification",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "Sends a text when a sensor detects motion.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

//This app contains requirements 1.2.2.3, 1.2.3.1
preferences {
	section("Monitor Which Motion Sensor?") {
		input "accelerationSensor", "capability.accelerationSensor", title: "Where?"
	}
	section("Alert which phone?") {
		input("recipients", "contact", title: "Send notifications to") {
			input "phone1", "phone", title: "Phone number?"
		}
	}
}

def installed() {
	subscribe(accelerationSensor, "acceleration.active", accelerationHandler)
}

def updated() {
	unsubscribe()
	subscribe(accelerationSensor, "acceleration.active", accelerationHandler)
}



def sendAlert() {
	boolean alertSent = false;
	log.debug "accelerationSensor has moved, sending text message"
	sendSms(phone1, "Warning, the sensor has detected movement")
	alertSent = true;

}

def accelerationHandler(evt) {

	sendAlert()
		
	
}