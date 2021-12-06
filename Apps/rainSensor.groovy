/**
 *  Rain Sensor
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
 
 
 //This app contains requirements 1.1.2, 1.2.1, and 1.2.2
definition(
	name: "Rain Sensor",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "This app checks to see if a sensor is wet in order to determine if it is currently raining.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
section("When water is detected") {
	input "alarm", "capability.waterSensor", title: "Where?"
}

section("Control this switch:") {
	input "switch1", "capability.switch", required: true
}
																														
}


def installed() {

initialize()
}

def updated() {
unsubscribe()
initialize()
}

def initialize() {
	subscribe(alarm, "water", waterHandler)
}


def waterHandler(evt) {
	
	 def waterState = alarm.currentValue("water");

	handleMoistureUpdate(waterState)

}


def handleMoistureUpdate(water){
	
	def switchState = switch1?.currentValue("switch")
	
	if(water == "wet"){
		log.debug "It is raining!"
		
		if(switchState != "on") {
		log.debug "Activating $switch1"
		switch1?.on()
	}
	}else{
		log.debug "The sensor is dry. It is not currently raining."
		
		if(switchState != "off") {
		log.debug "Deactivating $switch1"
		switch1?.off()
		}
	}
}