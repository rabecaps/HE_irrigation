# HE_irrigation
Site for code for Hubduino controller and irrigation controller via ESP8266 and 8 channel relay
Note - I am not a coder or software engineer, I am a hack. Hacking together code and ideas from everywhere.
Please do not expect support for this code.
If you see something and think "why the heck did he do it that way!" and you have a better way please let me know.
This is code is in two parts ESP 8266 (handled now by Hubduino) and groovy code adapted from @BPTWorld simple Irrigation and the idea was to remove the need for logic in Node Red and to simplify control systems around HE for a new retic control system with 8 zones for a new house. (NB Add technical and HW here) <br>

Principles:
Minimising to just Hubduino which is very reliable
NodeMCU/ESP8266 to control On and Off independently of HE signals so if HE dies during watering for some reason the water won't run all day.
Note that HubDuino relay is a timed relay and has a default time that I adjusted to 20 minutes. This is the fall back and I know that I would never water a single zone for more than 20 minutes.

Basics: ESP LoLin NodeMCU v3 Node MCU running Hubduino / ST Anything - only mod is the timeout.
I have set up 8 zones. 
This consists of an 8 relay board which Hubduino exposes as 8 relays.
I set up 8 global variables in RM with 8 connectors to dimmers and 8 to variables, one for each relay.
I set up a dashboard called Watering Controll that has 8 dimmer sliders that can be used to set the water time or turned off the skip a zone. There is a weather linked switch to Weather underground that looks for more than 5mm in last 24 hours or more than 5mm forecast. There is also a manual override for Winter watering bans. These are covered in Bryans code already.

I modified the Simple Irrigation timer to ask for these variables and controls and set it to loop through the 8 zones sequentially.

I dont really know how github works either ;-)

  
