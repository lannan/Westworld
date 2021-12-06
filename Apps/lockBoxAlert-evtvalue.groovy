/**
 *  lockBoxAlert
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
 
 //This app contains requirements: 1.2.2.1, 1.2.2.5, 1.2.3.2
definition(
	name: "lockBoxAlert",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "This app sends an alert if a secure box or drawer is opened.",
	category: "",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	  section("Monitor which container?") {
		input "contact1", "capability.contactSensor"
}
	section("Notifications") {
		input "phone1", "phone", title: "Phone Number?"
}

}

def installed() {
	subscribe(contact1, "contact", contactHandler)
}

def updated() {
	unsubscribe()
	subscribe(contact1, "contact", contactHandler)
}


def buildMessage(String str1, String str2){
	StringBuilder stringBuilder = new StringBuilder()
	 stringBuilder.append(str1)
	stringBuilder.append(str2)
	
	return stringBuilder.toString()
}


def contactHandler(evt) {

	def msg = "message"
	String contactV = contact1.latestValue
	
	
	if(contactV == "open"){
		log.debug "The container has been opened."
		msg = buildMessage("Alert, the ", "container has been opened.")
		sendSms(phone1, msg)
	}
   
	else if(contactV == "closed"){
		log.debug "The container just closed."
		msg = "Warning, did you leave the container open? It has just closed."
		sendSms(phone1, msg)
	}
	
	
	
}