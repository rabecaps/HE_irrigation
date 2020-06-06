# HE_irrigation
Site for code for MQTT hubitat controller and irrigation controller via ESP8266 and 8 channel relay
Note - I am not a coder or software engineer, I am a hack. Hacking together code and ideas from everywhere.
Please do not expect support for this code.
If you see something and think "why the heck did he do it that way!" and you have a better way please let me know.
This is code is in two parts ESP 8266 and groovy and the idea was to remove the need for logic in Node Red and to simplify control systems around HE for a new retic controll system with 8 zones for a new house. (NB Add technical and HW here)
Principles:
  Minimising to just an MQTT broker which are very reliable
  ESP8266 to control On and Off independently of HE signals so if HE dies during watering for some reason the water won't run all day.
  HE to be able to clean up.
  Basics: ESP Node MCU connects to MQTT "irrigation/control "and listens for [Station number]:[time in millis seconds]:[on or off] e.g.     1:180000:1 = zone one on for 3 minutes.
  1:180000:0 = zone one off
  9:9:9 = all zones off.
  
  HE hubitat groovy will develop in phases.
  Phase one just issue the commands to one station.
  Phase two develop time code to sequence - Steal ideas from simple irrigation and device sequencer
