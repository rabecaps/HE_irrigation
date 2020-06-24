/**
 *  ****************  Hubduino Simple Irrigation Child App  ****************
 *
 *  Design Usage:
 *  For use with any valve device connected to your hose, like the Orbit Hose Water Timer. Features multiple timers and
 *  restrictions.
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 * 
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
 * 
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *  
 *  This app requires four elements to be in place for fully dashboard controlled run. 
 *  1. Global variables set up in RM one for eachZone. 
 *  2. Connectors for each Global Variable one for variable and one for each dimmer
 *  3. Dimmers on a dash board linked to the connectors that set the dimmer level as a substitute for runtime.
 *  4. Sync rules to set the global variable when the dimmer level changes.
 *  Note that I set a maximum runtime default in the Hubduino code so that any loss of power or crash of HE would not leave the water running
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *  V1.0.2 - 06/23/20 - updated the startTime to update the dashboard tile.
 *  V1.0.1 - 06/22/20 - Added dimmer capability via connectors and global variables. http://
 *  V1.0.0 - 06/21/20 - Modified Initial BPTWorld release of simple Irrigation timer.
 *  Based on v2.0.0 Simple Irrigation Timer
 */


def setVersion(){
    // *  V2.0.0 - 08/18/19 - Now App Watchdog compliant
	if(logEnable) log.debug "In setVersion - App Watchdog Child app code"
    // Must match the exact name used in the json file. ie. AppWatchdogParentVersion, AppWatchdogChildVersion or AppWatchdogDriverVersion
    state.appName = "HubduinoSimpleIrrigationChild"
	state.version = "v2.0.0"
    
    try {
        if(parent.sendToAWSwitch && parent.awDevice) {
            awInfo = "${state.appName}:${state.version}"
		    parent.awDevice.sendAWinfoMap(awInfo)
            if(logEnable) log.debug "In setVersion - Info was sent to App Watchdog"
            schedule("0 0 3 ? * * *", setVersion)
	    }
    } catch (e) { log.error "In setVersion - ${e}" }
}

definition(
    name: "Hubduino Simple Irrigation Child",
    namespace: "BPTWorld",
    author: "Bryan Turcotte - Original - Brad Filmer Modifications 2020",
    description: "For use with Hubduino and multi-relay boards for irrigation control. Features multiple timers and weather / zone restrictions with ability to use dimmer sliders to set runtime on dashboard and control zones.",
    category: "Convenience",
	parent: "BPTWorld:Hubduino Simple Irrigation",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/",
)

preferences {
    page(name: "pageConfig")
}

def pageConfig() {
    dynamicPage(name: "", title: "<h2 style='color:#1A77C9;font-weight: bold'>Simple Irrigation</h2>", install: true, uninstall: true, refreshInterval:0) {
		display() 
        section("Instructions:", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
    		paragraph "For use with Hubduino 8266 and upto 8 Chanel relay board. Features multiple timers and restrictions."
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Valve Devices")) {
			input "relayDevice1", "capability.switch", required: true, title: "Select Zone 1 Relay Device."
            input "relayDevice2", "capability.switch", required: false, title: "Select Zone 2 Relay Device."
            input "relayDevice3", "capability.switch", required: false, title: "Select Zone 3 Relay Device."
            input "relayDevice4", "capability.switch", required: false, title: "Select Zone 4 Relay Device."
            input "relayDevice5", "capability.switch", required: false, title: "Select Zone 5 Relay Device."
            input "relayDevice6", "capability.switch", required: false, title: "Select Zone 6 Relay Device."
            input "relayDevice7", "capability.switch", required: false, title: "Select Zone 7 Relay Device."
            input "relayDevice8", "capability.switch", required: false, title: "Select Zone 8 Relay Device."
        }
         section(getFormat("header-green", "${getImage("Blank")}"+" Dashboad Zone Switch Devices")) {   
            input "switchZone1", "capability.switch", required: true, title: "Select Zone 1 dashboard Switch."
            input "switchZone2", "capability.switch", required: false, title: "Select Zone 2 dashboard Switch."
            input "switchZone3", "capability.switch", required: false, title: "Select Zone 3 dashboard Switch."
            input "switchZone4", "capability.switch", required: false, title: "Select Zone 4 dashboard Switch."
            input "switchZone5", "capability.switch", required: false, title: "Select Zone 5 dashboard Switch."
            input "switchZone6", "capability.switch", required: false, title: "Select Zone 6 dashboard Switch."
            input "switchZone7", "capability.switch", required: false, title: "Select Zone 7 dashboard Switch."
            input "switchZone8", "capability.switch", required: false, title: "Select Zone 8 dashboard Switch."
         }
         section(getFormat("header-green", "${getImage("Blank")}"+" Global Variables synced to Zone Switch Devices")) {
            input "varwaterTime", "capability.sensor", required: false, title: "Select your Watering Time dashboard linked vairable."   
            input "varZone_1", "capability.sensor", required: true, title: "Select Zone1 global variable / connector capability.sensor."
            input "varZone_2", "capability.sensor", required: false, title: "Select Zone2 global variable / connector capability.sensor."
            input "varZone_3", "capability.sensor", required: false, title: "Select Zone3 global variable / connector capability.sensor."
            input "varZone_4", "capability.sensor", required: false, title: "Select Zone4 global variable / connector capability.sensor."
            input "varZone_5", "capability.sensor", required: false, title: "Select Zone5 global variable / connector capability.sensor."
            input "varZone_6", "capability.sensor", required: false, title: "Select Zone6 global variable / connector capability.sensor."
            input "varZone_7", "capability.sensor", required: false, title: "Select Zone7 global variable / connector capability.sensor."
            input "varZone_8", "capability.sensor", required: false, title: "Select Zone8 global variable / connector capability.sensor."
        }
		section(getFormat("header-green", "${getImage("Blank")}"+" Schedule")) {
			input(name: "days", type: "enum", title: "Only water on these days", description: "Days to water", required: true, multiple: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
			paragraph "Select up to 3 watering sessions per day. Set to 12:00:00 to use dashboard tiletime"
			input "startTime1", "time", title: "Time to turn on 1", required: true, width: 6
        	input "onLength1", "number", title: "Leave valve on for how long (in minutes)", required: true, width: 6
			paragraph "<hr>"
			input "startTime2", "time", title: "Time to turn on 2", required: false, width: 6
        	input "onLength2", "number", title: "Leave valve on for how long (in minutes)", required: false, width: 6
			paragraph "<hr>"
			input "startTime3", "time", title: "Time to turn on 3", required: false, width: 6
        	input "onLength3", "number", title: "Leave valve on for how long (in minutes)", required: false, width: 6
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Safety Features")) {
			paragraph "App can send the open/closed command several times with a 20 second delay between commands, until either max tries is reached or device reports that it is open/closed. Once max tries is reached, a notification will can be sent (if selected below)."
			input "maxTriesOn", "number", title: "Attempts to OPEN", required: true, defaultValue: 3, width: 6
			input "maxTriesOff", "number", title: "Attempts to CLOSE", required: true, defaultValue: 5, width: 6
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Check the Weather")) {
			paragraph "Disable app using any 'Switch' type device. I highly recommend using WATO to turn a virtual switch on/off based on any device parameter."
			paragraph "If ANY of the options below are ON, watering will be cancelled."
			input "rainSensor", "capability.switch", title: "Rain Switch", required: false
			input "windSensor", "capability.switch", title: "Wind Switch", required: false
			input "otherSensor", "capability.switch", title: "Other Switch", required: false
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Notification Options")) {
			input "sendPushMessage", "capability.notification", title: "Send a notification?", multiple: true, required: false
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {label title: "Enter a name for this automation", required: false}
        section() {
            input(name: "logEnable", type: "bool", defaultValue: "true", title: "Enable Debug Logging", description: "Enable extra logging for debugging.")
		}

		display2()
	}
}

def installed() {
    log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {	
    if(logEnable) log.debug "Updated with settings: ${settings}"
	unschedule()
	initialize()
}

def initialize() {
    setDefaults()
     // cant figure out how to trigger schedule update on dashboard tile change. So push time to dashboard tiles instead
    if(startTime1) {
        schedule(startTime1, turnSwitchOn, [overwrite: false]) 
        varwaterTime.setVariable(startTime1)
    } 
    if(startTime2) schedule(startTime2, turnSwitchOn, [overwrite: false])
    if(startTime3) schedule(startTime3, turnSwitchOn, [overwrite: false])
}
	
def turnSwitchOn() {
    //Create array of hubduino relay devices to allow a single cycle through upto 8 zones.
    def relayArray = [relayDevice1, relayDevice2 ,relayDevice3 ,relayDevice4 ,relayDevice5 ,relayDevice6 ,relayDevice7 ,relayDevice8]
    //Create array of values stored in the global variable / connectors. These are RM synced to dimmer sliders on the Watering Control dashboard. Value as integer = runtime in minutes
    def String[] zoneRuntimeArrayStr = [varZone_1.currentVariable, varZone_2.currentVariable, varZone_3.currentVariable, varZone_4.currentVariable, varZone_5.currentVariable, varZone_6.currentVariable, varZone_7.currentVariable, varZone_8.currentVariable]
    //Create array of values for the each Zone Dimmer from the dashboard so we can skip if a zone is off
    def zoneManualStatus = [switchZone1, switchZone2, switchZone3, switchZone4, switchZone5, switchZone6, switchZone7, switchZone8]
    
    for (i = 0; i <8; i++) { // 8 relay module in this case 0-7
        def relayDevice = relayArray[i]
        def zoneManual = zoneManualStatus[i] 
        def runTime = zoneRuntimeArrayStr[i]
        def delay = zoneRuntimeArrayStr[i].toInteger() *60 //Convert to integer from string as stored in the array.
        state.relayStatus = relayDevice.currentValue("switch")
        state.zoneStatus = zoneManual.currentValue("switch")
                if(logEnable) log.debug "0:== runtime variable update ==> ${runTime} minutes"
                if(logEnable) log.debug "1:== Relay Device is set to: $relayDevice, status of device is currently: ${state.relayStatus}, delay is set to: $delay seconds"
                if(logEnable) log.debug "2:== Dashboard manual control status for zone is: ${state.zoneStatus}"
  	    dayOfTheWeekHandler()
	    checkForWeather()
        // checkZoneManualBypass() // Future enhancement - perhaps handle in own function.
	    //check it is the right day
        if(state.daysMatch == "yes") { 
		    // check the weather switch which is set from WU custom and a combination of rainToday >5 and rainTomorrow >5 and written into virtual switch
            if(state.canWater == "yes") { 
			    if (state.zoneStatus == "on") { //check that the zone is enabled on the dashboard.
                    if(state.relayStatus == "off") { //check it is currently off - it should be!
                        if(logEnable) log.debug "3: In function turnSwitchOn: ${relayDevice} - about to turn on"
				        relayDevice.on()
                        pauseExecution(1000) //just hold your horses for one second
                        state.relayStatus = relayDevice.currentValue("switch", true) //check new status using skipCache - this was tricky to find!
                        if(logEnable) log.debug "4: ${relayDevice} is now set to status: ${state.relayStatus}, delay will be set to: ${delay} seconds"
                        if (state.relayStatus == "on") {log.debug "${relayDevice}: ${state.relayStatus} - turned on successfully"}
            //delay loop  
                        if(logEnable) log.debug {"5: Valve is now ${state.relayStatus}, Setting timer to turn off in ${runTime} minutes"}
				        state.msg = "${relayDevice} is now ${state.relayStatus}"
				        if(sendPushMessage) pushHandler()
                        int delay1 = delay * 1000
                        pauseExecution(delay1)
				        
                        relayDevice.off()
                        state.relayStatus = relayDevice.currentValue("switch", true)
                        state.msg = "${relayDevice} is now ${state.relayStatus}"
                        if(sendPushMessage) pushHandler(errorMsg)
                        pauseExecution(10000) // pause 10s between zones
                    } else {
                        log.debug "==> Opps switch was already on. Something failed or was in the wrong state?"
                        def String errorMsg = "Error: Watering Failure - One of the Zones was on! You had better check"
                        if(sendPushMessage) pushHandler(errorMsg)
                    }
                } else {
                    log.debug "Skipping zone manually switched off"
                    exit
                }
		        } else {
                    log.info "${app.label} Didn't pass weather check. ${relayDevice} not turned on."
			        relayDevice.off()
                    def String errorMsg = "Status: Watering skipped - Not watering today as Manual or Rain restrictions in place"
                    if(sendPushMessage) pushHandler(errorMsg)
		        }
	        } else {
		        log.info "${app.label} Not the right days to water - Water not turned on."
		        state.msg = "${app.label} Didn't pass restriction check - Watering Days. ${relayDevice} will not turn on."
	}	
}
}

def checkForWeather() {
	if(logEnable) log.debug "In checkForWeather..."
	if(rainSensor) state.rainDevice = rainSensor.currentValue("switch")
	if(windSensor) state.windDevice = windSensor.currentValue("switch")
	if(otherSensor) state.otherDevice = otherSensor.currentValue("switch")
	if(state.rainDevice == "on" || state.windDevice == "on" || state.otherDevice == "on") {
		if(logEnable) log.debug "In checkForWeather - Weather Check failed."
		state.canWater = "no"
	} else {
		if(logEnable) log.debug "In checkForWeather - Weather Check passed."
		state.canWater = "yes"
	}
}

def dayOfTheWeekHandler() {
	if(logEnable) log.debug "In dayOfTheWeek..."
	Calendar date = Calendar.getInstance()
	int dayOfTheWeek = date.get(Calendar.DAY_OF_WEEK)
	if(dayOfTheWeek == 1) state.dotWeek = "Sunday"
	if(dayOfTheWeek == 2) state.dotWeek = "Monday"
	if(dayOfTheWeek == 3) state.dotWeek = "Tuesday"
	if(dayOfTheWeek == 4) state.dotWeek = "Wednesday"
	if(dayOfTheWeek == 5) state.dotWeek = "Thursday"
	if(dayOfTheWeek == 6) state.dotWeek = "Friday"
	if(dayOfTheWeek == 7) state.dotWeek = "Saturday"

	if(days.contains(state.dotWeek)) {
		if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Passed"
		state.daysMatch = "yes"
	} else {
		if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Check Failed"
		state.daysMatch = "no"
	}
}

def pushHandler(errorMsg){
    def errMsg = errorMsg
	if(logEnable) log.debug "In pushNow..."
        if (errMsg == null) { 
	        theMessage = "${app.label} - ${state.msg}"
        } else {
            theMessage = "${errMsg}"
        }
	if(logEnable) log.debug "In pushNow...Sending message: ${theMessage}"
   	sendPushMessage.deviceNotification(theMessage)
	state.msg = ""
}

// ********** Normal Stuff **********

def setDefaults(){
	if(logEnable == null){logEnable = false}
	if(state.rainDevice == null){state.rainDevice = "off"}
	if(state.windDevice == null){state.windDevice = "off"}
	if(state.otherDevice == null){state.otherDevice = "off"}
	if(state.daysMatch == null){state.daysMatch = "no"}
	if(state.msg == null){state.msg = ""}
}

def getImage(type) {					// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
}

def getFormat(type, myText=""){			// Modified from @Stephack Code
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "\n<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<div style='color:blue;font-weight: bold'>${myText}</div>"
}

def display() {
	section() {
		paragraph getFormat("line")
	}
}

def display2(){
    setVersion()
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center'>Hubduio Simple Irrigation - @BPTWorld<br><a href='https://github.com/rabecaps/Hubitat' target='_blank'> Just new to all this here!</a><br></div>"
	}       
}
 //kept for reference==> log.debug "Inside turnSwitchOn - relayDevice is set to: $tempSwitchDevice, ${tempSwitchDevice.currentValue("switch")}, ${relayDevice.currentValue("switch")}"
