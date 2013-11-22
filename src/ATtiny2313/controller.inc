;******************************************************************************
;***                                                                        ***
;***  Leuchte Firmware v1.5                                                 ***
;***  Platform AT90S2313 5MHz                                               ***
;***                                                                        ***
;******************************************************************************

;Portpin	function		I/O	(pull-up)
;PD0	Sensor data		I	
;  1	Sensor clock	O	
;  2	LED		O	
;  3	Ventilation		O	
;  4	865 tubes		O	
;  5	SON-T		O	
;  6	RFM12 Int		I	

;PB0	RFM12 EN		O	
;  1	RFM12 SDO		O	
;  2	RFM12 SDI		I	
;  3	RFM12 SCLK		O	
;  4	Mode 0	 	I	*
;  5	Mode 1		I	*
;  6	Mode 2		I	*
;  7	Mode 3		I	*

;Interrupt source config
;GIMSK	00000000	external INT1, INT0 disabled
;TIMSK	01000000	TOIE1, TICIE1, TOIE0 disabled; OCIE1A enabled
;MCUCR	00000000	Sleep disabled; INT1, INT0 low level activated
;ACSR	10000000	AC power off; AC disabled
;WDTCR	00001111	watchdog 1.9s
;TCCR1A	00000000	T1 disconnected from OC1, no PWM
;TCCR1B	00001010	no capture, clear on compare match, prescaler 8


;***************
;** constants **
;***************

;*** presets
.equ	VERSION	= 0x0150	;software version
.equ	LED_ON	= 1	;flash on
.equ	LED_OFF	= 4	;flash off
.equ	LED_LAST	= 9	;last flash off
.equ	LED_LONG	= 16	;flash long
.equ	MIN_VENT	= 5	;minimum vent-on time in minutes
.equ	SEC_CORR	= 26	;correction for seconds on reboot
.equ	DEFAULTPROG	= 3	;default program

;*** menu state constants
.equ	MENU_START	= 0xff
.equ	MENU_TIME	= 0x10
.equ	MENU_SERIAL	= 0x20
.equ	MENU_DUMMY3	= 0x30
.equ	MENU_DUMMY4	= 0x40
.equ	MENU_DUMMY5	= 0x50
.equ	MENU_DUMMY6	= 0x60
.equ	MENU_DUMMY7	= 0x70
.equ	MENU_DUMMY8	= 0x80
.equ	MENU_DUMMY9	= 0x90

;*** sensor commands
.equ	CMD_T_MEAS	= 0x03	;measure temperature
.equ	CMD_H_MEAS	= 0x05	;measure humidity
.equ	CMD_WR_ST	= 0x06	;write status
.equ	CMD_RD_ST	= 0x07	;read status
.equ	CMD_RESET	= 0x1e	;reset

;*** RFM12 packet headers
.equ	S_STATUS	= 0xf5	;
.equ	S_SETTIME	= 0xf6	;
.equ	S_SETTHRESH	= 0xf7	;
.equ	S_SETPROG	= 0xf8	;f8:e_config0, f9:e_config1, ...
.equ	S_BUSY	= 0xbb	;


;***************
;** SRAM area **
;***************
.DSEG
.ORG	$0060
sram_addr:			;start of SRAM
d_vars:	.byte	48	;variables

d_pream:	.byte	4	;transmitter preamble
d_data:	.byte	20	;RFM12 data buffer

d_ewr:	.byte	15	;EEPROM write buffer
d_ewrl:	.byte	1

scrap:	.byte	30	;free space

	.byte	10	;stack

;*** offsets relativ to d_vars (Y)
.equ	d_temp	= 0x00
.equ	d_humi	= 0x01
.equ	d_write_ad	= 0x02
.equ	d_write_cs	= 0x03
.equ	d_menu1	= 0x04
.equ	d_menu2	= 0x05
.equ	d_sensorinl	= 0x06
.equ	d_sensorinh	= 0x07
.equ	d_sensorcnt	= 0x08
.equ	d_sensorcmd	= 0x09
.equ	d_vent_o	= 0x0a
.equ	d_sendend	= 0x0b
.equ	d_recvend	= 0x0c
.equ	d_recvstart	= 0x0d
.equ	d_wrext_ad	= 0x0e
.equ	d_wrext_en	= 0x0f
;.equ	d_	= 0x10
;.equ	d_	= 0x11
;.equ	d_	= 0x12
;.equ	d_	= 0x13
;.equ	d_	= 0x14
;.equ	d_	= 0x15
;.equ	d_	= 0x16
;.equ	d_	= 0x17
;.equ	d_	= 0x18
;.equ	d_	= 0x19
;.equ	d_	= 0x1a
;.equ	d_	= 0x1b

.equ	d_threshold = 0x1c
.equ	d_lth_temp	= 0x1c
.equ	d_uth_temp	= 0x1d
.equ	d_lth_humi	= 0x1e
.equ	d_uth_humi	= 0x1f

.equ	d_program	= 0x20
.equ	d_1on_1_h	= 0x20
.equ	d_1on_1_m	= 0x21
.equ	d_1off_1_h	= 0x22
.equ	d_1off_1_m	= 0x23
.equ	d_1on_2_h	= 0x24
.equ	d_1on_2_m	= 0x25
.equ	d_1off_2_h	= 0x26
.equ	d_1off_2_m	= 0x27
.equ	d_2on_1_h	= 0x28
.equ	d_2on_1_m	= 0x29
.equ	d_2off_1_h	= 0x2a
.equ	d_2off_1_m	= 0x2b
.equ	d_2on_2_h	= 0x2c
.equ	d_2on_2_m	= 0x2d
.equ	d_2off_2_h	= 0x2e
.equ	d_2off_2_m	= 0x2f
.equ	d_prog1_1	= d_1on_1_h
.equ	d_prog1_2	= d_1on_2_h
.equ	d_prog2_1	= d_2on_1_h
.equ	d_prog2_2	= d_2on_2_h


;*****************
;** EEPROM area **
;*****************

;*** time and program presets
.equ	TIME_S	= 10
.equ	TIME_M	= 21
.equ	TIME_H	= 14
.equ	TIME_D	= 1
.equ	PROGRAM	= 0

.set	TEMP	= low(TIME_S + 5)
.set	TEMP	= low((TEMP << 4) | (TEMP >> 4))
.set	TEMP	= TEMP ^ low(TIME_M + 4)
.set	TEMP	= low((TEMP << 4) | (TEMP >> 4))
.set	TEMP	= TEMP ^ low(TIME_H + 3)
.set	TEMP	= low((TEMP << 4) | (TEMP >> 4))
.set	TEMP	= TEMP ^ low(TIME_D + 2)
.set	TEMP	= low((TEMP << 4) | (TEMP >> 4))
.set	TEMP	= TEMP ^ low(PROGRAM + 1)
.equ	CHKSUM	= low((TEMP << 4) | (TEMP >> 4))

;*** data
.ESEG
e_data:
e_version:	.db	high(VERSION), low(VERSION)
	.db	0, 0, 0, 0, 0, 0

e_time:
e_time_s:	.db	TIME_S	;seconds
e_time_m:	.db	TIME_M	;minutes
e_time_h:	.db	TIME_H	;hours
e_time_d:	.db	TIME_D	;days
e_program:	.db	PROGRAM	;current program
e_chksum:	.db	CHKSUM	;checksum block
e_time_bu:	.db	TIME_S, TIME_M, TIME_H, TIME_D
e_prog_bu:	.db	PROGRAM
e_chks_bu:	.db	CHKSUM

	.db	0, 0, 0, 0

e_threshold:
e_lth_temp:	.db	101	;lower threshold temperature
e_uth_temp:	.db	117	;upper threshold temperature
e_lth_humi:	.db	65	;lower threshold humidity
e_uth_humi:	.db	90	;upper threshold humidity

	.db	0, 0, 0, 0

e_config:
e_config0:	.db	 5, 00, 23, 00, -1, -1, -1, -1
	.db	-1, -1, -1, -1, -1, -1, -1, -1
e_config1:	.db	 5, 00, 23, 00, -1, -1, -1, -1
	.db	 9, 00, 20, 00, -1, -1, -1, -1
e_config2:	.db	 5, 00, 23, 00, -1, -1, -1, -1
	.db	 6, 00, 22, 00, -1, -1, -1, -1
e_config3:	.db	 8, 00, 20, 00, -1, -1, -1, -1
	.db	 8, 00, 20, 00, -1, -1, -1, -1
e_config4:	.db	11, 00, 17, 00, -1, -1, -1, -1
	.db	 8, 30, 20, 00, -1, -1, -1, -1
e_config5:	.db	-1, -1, -1, -1, -1, -1, -1, -1
	.db	 8, 30, 20, 00, -1, -1, -1, -1


;******************************************************

.EXIT

;r0	seconds
;r1	minutes
;r2	hours
;r3	days
;r4	1/20s
;r5	constant 20
;r6	constant 60
;r7	constant 24
;r8	constant 10
;r9	i working register
;r10	send counter
;r11	receive counter
;r12	
;r13	
;r14	working register
;r15	working register
;r16	working register
;r17	working register
;r18	
;r19	
;r20	status register
;7	write time pending
;6	key pressed
;5	day change
;4	minute change
;3	seconds change
;2	seconds + 0.5 change
;1	
;0	extended write pending
;r21	hardware status
;7	venting active
;6	venting override
;5	SON-T indicator
;4	865 indicator
;3	read T / rH value
;2	program nr bit 2
;1	program nr bit 1
;0	program nr bit 0
;r22	led flash counter
;r23	led flash timer
;r24	
;r25	
;r26/XL	working register
;r27	state of menu
;r28/YL	pointer to variables in SRAM
;r29	
;r30/ZL	working register
;r31	
