/**
 *  cookingThermometer
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
	name: "cookingThermometer2",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "This app sends you a text when your water has reached a temperature.",
	category: "",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Monitor which sensor?") {
		input "cookingSensor1", "capability.temperatureMeasurement"
}
	section( "Notifications" ) {
	input "phone1", "phone", title: "Send a Text Message?"
}
 section("Send message once it reaches which temperature?"){
		input "tempGoal", "number", title: "Temperature?"
	}
}

def installed() {
	subscribe(cookingSensor1, "temperature", tempHandler)
}

def updated() {
	unsubscribe()
	subscribe(cookingSensor1, "temperature", tempHandler)
}


def tempHandler(evt){
	def tempa = evt.integerValue
	def goal = tempGoal
	def boiling = 100   
	
	if(tempa > goal) {
		if(tempa > 100) {

		}
		else {

		}
	}
	
	
	
	 if(tempa < 0) {

	}
	
	 if(tempa < goal) {

	}
	
  


}