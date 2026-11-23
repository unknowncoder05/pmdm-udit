ADB ?= adb
DEVICE ?= emulator-5580
NUMBER ?= 400123456
export ADB DEVICE NUMBER

.PHONY: call call-cancel call-list

# Simulate an incoming call; no real phone number is dialled.
call:
	"$$ADB" -s "$$DEVICE" emu gsm call "$$NUMBER"

call-cancel:
	"$$ADB" -s "$$DEVICE" emu gsm cancel "$$NUMBER"

call-list:
	"$$ADB" -s "$$DEVICE" emu gsm list
