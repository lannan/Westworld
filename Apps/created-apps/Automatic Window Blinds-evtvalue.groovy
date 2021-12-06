/**
 *  Automatic Window Blinds
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
	name: "Automatic Window Blinds2",
	namespace: "sem15",
	author: "Steven Maxwell",
	description: "Automatically raises your blinds.",
	category: "",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Blinds to control?") {
		input "blinds", "capability.switch", multiple: true
	}
	
	section("Illuminance sensor to watch?") {
		input "light1", "capability.illuminanceMeasurement"
	}
		
}

def installed() {
	
	subscribe(light1, "illuminance", activateBlinds)
	

}

def updated(settings) {
	unsubscribe()
	subscribe(light1, "illuminance", activateBlinds)
	
}




def activateBlinds(evt) {
	def impossibleVal = 0
	boolean raiseBlinds = false
	int blindsState = evt.integerValue
	
	log.debug "evt.value: ${blindsState} "
 
	if(blindsState > 10) {
		raiseBlinds = true
	}
	
	if(blindsState < 10){
		raiseBlinds = false
	}
	
	def loc = getLocation()
	def curMode = loc.getCurrentMode()
	
	if(curMode != "Home" && blindsState < impossibleVal){
		blinds.off()
	}
	
	
	if(curMode == "Home" && raiseBlinds){
		blinds.on()
	}
	
	
	

}
