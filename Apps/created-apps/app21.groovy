/**
 *  Test for paths with systemAPI
 */
definition(
	name: "? paths",
	namespace: "tests",
	author: "boubou",
	description: "Test for paths",
	category: "Convenience",
	iconUrl: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn",
	iconX2Url: "https://graph.api.smartthings.com/api/devices/icons/st.Weather.weather9-icn?displaySize=2x"
)


preferences {

  if (!(location.zipCode || ( location.latitude && location.longitude )) && location.channelName == 'samsungtv') {
        section { paragraph title: "Note:", "Location is required for this SmartApp. Go to 'Location Name' settings to setup your correct location." }
    }

    section( "Set the temperature range for your comfort zone..." ) {
        input "minTemp", "number", title: "Minimum temperature"
        input "maxTemp", "number", title: "Maximum temperature"
    }
    section( "Select windows to check..." ) {
        input "sensors", "capability.contactSensor", multiple: true
    }
    section( "Select temperature devices to monitor..." ) {
        input "inTemp", "capability.temperatureMeasurement", title: "Indoor"
        input "outTemp", "capability.temperatureMeasurement", title: "Outdoor (optional)", required: false
    }

    if (location.channelName != 'samsungtv') {
        section( "Set your location" ) { input "zipCode", "text", title: "Zip code" }
    }

    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "retryPeriod", "number", title: "Minutes between notifications:"
    }
}

def installed() {
    log.debug "Installed: $settings"
    subscribe( inTemp, "temperature", temperatureHandler )
}

def updated() {
    log.debug "Updated: $settings"
    unsubscribe()
    subscribe( inTemp, "temperature", temperatureHandler )
}

def temperatureHandler(evt) {
    def currentOutTemp = null
    if ( outTemp ) {
        currentOutTemp = outTemp.latestValue("temperature")
    } else {
        log.debug "No external temperature device set. Checking The Weather Company..."
        currentOutTemp = weatherCheck()
    }

    def currentInTemp = evt.doubleValue
    def openWindows = sensors.findAll { it?.latestValue("contact") == 'open' }

    if (!retryPeriod) {
        def retryPeriod = 30
    }
    
    def timeAgo = new Date(now() - (1000 * 60 * retryPeriod).toLong())
    def recentEvents = inTemp.eventsSince(timeAgo)
    log.trace "Found ${recentEvents?.size() ?: 0} events in the last $retryPeriod minutes"

    if ( currentInTemp > minTemp && currentInTemp < maxTemp ) {
        log.info "In comfort zone: $currentInTemp is between $minTemp and $maxTemp."
        log.debug "No notifications sent."
    } else if ( currentInTemp > maxTemp ) {

        def alreadyNotified = recentEvents.count { it.doubleValue > currentOutTemp } > 1

        if ( !alreadyNotified ) {
            if ( currentOutTemp < maxTemp && !openWindows ) {
                send( "Open some windows to cool down the house! Currently ${currentInTemp}°F inside and ${currentOutTemp}°F outside." )
            } else if ( currentOutTemp > maxTemp && openWindows ) {
                send( "It's gotten warmer outside! You should close these windows: ${openWindows.join(', ')}. Currently ${currentInTemp}°F inside and ${currentOutTemp}°F outside." )
            } else {
                log.debug "No notifications sent. Everything is in the right place."
            }
        } else {
            log.debug "Already notified! No notifications sent."
        }
    } else if ( currentInTemp < minTemp ) {
        def alreadyNotified = recentEvents.count { it.doubleValue < currentOutTemp } > 1
        if ( !alreadyNotified ) {
            if ( currentOutTemp > minTemp && !openWindows ) {
                send( "Open some windows to warm up the house! Currently ${currentInTemp}°F inside and ${currentOutTemp}°F outside." )
            } else if ( currentOutTemp < minTemp && openWindows ) {
                send( "It's gotten colder outside! You should close these windows: ${openWindows.join(', ')}. Currently ${currentInTemp}°F inside and ${currentOutTemp}°F outside." )
            } else {
                log.debug "No notifications sent. Everything is in the right place."
            }
        } else {
            log.debug "Already notified! No notifications sent."
        }
    }
}

def weatherCheck() {
    def obs = getTwcConditions(zipCode)
    def currentTemp = obs.temperature
    if ( currentTemp ) {
        log.trace "Temp: $currentTemp (The Weather Company)"
        return currentTemp
    } else {
        log.warn "Did not get a temp: $obs"
        return false
    }
}

private send(msg) {
    if (sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
        sendEvent(linkText:app.label, descriptionText:msg, eventType:"SOLUTION_EVENT", displayed: true, name:"summary")
    }
    if ( phone1 ) {
        log.debug( "sending text message" )
        sendSms( phone1, msg )
    }
    log.info msg
}
