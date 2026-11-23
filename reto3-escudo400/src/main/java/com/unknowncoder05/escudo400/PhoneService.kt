package com.unknowncoder05.escudo400

import android.telecom.InCallService

// Legacy Android needs a default dialer to bind the screening service.
// This service supplies no call UI: Telecom uses the built-in phone screen.
class PhoneService : InCallService()
