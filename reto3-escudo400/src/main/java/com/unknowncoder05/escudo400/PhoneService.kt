package com.unknowncoder05.escudo400

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object LiveCalls {
    val calls = mutableStateListOf<Call>()
    var revision by mutableIntStateOf(0)
}

// Android 7–9 exige un teléfono predeterminado para enlazar CallScreeningService.
// Las llamadas que el filtro permite conservan controles para responder y colgar.
class PhoneService : InCallService() {
    private val callbacks = mutableMapOf<Call, Call.Callback>()
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        val callback = object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) { LiveCalls.revision++ }
            override fun onDetailsChanged(call: Call, details: Call.Details) { LiveCalls.revision++ }
        }
        callbacks[call] = callback
        call.registerCallback(callback)
        LiveCalls.calls.add(call)
        startActivity(Intent(this, CallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onCallRemoved(call: Call) {
        callbacks.remove(call)?.let(call::unregisterCallback)
        LiveCalls.calls.remove(call)
        super.onCallRemoved(call)
    }

    override fun onDestroy() {
        callbacks.forEach { (call, callback) -> call.unregisterCallback(callback) }
        callbacks.clear()
        LiveCalls.calls.clear()
        super.onDestroy()
    }
}

class CallActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 27) { setShowWhenLocked(true); setTurnScreenOn(true) }
        else window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        setContent {
            EscudoTheme {
                val revision = LiveCalls.revision
                val calls = LiveCalls.calls.toList()
                LaunchedEffect(calls.isEmpty()) { if (calls.isEmpty()) finish() }
                LazyColumn(Modifier.fillMaxSize().safeDrawingPadding(), contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    item {
                        Column {
                            Text("Escudo 400 · Teléfono", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                            TextButton(onClick = {
                                startActivity(Intent(this@CallActivity, MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                            }) { Text("Volver a la app") }
                        }
                    }
                    items(calls, key = { it.hashCode() }) { call ->
                        val state = remember(revision, call) { call.state }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                                Text(call.details.handle?.schemeSpecificPart ?: "Número oculto", fontSize = 26.sp)
                                Text(when (state) {
                                    Call.STATE_RINGING -> "Llamada entrante"
                                    Call.STATE_ACTIVE -> "Llamada en curso"
                                    Call.STATE_HOLDING -> "En espera"
                                    Call.STATE_DISCONNECTED -> "Finalizada"
                                    else -> "Conectando…"
                                })
                                if (state == Call.STATE_RINGING) {
                                    Button(onClick = { call.answer(VideoProfile.STATE_AUDIO_ONLY) }) { Text("Responder") }
                                    OutlinedButton(onClick = { call.reject(false, null) }) { Text("Rechazar") }
                                } else if (state != Call.STATE_DISCONNECTED) {
                                    Button(onClick = { call.disconnect() }) { Text("Colgar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
