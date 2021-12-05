/**
 *  Motion Activated Light
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
 
 //This app contains requirements 1.1.3, 1.2.2.3
definition(
	name: "Motion Activated Light",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "Turns on a light when it detects motion.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {


section("Which light(s) should activate?") {
		input "switches", "capability.switch"
	}
	
	section("Detect motion from:") {
		input "motionSensor1", "capability.motionSensor"
	}
	
}

def installed()
{
	switches*.off()
	initialize()
}

def updated()
{
	unsubscribe()
	initialize()
}




def initialize() {
subscribe(motionSensor1, "motion.active", motionHandler)
}


def activateSwitches() {
	log.debug "Activating switches that are off."
	switches.on()
}

def turnOffSwitches() {
	
	switches.off()
}


def motionHandler(evt) {
	def var = 50;
	log.debug "motion detected."
	activateSwitches()
	var = var + 50
		
		
}