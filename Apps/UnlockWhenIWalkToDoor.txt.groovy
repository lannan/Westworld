/**
 *  Unlock When I Walk To Door
 *
 *  Copyright 2015 Matt Cowger
 *
 */
definition(
    name: "Unlock When I Walk To Door",
    namespace: "mcowger",
    author: "Matt Cowger",
    description: "Unlocks 1 or more locks when presence is detected & motion sensor is activated.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("When I arrive..."){
    input "presence1", "capability.presenceSensor", title: "Who...", required: true
  	input "motion1", "capability.motionSensor", title: "and this sensor sees movement...", required: true
  }
  
  section("Unlock the lock..."){
    input "lock1", "capability.lock"
  }
}

def installed()
{
  subscribe(motion1, "motion", motionevent)
}

def updated()
{
  unsubscribe()
  subscribe(motion1, "motion", motionevent)
}

def motionevent(evt)
{
	def str = evt.value

    if (str == "active" ) {
        	lock1.unlock()
    }

}
