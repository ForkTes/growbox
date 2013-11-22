;******************************************************************************
;***                                                                        ***
;***  Leuchte Firmware v1.5                                                 ***
;***  Platform AT90S2313 8MHz                                               ***
;***                                                                        ***
;******************************************************************************

.INCLUDE	"2313def.inc"
.INCLUDE	"controller.inc"

;**************************************************
;*************** interrupts vectors ***************
;**************************************************

.CSEG
.ORG	$000
	clr	r15		;zero working reg
	ldi	r16, RAMEND		;set SP to top of SRAM
	out	SPL, r16
	rjmp	init

;*** interrupt routine, operates clock
.ORG	OC1addr
	in	r9, SREG		;save status
	dec	r4		;
	brne	t1_noclk1		;seconds update ?
	mov	r4, r5		;reload 1/20 counter
	inc	r0		;update clock
	sbr	r20, 0x08		;set seconds update flag
	cp	r0, r6		;
	brlo	t1_noclk		;
	clr	r0		;
	inc	r1		;
	sbr	r20, 0x10		;set minute update flag
	cp	r1, r6		;
	brlo	t1_noclk		;
	clr	r1		;
	inc	r2		;
	cp	r2, r7		;
	brlo	t1_noclk		;
	clr	r2		;
	inc	r3		;
	sbr	r20, 0x20		;set day update flag

t1_noclk:	out	SREG, r9		;restore status
	reti
t1_noclk1:	cp	r4, r8		;
	brne	t1_noclk		;
	sbr	r20, 0x04		;
	rjmp	t1_noclk		;

;**************************************************
;***************** initialisation *****************
;**************************************************
init:
;*** general Init ***
	ldi	YL, d_vars		;Y pointer to variables
	ldi	r16, 0x80		;switch off analog comparator
	out	ACSR, r16		;(80)
	ldi	r16, 0x0f		;enable watchdog 1.9s
	wdr			;
	out	WDTCR, r16		;(0f)
;*** PORT Init ***
	ldi	r16, 0xf1		;port B PB4-PB7 pu, outputs 0, PB0 1
	out	PORTB, r16		;(f1)
	ldi	r16, 0x0b		;port B PB0,PB1,PB3 output
	out	DDRB, r16		;(0b)
	;ldi	r16, 0xxx		;port D outputs 0
	out	PORTD, r15		;(00)
	ldi	r16, 0x3e		;port D PD1-PD5 output
	out	DDRD, r16		;(3e)
;*** timer Init ***
	ldi	r16, 0x7a		;compare value for 20Hz @5Mhz CK
	out	OCR1AH, r16		;(7a)
	ldi	r16, 0x12		;
	out	OCR1AL, r16		;(12)
	ldi	r16, 0x40		;enable timer OC1 interrupt
	out	TIMSK, r16		;(40)

;*** wait for stabilization of power supply
	ldi	r16, 9		;4 counts: 1000ms @ 1MHz, 200ms @ 5MHz
	mov	r4, r16		;(1MHz RFM12 default CLK after power up)
	ldi	r16, 5		;sufficient for reliable RFM12 POR
	mov	r8, r16		;
	clr	r20		;
	ldi	r16, 0x0a		;prepare clock start
	sei			;globally enable interrupts
	out	TCCR1B, r16		;(0a) start clock

i_wait05:	wdr			;
	sbrs	r20, 2		;wait for half seconds to change
	rjmp	i_wait05		;

	ldi	r17, 0xfe		;RFM12 reset
	ldi	r16, 0x00		;
	rcall	rfm_exchg		;

i_wait:	wdr			;
	sbrs	r20, 3		;wait for seconds to change
	rjmp	i_wait		;
	out	TCCR1B, r15		;(00) stop clock
	cli			;disable interrupts

;*** start preparations ***
	ldi	XL, d_vars		;
i_clrvar:	st	X+, r15		;clear vars
	cpi	XL, d_pream		;
	brne	i_clrvar		;

	rcall	loadtime		;load EEPROM time data onto scrap
	ldi	XL, scrap		;
	rcall	testtime		;validate first set of data
	breq	i_timeok		;if ok, continue
	rcall	testtime		;else, validate second set of data
	breq	i_timeok		;if ok, continue
	ldi	r21, DEFAULTPROG	;if no valid program, use default
	clr	r0		;and set time to 0
	clr	r1		;
	clr	r2		;
	clr	r3		;

i_timeok:	ldi	r16, SEC_CORR	;
	add	r0, r16		;

	ldi	r16, 20		;init 1/20 clock
	mov	r4, r16		;
	mov	r5, r16		;presets 20
	ldi	r16, 60		;
	mov	r6, r16		;60
	ldi	r16, 24		;
	mov	r7, r16		;24
	ldi	r16, 10		;
	mov	r8, r16		;10
	clr	r27		;reset state of menu
	clr	r10		;reset send counter
	clr	r11		;reset receive counter
	ldi	r22, -1		;

	rcall	loadthrsh		;

	rcall	loadprog		;

	ldi	r16, d_prog2_1	;SON-T cooldown test
	rcall	checktime		;
	brtc	i_noson1		;window 1 active ?
	mov	r16, r1		;-> set ontime 1 to now + 10min
	mov	r17, r2		;
	subi	r16, -10		;
	cp	r16, r6		;
	brlo	i_newson1		;
	sub	r16, r6		;
	inc	r17		;
i_newson1:	std	Y+d_2on_1_h, r17	;
	std	Y+d_2on_1_m, r16	;
i_noson1:	ldi	r16, d_prog2_2	;
	rcall	checktime		;
	brtc	i_noson2		;window 2 active ?
	mov	r16, r1		;-> set ontime 2 to now + 10min
	mov	r17, r2		;
	subi	r16, -10		;
	cp	r16, r6		;
	brlo	i_newson2		;
	sub	r16, r6		;
	inc	r17		;
i_newson2:	std	Y+d_2on_2_h, r17	;
	std	Y+d_2on_2_m, r16	;
i_noson2:
	ldi	XL, d_pream		;load write command and sync pattern into output puffer
	ldi	r16, 0xb8		;
	st	X+, r16		;(b8)
	ldi	r16, 0x2d		;
	st	X+, r16		;(2d)
	ldi	r16, 0xd4		;
	st	X+, r16		;(d4)

	ldi	r17, 0x80		;e1,ef,868MHz,12.5pf
	ldi	r16, 0xe8		;
	rcall	rfm_exchg		;
	ldi	r17, 0xa5		;866,60MHz
	ldi	r16, 0x28		;
	rcall	rfm_exchg		;
	ldi	r17, 0xc6		;~16kbd
	ldi	r16, 0x14		;
	rcall	rfm_exchg		;
	ldi	r17, 0x94		;p16 VDI,fast VDI,67kHz bw,-6dbm gain,-97dbm DRSSI
	ldi	r16, 0xc9		;
	rcall	rfm_exchg		;
	ldi	r17, 0xc2		;al,s,4 DQD
	ldi	r16, 0xac		;
	rcall	rfm_exchg		;
	ldi	r17, 0xca		;8 FIFO Int lvl,ff
	ldi	r16, 0x82		;
	rcall	rfm_exchg		;
;	ldi	r17, 0xce		;sync pattern 0xd4
;	ldi	r16, 0xd4		;
;	rcall	rfm_exchg		;
;	ldi	r17, 0xc4		;AFC independent of VDI,-4..+3df range limit,fi,oe,en
;	ldi	r16, 0xf7		;
;	rcall	rfm_exchg		;
	ldi	r17, 0x98		;45kHz deviation,-2.5dbm power
	ldi	r16, 0x21		;
	rcall	rfm_exchg		;
	ldi	r17, 0x82		;er,ebb,es,ex
	ldi	r16, 0xd8		;
	rcall	rfm_exchg		;
	ldi	r17, 0xcc		;>=5MHz CLK,ddi
	ldi	r16, 0x76		;
	rcall	rfm_exchg		;
	ldi	r17, 0xe0		;wakeup disable
	ldi	r16, 0x00		;
	rcall	rfm_exchg		;
	ldi	r17, 0xc8		;low duty cycle disable
	ldi	r16, 0x00		;
	rcall	rfm_exchg		;
	ldi	r17, 0xc0		;5MHz uC clock,2.2V low battery
	ldi	r16, 0xc0		;
	rcall	rfm_exchg		;
	clr	r17		;read status
	rcall	rfm_exchg		;

	ldi	r16, 0x0a		;prepare clock start
	sei			;globally enable interrupts
	out	TCCR1B, r16		;(0a) start clock
	rjmp	m_lamps		;start with lamp switch

;**************************************************
;******************* main loop ********************
;**************************************************
main:

;sensor commmunication
	ldd	r16, Y+d_sensorcnt	;get sensor comm state counter
	inc	r16		;inc counter
	breq	m_sensenot		;comm state 'inactive'
	cpi	r16, 9		;
	brlo	m_senscsnd		;comm state 'send command'
	breq	m_senscack		;comm state 'receive command ack'
	cpi	r16, 10		;
	breq	m_senswait		;comm state 'wait for measurement'
	cpi	r16, 19		;
	brlo	m_sensrecv		;comm state 'receive data'
	breq	m_sensrack		;comm state 'send 1. data ack'
	cpi	r16, 28		;
	brlo	m_sensrecv		;comm state 'receive data'
	breq	m_sensrend		;comm state 'receive finished'
				;comm state 'interface reset'
	sbi	PORTD, PD1		;clock high
	cpi	r16, 40		;if reset finished
	brlo	m_senscerr		;
	ldi	r16, -1		;then set comm state 'inactive'
m_senscerr:	rjmp	m_sensscnt		;

m_senscsnd:	ldd	r15, Y+d_sensorcmd	;get command shift reg
	sbrs	r15, 7		;if next bit 0
	sbi	DDRD, PD0		;then data low
	sbrc	r15, 7		;if next bit 1
	cbi	DDRD, PD0		;then data high
	sbi	PORTD, PD1		;clock high
	add	r15, r15		;shift command
	std	Y+d_sensorcmd, r15	;write back
	cpi	r16, 8		;last command bit -> set Z
	rjmp	m_sensscnt		;(switch to data input)

m_senscack:	sbi	PORTD, PD1		;clock high
	sbic	PIND, PD0		;if data high
	rjmp	m_senserr		;then error
	clz			;else clear Z
	rjmp	m_sensscnt		;(don't switch to data input)

m_sensrecv:	sbi	PORTD, PD1		;clock high
	ldd	r14, Y+d_sensorinl	;get input shift reg
	ldd	r15, Y+d_sensorinh	;
	add	r14, r14		;shift input
	adc	r15, r15		;
	sbic	PIND, PD0		;if data high
	inc	r14		;set input bit 0
	std	Y+d_sensorinl, r14	;write back
	std	Y+d_sensorinh, r15	;
	rjmp	m_sensscnt		;(data input is already active)

m_sensrack:	sbi	DDRD, PD0		;data low
	sbi	PORTD, PD1		;clock high
	sez			;set Z
	rjmp	m_sensscnt		;(switch to data input)

m_sensrend:	sbi	PORTD, PD1		;clock high
	ldi	r16, -1		;set comm state 'inactive'
	rjmp	m_sensscnt		;(data input is already active)

m_senserr:	ldi	r16, 29		;set comm state 'interface reset'
	sez			;set Z
	rjmp	m_sensscnt		;(switch to data input)

m_senswait:	sbis	PIND, PD0		;
m_sensscnt:	std	Y+d_sensorcnt, r16	;
	cbi	PORTD, PD1		;clock low
	brne	m_sensenot		;if Z set
	cbi	DDRD, PD0		;then data high (input)
m_sensenot:

;wireless transmission
	tst	r10		;transmission activated ?
	breq	m_sendnot		;
	mov	XL, r10		;put offset into X
	ldi	r17, 0x82		;preload tx configuration command
	subi	XL, 2		;adjust offset
	brmi	m_sendprep		;if offset==1 prep transmitter
	sbic	PIND, PD6		;do nothing unless RFM12 Int is low
	rjmp	m_recvnot		;
	cbi	PORTB, PB0		;pull RFM12 EN low (start)
m_sendcont:	subi	XL, -d_pream	;build data pointer
	ld	r15, X		;get data
	ldi	r16, 8		;
m_sendloop:	add	r15, r15		;shift r15 into carry
	brcc	m_sendzero		;
	sbi	PORTB, PB1		;RFM SDO output 1
	rjmp	m_sendone		;
m_sendzero:	cbi	PORTB, PB1		;RFM SDO output 0
m_sendone:	sbi	PORTB, PB3		;RFM SCLK high
	dec	r16		;
	ldd	r14, Y+d_sendend	;load end address early (balances cycle)
	cbi	PORTB, PB3		;RFM SCLK low
	brne	m_sendloop		;loop, while r16 > 0
	cpse	XL, r14		;transmission over ?
	rjmp	m_sendinc		;
	sbi	PORTB, PB0		;RFM12 EN high
	ldi	r16, 0xd8		;er,ebb,es,ex
	rcall	rfm_exchg		;
	clr	r10		;clear counter to end transmission
	rjmp	m_sendnot		;
m_sendprep:	ldi	r16, 0x78		;ebb,et,es,ex
	rcall	rfm_exchg		;
m_sendinc:	inc	r10		;progress counter
	rjmp	m_recvnot		;
m_sendnot:

;wireless reception
	sbic	PIND, PD6		;RFM12 INT request ?
	rjmp	m_recvnot		;

	clr	r17		;read status
	rcall	rfm_exchg		;
	sbrs	r17, 7		;data received ?
	rjmp	m_recverr		;
	ldi	r17, 0xb0		;read FIFO
	clr	r16		;
	rcall	rfm_exchg		;

	tst	r11		;message in progress ?
	brne	m_recvcont		;
	tst	r16		;new message min len 1
	breq	m_recverr		;
	cpi	r16, 18		;max len 17 (excluding checksum)
	brsh	m_recverr		;
	inc	r11		;progress counter
	subi	r16, -2		;adjust end index
	std	Y+d_recvend, r16	;
	std	Y+d_recvstart, r0	;save starttime
	rjmp	m_recvnot		;
m_recvcont:	mov	XL, r11		;put offset into X
	subi	XL, 1-d_data	;build data pointer
	st	X, r16		;
	ldd	r14, Y+d_recvend	;load end index
	inc	r11		;
	cpse	r11, r14		;transmission over ?
	rjmp	m_recvnot		;
	dec	r14		;calculate message length
	dec	r14		;
	rjmp	rfm_newinc		;
m_recvback:
m_recverr:	clr	r11		;
	ldi	r17, 0xca		;FIFO reset and resync
	ldi	r16, 0x80		;8 FIFO Int lvl
	rcall	rfm_exchg		;
	ldi	r17, 0xca		;8 FIFO Int lvl,ff
	ldi	r16, 0x82		;
	rcall	rfm_exchg		;
m_recvnot:

;things to do each second + 0.5
	sbrs	r20, 2		;second + 0.5 update ?
	rjmp	m_nosecs5		;
	cbr	r20, 0x04		;

	tst	r11		;reception in progress ?
	breq	m_norxto		;
	ldd	r15, Y+d_recvstart	;load start seconds
	cp	r0, r15		;compare with current seconds
	brne	m_recverr		;timeout if not equal
m_norxto:
m_nosecs5:

;things to do each second
	sbrs	r20, 3		;seconds update ?
	rjmp	m_nosecs		;
	cbr	r20, 0x08		;

	ldi	r16, 1		;flash once every second
	rcall	flashled		;

	sbrc	r20, 7		;write pending ?
	rcall	writetime		;

	rcall	checkvent		;

	ldd	r16, Y+d_sensorcnt	;
	sbrc	r16, 7		;
	rcall	sense		;

	wdr			;reset watchdog every second
m_nosecs:

;things to do each minute
	sbrs	r20, 4		;minute update ?
	rjmp	m_nomin		;
	cbr	r20, 0x10		;

	rcall	startwrite		;store new time

	ldd	r16, Y+d_vent_o	;decrease vent-on timer
	dec	r16		;
	brmi	m_lamps		;
	std	Y+d_vent_o, r16	;

m_lamps:	clr	r17		;
	ldi	r16, d_prog1_1	;
	rcall	checktime		;
	bld	r17, 0		;
	ldi	r16, d_prog1_2	;
	rcall	checktime		;
	bld	r17, 1		;
	tst	r17		;
	breq	m_1off		;
	sbi	PORTD, PD4		;
	sbr	r21, 0x10		;indicator
	rjmp	m_1on		;
m_1off:	cbi	PORTD, PD4		;
	cbr	r21, 0x10		;indicator
m_1on:
	ldi	r16, d_prog2_1	;
	rcall	checktime		;
	bld	r17, 0		;
	ldi	r16, d_prog2_2	;
	rcall	checktime		;
	bld	r17, 1		;
	tst	r17		;
	breq	m_2off		;
	sbi	PORTD, PD5		;
	sbr	r21, 0x20		;indicator /*& force venting while SON-T on*/
	rjmp	m_2on		;
m_2off:	cbi	PORTD, PD5		;
	cbr	r21, 0x20		;indicator /*& unforce venting*/
m_2on:
m_nomin:

;process user input
	rcall	readkey		;
	tst	r16		;
	breq	m_keyrel		;
	sbrc	r20, 6		;test, whether key was  released
	rjmp	m_nokey		;no -> don't process
	rjmp	input		;
m_keyrel:	cbr	r20, 0x40		;
m_nokey:

;things to do each day
	sbrs	r20, 5		;new day ?
	rjmp	m_noday		;
	cbr	r20, 0x20		;then reload current prog

	rcall	loadprog		;(to undo possible SON-T blackout changes)
m_noday:

;do LED output
	tst	r22		;flash running ?
	brmi	m_ledidle		;no -> cont
	cp	r4, r23		;time to flash now ?
	brne	m_ledidle		;no -> cont
	dec	r22		;decrease flash counter, is it negative yet
	brmi	m_ledidle		;yes -> cont (flash is over)
	sbrs	r22, 0		;on or off now ?
	rjmp	m_loff		;
	sbi	PORTD, PD2		;
	ldi	r16, LED_ON		;
	rjmp	m_lon		;
m_loff:	cbi	PORTD, PD2		;
	ldi	r16, LED_OFF	;
	tst	r22		;is this the last flash
	brne	m_lon		;
	ldi	r16, LED_LAST	;yes -> longer off time
m_lon:	mov	r23, r4		;
	sub	r23, r16		;
	breq	m_lwrap		;
	brpl	m_ledidle		;
m_lwrap:	add	r23, r5		;
m_ledidle:
	rjmp	main

;**************************************************
;***************** main loop end ******************
;**************************************************

;*** process user input
input:	sbr	r20, 0x40		;set flag key acknowledged
	tst	r27		;test menu state
	brne	in_menu		;in progress -> go to menu
	cpi	r16, 7		;direct prog ? (key 1-6)
	brlo	in_direct		;
	breq	in_vent		;key 7: venting
	cpi	r16, 9		;
	breq	in_enter		;key 9: enter menu
	;todo key 8
in_end:	rjmp	m_nokey		;

;set new program
in_direct:	dec	r16		;adjust key->prognr
	mov	r17, r21		;
	cbr	r17, 0xf8		;
	cp	r16, r17		;
	breq	in_nochg		;
	cbr	r21, 0x07		;
	or	r21, r16		;
	inc	r16		;adjust prognr->key
	rcall	flashled		;flash first, loadprog changes r16
	rcall	loadprog		;
in_nochg:	rjmp	m_nokey		;

;manual venting
in_vent:	rcall	keyblock		;
	sbrs	r21, 6		;toggle force venting flag
	rjmp	in_venton		;
	cbr	r21, 0xc0		;switching off also resets venting active
	rjmp	m_nokey		;
in_venton:	sbr	r21, 0x40		;
	rjmp	m_nokey		;

;start menu
in_enter:	ldi	r27, MENU_START	;
	rcall	longflash		;
	rjmp	m_nokey		;

;select menu item
in_menu:	cpi	r27, MENU_START	;
	breq	mn_start		;
	mov	r17, r27		;
	cbr	r17, 0x0f		;
	cpi	r17, MENU_SERIAL	;
	brlo	mn_time		;
	breq	mn_serl		;
	;cpi	r17, MENU_DUMMY4	;
	;brlo	mn_dummy3		;
	;breq	mn_dummy4		;
	;cpi	r17, MENU_DUMMY6	;
	;brlo	mn_dummy5		;
	;breq	mn_dummy6		;
	;cpi	r17, MENU_DUMMY8	;
	;brlo	mn_dummy7		;
	;breq	mn_dummy8		;
	;rjmp	mn_dummy9		;

mn_exit:	clr	r27		;no defined menu item - leave menu
	rcall	longflash		;
	rjmp	m_nokey		;

;acknowledge menu item selected
mn_start:	swap	r16		;key = menu item
	mov	r27, r16		;
	ldi	r16, 2		;2 flashes for menu start
	rcall	flashled		;
	clr	r16		;clear menu memory
	std	Y+d_menu1, r16	;
	std	Y+d_menu2, r16	;
	rjmp	m_nokey		;

mn_time:	sbrc	r27, 0		;1st or 2nd part of time ?
	rjmp	mn_time2		;
	cpi	r16, 9		;invalid input ?
	breq	mn_exit		;
	dec	r16		;0-7
	mov	r17, r16		;multiply by 3
	add	r17, r16		;(hours offset)
	add	r17, r16		;
	std	Y+d_menu1, r17	;store for next input
	inc	r27		;next time 2nd part in
	rcall	keyblock		;
	rjmp	m_nokey		;
mn_time2:	ldd	r17, Y+d_menu1	;get hours offset
	clr	r15		;set minutes to 00
mn_timelp:	dec	r16		;
	breq	mn_timeset		;ready -> set time
	add	r15, r5		;add 20min
	cpse	r15, r6		;full hour ?
	rjmp	mn_timelp		;
	inc	r17		;
	clr	r15		;
	rjmp	mn_timelp		;
mn_timeset:	cli			;
	clr	r0		;set time
	mov	r1, r15		;
	mov	r2, r17		;
	mov	r4, r5		;
	sei			;
	rcall	startwrite		;store new time
	rjmp	mn_exit		;

mn_serl:	cpi	r16, 8		;premature end of programming ?
	breq	mn_exit		;-> leave serial reception
	rjmp	m_nokey		;-> else stay


;**************************************************
;****************** subroutines *******************
;**************************************************

;starts T or rH acquisition
;changes r14,r15,r16
sense:	sbi	PORTD, PD1		;CLK high
	ldd	r14, Y+d_sensorinl	;
	ldd	r15, Y+d_sensorinh	;
	sbi	DDRD, PD0		;DATA low
	clr	r16		;
	std	Y+d_sensorcnt, r16	;
	ldi	r16, 0x0f		;
	cbi	PORTD, PD1		;CLK low
	sbrc	r21, 3		;
	rjmp	se_meas_h		;
	add	r14, r14		;normalize 14bit T input
	adc	r15, r15		;
	add	r14, r14		;
	adc	r15, r15		;
	sbrc	r14, 7		;rounding
	inc	r15		;
	set			;
	ldi	r16, CMD_H_MEAS	;
	sbi	PORTD, PD1		;CLK high
	std	Y+d_temp, r15	;
	rjmp	se_meas_e		;
se_meas_h:	and	r15, r16		;normalize 12bit rH input
	swap	r15		;
	swap	r14		;
	and	r16, r14		;
	or	r15, r16		;
	sbrc	r14, 7		;rounding
	inc	r15		;
	sbi	PORTD, PD1		;CLK high
	clt			;
	ldi	r16, CMD_T_MEAS	;
	std	Y+d_humi, r15	;
se_meas_e:	cbi	DDRD, PD0		;DATA high
	std	Y+d_sensorcmd, r16	;
	bld	r21, 3		;
	cbi	PORTD, PD1		;CLK low
	ret

;loads current addressed prog into config
;changes r15,r16,r17,XL
loadprog:	mov	r16, r21		;
	cbr	r16, 0xf8		;
	cpi	r16, 6		;
	brlo	lp_direct		;
	ldi	r16, DEFAULTPROG	;
lp_direct:	swap	r16		;
	subi	r16, -e_config	;
	ldi	r17, 16		;
	ldi	XL, d_vars+d_program	;
lp_eewait:	sbic	EECR, EEWE		;
	rjmp	lp_eewait		;
lp_eeget:	out	EEAR, r16		;
	sbi	EECR, EERE		;
	in	r15, EEDR		;
	st	X+, r15		;
	inc	r16		;
	dec	r17		;
	brne	lp_eeget		;
	ret

;reloads thresholds
;changes r15,r16,r17,XL
loadthrsh:	ldi	r16, e_threshold	;get thresholds
	ldi	r17, 4		;
	ldi	XL, d_vars+d_threshold	;
	rjmp	lp_eewait		;

;reloads time
;changes r15,r16,r17,XL
loadtime:	ldi	r16, e_time		;get time
	ldi	r17, 12		;
	ldi	XL, scrap		;
	rjmp	lp_eewait		;

;test, if conditions (temperature/humidity, override, ...) require venting. vent if they do
;changes r15,r16,r17
checkvent:	sbrc	r21, 6		;forced venting
	rjmp	cv_yes		;
	clt			;
	ldi	r16, 30		;
	cp	r0, r16		;
	brne	cv_sensor		;
	set			;
	tst	r1		;thirty minute venting
	breq	cv_yes		;
	cp	r1, r16		;
	breq	cv_yes		;
cv_sensor:	ldd	r15, Y+d_uth_temp	;temperature / humidity control
	ldd	r16, Y+d_temp	;
	cp	r15, r16		;T > upper
	brlo	cv_yes		;-> vent
	ldd	r15, Y+d_uth_humi	;
	ldd	r17, Y+d_humi	;
	cp	r15, r17		;H > upper
	brlo	cv_yes		;-> vent
	ldd	r15, Y+d_lth_temp	;
	cp	r16, r15		;T >= lower
	brsh	cv_same		;-> no change
	ldd	r15, Y+d_lth_humi	;
	cp	r17, r15		;H >= upper
	brsh	cv_same		;-> no change
cv_no:	ldd	r16, Y+d_vent_o	;all else -> venting should stop
	tst	r16		;but only after 5 minutes vent time
	brne	cv_exit		;
	brtc	cv_exit		;and only on the 30th second
	cbr	r21, 0x80		;
	cbi	PORTD, PD3		;
cv_exit:	ret
cv_same:	sbrs	r21, 7		;test, if currently venting
	rjmp	cv_no		;if not, leave off
cv_yes:	ldi	r16, MIN_VENT+1	;
	sbrs	r21, 7		;set timer only if vent just started
	std	Y+d_vent_o, r16	;
	sbr	r21, 0x80		;start venting
	sbi	PORTD, PD3		;
	ret

;test, if current time is in defined window. set T if it is
;r16 offset of window definition (start h, start m, stop h, stop m) relative to Y
;changes r14,r15,XL
checktime:	mov	XL, YL		;
	add	XL, r16		;
	ld	r14, X+		;start h
	ld	r15, X+		;start m
	cp	r2, r14		;
	brlo	ct_no		;
	brne	ct_end		;
	cp	r1, r15		;
	brlo	ct_no		;
ct_end:	ld	r14, X+		;stop h
	ld	r15, X		;stop m
	cp	r14, r7		;
	brsh	ct_no		;
	cp	r15, r6		;
	brsh	ct_no		;
	cp	r14, r2		;
	brlo	ct_no		;
	brne	ct_ok		;
	cp	r15, r1		;
	brlo	ct_no		;
ct_ok:	set			;
	ret
ct_no:	clt			;
	ret

;flashes led, if no flash in progress
;r16 number of flashes
;changes r16
flashled:	tst	r22		;
	brpl	fl_no		;
	mov	r23, r4		;
	subi	r23, LED_ON		;
	breq	fl_wrap		;
	brpl	fl_add		;
fl_wrap:	add	r23, r5		;
fl_add:	lsl	r16		;
	mov	r22, r16		;
	dec	r22		;
	sbi	PORTD, PD2		;
fl_no:	ret

;flashes led long once, if no flash in progress
longflash:	tst	r22		;
	brpl	lf_no		;
	mov	r23, r4		;
	subi	r23, LED_LONG	;
	breq	lf_wrap		;
	brpl	lf_add		;
lf_wrap:	add	r23, r5		;
lf_add:	ldi	r22, 1		;
	sbi	PORTD, PD2		;
lf_no:	ret

;blocks input for a short while, if no flash in progress
keyblock:	tst	r22		;
	brpl	kb_no		;
	mov	r23, r4		;
	subi	r23, LED_OFF	;
	breq	kb_wrap		;
	brpl	kb_add		;
kb_wrap:	add	r23, r5		;
kb_add:	clr	r22		;
kb_no:	ret

;reads input, if no flash in progress
;returns input in r16 (0=none)
readkey:	clr	r16		;
	tst	r22		;
	brpl	rk_no		;
	in	r16, PINB		;
	com	r16		;
	swap	r16		;
	cbr	r16, 0xf0		;
rk_no:	ret

;initiate non-volatile storing of time and program
;changes r16
startwrite:	sbr	r20, 0x80		;set write pending
	ldi	r16, e_time		;init address
	std	Y+d_write_ad, r16	;
	clr	r16		;init checksum
	std	Y+d_write_cs, r16	;
	ret

;saves one state register to EEPROM
;changes r14,r15,r16,r17,XL
writetime:	ldd	r16, Y+d_write_ad	;
	ldd	r14, Y+d_write_cs	;
	ldi	r17, e_time		;init offset correction block 1
	cpi	r16, e_program	;
	brlo	wt_donext1		;-> time 1
	breq	wt_doprog		;-> program 1
	cpi	r16, e_chksum	;
	breq	wt_do_cs		;-> checksum 1
	cpi	r16, e_prog_bu	;
	brlo	wt_donext2		;-> time 2
	breq	wt_doprog		;-> program 2
	sbrs	r20, 0		;test extended write
	rjmp	wt_do_end		;-> checksum 2 and end
	cpi	r16, e_chks_bu+1	;
	brlo	wt_do_cs		;-> checksum 2
	brne	wt_ext1		;
	ldd	r16, Y+d_wrext_ad	;fetch extended write first
	ldd	r14, Y+d_wrext_en	;and last address in EEPROM
wt_ext1:	mov	XL, r16		;build ewr address
	sub	XL, r14		;
	brne	wt_ext2		;last byte ?
	cbr	r20, 0x81		;end write cycle
wt_ext2:	subi	XL, -d_ewrl		;build ewr address continued
	ld	r15, X		;
	rjmp	wt_eewait		;
wt_do_end:	cbr	r20, 0x80		;end write cycle
wt_do_cs:	mov	r15, r14		;get checksum
	clr	r14		;reset
	rjmp	wt_eewait		;
wt_doprog:	mov	r15, r21		;write r21 (includes program nr)
	ldi	r17, 1		;init checksum iterator
	rjmp	wt_calc		;
wt_donext2:	ldi	r17, e_time_bu	;set offset correction block 2
wt_donext1:	mov	XL, r16		;
	sub	XL, r17		;convert to register offset
	ld	r15, X		;
	ldi	r17, 5		;init checksum iterator
	sub	r17, XL		;
wt_calc:	add	r17, r15		;calculate next checksum iteration
	eor	r14, r17		;
	swap	r14		;
wt_eewait:	sbic	EECR, EEWE		;
	rjmp	wt_eewait		;
	out	EEAR, r16		;write data into EEPROM
	out	EEDR, r15		;
	cli			;
	sbi	EECR, EEMWE		;
	sbi	EECR, EEWE		;
	sei			;
	inc	r16		;
	std	Y+d_write_ad, r16	;store address and checksum for next round
	std	Y+d_write_cs, r14	;
	ret

;tests integrity of EEPROM data
;XL pointer to start of data in SRAM
;clears Z flag, if data is corrupted, XL points to first byte behind data
;changes r0,r1,r2,r3,r4,r15,r16,r17,r21,XL,ZL
testtime:	ldi	ZL, 0		;register offset
	clr	r16		;
	ldi	r17, 5		;
tt_loop:	ld	r15, X+		;get data
	st	Z+, r15		;store in register
	add	r15, r17		;
	eor	r16, r15		;
	swap	r16		;
	dec	r17		;
	brne	tt_loop		;
	mov	r21, r4		;
	cbr	r21, 0xf8		;clear transient status flags
	ld	r15, X+		;get checksum
	cp	r15, r16		;test and return with result
	ret

;exchanges 16bit data with the RFM12B module
;r17:r16 data to write
;changes XL
;returns read data in r17:r16
rfm_exchg:	cbi	PORTB, PB0		;RFM EN low
	ldi	r26, 16		;
re_loop:	add	r16, r16		;shift r17:r16 into carry
	adc	r17, r17		;
	brcc	re_zero		;
	sbi	PORTB, PB1		;output 1
	rjmp	re_one		;
re_zero:	cbi	PORTB, PB1		;
re_one:	sbi	PORTB, PB3		;RFM SCLK high
	sbic	PINB, PB2		;read RFM SDI
	inc	r16		;put databit into r16
	dec	r26		;
	cbi	PORTB, PB3		;RFM SCLK low
	brne	re_loop		;loop, while r26 > 0
	sbi	PORTB, PB0		;RFM EN high
	ret

;calculate checksum from data
;XL pointer to start of data in SRAM
;r14 length of message
;changes r14,r16
;returns checksum in r15, next address after data in XL
rfm_chksum:	clr	r15		;
rc_ltest:	ld	r16, X+		;iterate over message
	add	r16, r14		;and calculate checksum
	eor	r15, r16		;
	swap	r15		;
	dec	r14		;
	brne	rc_ltest		;
	ret

;new message received
;r14 length of message without checksum
;changes r14,r15,r16,r17,XL,ZL
;no return
rfm_newinc:	ldi	XL, d_data		;test message integrity
	ld	r17, X		;
	rcall	rfm_chksum		;
	ld	r16, X		;
	cp	r15, r16		;test checksum
	brne	rn_exit		;

	ldi	XL, d_pream+3	;load return message start (len byte)

	cpi	r17, S_SETTHRESH	;
	breq	rn_setthsh		;
	brpl	rn_setprog		;
	cpi	r17, S_STATUS	;
	breq	rn_sendst		;
	brpl	rn_settime		;

rn_exit:	rjmp	m_recvback		;

;send status
rn_sendst:	ldi	r16, 8		;length of packet
	st	X+, r16		;
	ldi	r16, S_STATUS	;status preamble
	st	X+, r16		;
	st	X+, r21		;hardware status with program
	ldd	r16, Y+d_temp	;T value
	st	X+, r16		;
	ldd	r16, Y+d_humi	;H value
	st	X+, r16		;
	st	X+, r3		;day
	st	X+, r2		;hour
	st	X+, r1		;minute
	st	X+, r0		;second
	rjmp	rn_dosend		;

;set new thresholds
rn_setthsh:	ldi	r16, 1		;length of packet
	st	X+, r16		;
	sbrc	r20, 0		;unwritten EEPROM data ?
	rjmp	rn_doerror		;
	inc	XL		;echo command

	ldi	r17, 4		;
	ldi	ZL, d_vars+d_threshold	;
rn_copy2:	ld	r14, X+		;
	st	Z, r14		;
	subi	ZL, d_vars+d_threshold-d_ewrl+3	;
	st	Z+, r14		;
	subi	ZL, d_ewrl-3-d_vars-d_threshold	;
	dec	r17		;
	brne	rn_copy2		;

	ldi	r16, e_threshold	;
	std	Y+d_wrext_ad, r16	;thresholds in EEPROM
	subi	r16, -3		;
	std	Y+d_wrext_en, r16	;
	ldi	XL, d_data+1	;readjust X
	sbr	r20, 0x01		;flag extended write
	rjmp	rn_dosend		;

;set new program
rn_setprog:	subi	r17, S_SETPROG	;calculate program offset
	cpi	r17, 6		;
	brsh	rn_sendpr		;invalid -> report current setting

	ldi	r16, 1		;length of packet
	st	X+, r16		;
	sbrc	r20, 0		;unwritten EEPROM data ?
	rjmp	rn_doerror		;
	inc	XL		;echo command

	ldi	r16, 16		;
	ldi	ZL, d_ewrl-15	;
rn_copy1a:	ld	r14, X+		;
	st	Z+, r14		;
	dec	r16		;
	brne	rn_copy1a		;

	swap	r17		;multiply by 16 (because bit 4-7 = 0)
	subi	r17, -e_config	;
	std	Y+d_wrext_ad, r17	;thresholds in EEPROM
	subi	r17, -15		;
	std	Y+d_wrext_en, r17	;
	ldi	XL, d_data+1	;readjust X
	sbr	r20, 0x01		;flag extended write
	rjmp	rn_dosend		;

;set new time
rn_settime:	ldi	r16, 1		;length of packet
	st	X+, r16		;
	inc	XL		;echo command
	cli			;
	ld	r3, X+		;set day
	ld	r2, X+		;set hour
	ld	r1, X+		;set minute
	ld	r0, X		;set second
	sei			;
	ldi	XL, d_data+1	;readjust X
	rcall	startwrite		;force EEPROM update
	rjmp	rn_dosend		;

rn_sendpr:	ldi	r16, 17		;length of packet
	st	X+, r16		;
	inc	XL		;echo command

	ldi	r16, 16		;send current program data
	ldi	ZL, d_vars+d_program	;
rn_copy1b:	ld	r14, Z+		;
	st	X+, r14		;
	dec	r16		;
	brne	rn_copy1b		;
;	rjmp	rn_dosend		;

rn_dosend:	mov	r14, XL		;calculate message length
	ldi	XL, d_data		;and load XL with start of data
	sub	r14, XL		;
	rcall	rfm_chksum		;calculate checksum
	st	X+, r15		;and store after message
	ldi	r16, 0xaa		;dummy
	st	X+, r16		;
	st	X, r16		;
	std	Y+d_sendend, XL	;store end address
	inc	r10		;start transmission
	rjmp	m_recvback		;

rn_doerror:	ldi	r16, S_BUSY		;busy flag indicates unwritten data
	st	X+, r16		;
	rjmp	rn_dosend		;


;**************************************************
;***************** end of program *****************
;**************************************************

.EXIT


