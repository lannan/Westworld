/**
 *  Heat Detector
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
	name: "Heat Detector2",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "Turns on the AC when it gets too hot.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

//This app contains requirements: 1.1.2(3 If Blocks), 1.2.1, 1.2.3.1
preferences {
	section("Monitor the temperature of which sensor?") {
		input "temperatureSensor1", "capability.temperatureMeasurement"
	}
	section("What temperature is too hot?") {
		input "temperature1", "number", title: "Temperature?"
	}
   
	section("Turn on which A/C?") {
		input "switch1", "capability.switch", required: true
	}
}

def installed() {
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
}

def updated() {
	unsubscribe()
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
}

def temperatureHandler(evt) {

	def tempat = evt.integerValue
	def tooHot = temperature1
	
	if(tempat >= tooHot){

		switch1.on()

	}
	
	if(tempat < tooHot){

		switch1.off()

	}
	
	
	
}