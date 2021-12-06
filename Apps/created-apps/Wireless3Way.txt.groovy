/**
 *  App Name:   Wireless 3-Way
 *
 *  Author: 	chrisb 
 *  Date: 		2013-07-16
 *  
 *  This app "groups" a set of switches together so that if any one is turned on
 *	or off, they all will go on or off.  This allows them to act as three-way
 *	without needing wiring between the switches.
 *	
 *  This program is Public Domain.
 */

// This section is what the user will see and asks him or her to input the data we need.

// Automatically generated. Make future change here.
definition(
    name: "Wireless 3-Way",
    namespace: "",
    author: "seateabee@gmail.com",
    description: "Set up any two (or more) switches to turn on or off together.  This allows for a &amp;amp;amp;quot;wireless&amp;amp;amp;quot; three way setup.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

preferences {
    section("Turn on which switches or outlets?"){						// Whatever is in the quotes will be displayed to the user.
		input "switches", "capability.switch", title: "Which?", multiple: true	
        
        // input means we're getting data from teh user.  The first item "switches" is the nickname we're giving to this data.
        
        // The second item "capability.switch" is asking what type of device we're looking for.  We're looking for a device that
        // can turn on and off.  That can "switch."  
        
        // The third item, Title, is optional.  This will display in the field that the user sees.  You don't need to include this. 
        
        // Finally, we're indicating "multiple: true".  This lets the user select multiple devices that fit the profile (ie, devices 
        // that can 'switch').  This is also optional.  Obviously we need multiple devices for this program but if you don't for
        // future programs, don't include this.     
    }
}


// This section tells the program what to do when it's first installed.
def installed()
{
	subscribe(switches, "switch.on", switchOnHandler)			
    subscribe(switches, "switch.off", switchOffHandler)
    

}

// This section tells the program what to do if a user who has installed the program updates it.
def updated()
{
	unsubscribe()
	subscribe(switches, "switch.on", switchOnHandler)
    subscribe(switches, "switch.off", switchOffHandler)


}

def switchOnHandler(evt) {
	log.debug "I see a switch was turned on... I'm turning them all on!"
 	switches.on()



}


def switchOffHandler(evt) {
	log.debug "I see a switch was turned off... I'm turning them all off!"
 	switches.off()

}