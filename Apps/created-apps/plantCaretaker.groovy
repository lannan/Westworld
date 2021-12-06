/**
 *  plantCaretaker
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
 
 //This app contains requirements: 1.1.2(4 If blocks, 2 Else If blocks), 1.2.1, 1.2.2.1, 1.2.3.1
definition(
	name: "plantCaretaker",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "This app controls the blinds to give plants their daily dose of sunlight.",
	category: "",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section( "Open the blinds at what time of day?" ) {
		input "openTime", "enum", title: "Time to let in sunlight?", metadata:[values:["Morning","Afternoon"]], required:true
	}
	
	section("Blinds to control?") {
		input "blinds", "capability.switch"
	}
	
	section("Lux sensor?") {
		input "light1", "capability.illuminanceMeasurement"
}
}

def installed() {
	subscribe(light1, "illuminance", plantHandler)
}

def updated() {
	unsubscribe()
	subscribe(light1, "illuminance", plantHandler)
}



def plantHandler(evt){
	String time = openTime
	def luxLevel = 0.0

	
	
	if(time == "Morning"){
		log.debug "Time is morning."
		luxLevel = 400.0
	}
	


	if(time == "Afternoon")
	{
		log.debug "Time is afternoon."
		luxLevel = 1000.0
	}


//	if(evt.doubleValue >= luxLevel) {
//		log.debug "It is time to open the blinds."
//		blindsStatus = true
//	}
//	
//	if(evt.doubleValue < luxLevel) {
//		log.debug "The lux level is too low."
//		   blindsStatus = false
//	}
	
//	
//	if(blindsStatus == false) {
//		log.debug "Closing the blinds."
//		blinds.off()
//	}
//	
//	if(blindsStatus == true) {
//		log.debug "Opening the blinds."
//		blinds.on()
//	}
	
}