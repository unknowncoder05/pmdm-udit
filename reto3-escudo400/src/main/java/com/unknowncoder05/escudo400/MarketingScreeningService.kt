package com.unknowncoder05.escudo400

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

class MarketingScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        if (Build.VERSION.SDK_INT >= 29 && callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return
        val raw = callDetails.handle?.schemeSpecificPart
        val store = CallStore.get(this)
        // La decisión es local y se responde antes de escribir el historial.
        val decision = try {
            CallPolicy.decide(raw, store.enabled, store.contains(CallPolicy.normalize(raw)))
        } catch (error: Exception) {
            Log.e("Escudo400", "No se pudo leer la configuración; se permite la llamada", error)
            Decision(false, Reason.DISABLED)
        }
        respondToCall(callDetails, CallResponse.Builder()
            .setDisallowCall(decision.blocked)
            .setRejectCall(decision.blocked)
            .setSkipCallLog(false)
            .setSkipNotification(decision.blocked)
            .build())
        try { store.record(raw, decision) }
        catch (error: Exception) { Log.e("Escudo400", "No se pudo guardar la actividad", error) }
    }
}
