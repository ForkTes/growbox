growbox
=======

Atmel AVR-based illumination / temperature / humidity / ventilation controller for indoors growing of seedlings. Includes a wireless readout and parameter setup. Controller: ATTiny2313, T / rH sensor: SHT-11, wireless: RFM12B

Warning: I am prone to work in the most minimalist enironment possible, therefore the controller side of this project is realized entirely in AVR-Assembler. 

Features:

- 6 user-selectable schedules for two light-sources with 2 on-periods per day each
- temperature and humidity sensing using a SHT-11 (or similar) sensor
- ventilation thresholds for T and rH based on sensor readings
- EEPROM retention of parameters to guard against IPL
- wireless remote monitoring and control of parameters using a RFM12B (868MHz) module
- lock-out algorithm to protect HP sodium tubes from hot ignition
- java GUI to display and control parameters

This project is geared towards a setup with a high pressure sodium vapour light source in combination with a secondary illumination of flourescent tubes and a ventilation system for temperature / humidity control. It comes preconfigured with a set of programs and thresholds in the on-chip EEPROM and relies on a wireless link via RFM12B (868MHz) to a host for changing of these parameters. 
Example schematics for the controller-board, host-connection, sensor, ventilation, and light-sources will be added in due time. 
Atmel code was built using the AVR Studio 4. Atmel devices were programmed via the STK-500 board. 

(I prefer tabstops >= 12 in AVR Studio, use this to make the code more readable)
