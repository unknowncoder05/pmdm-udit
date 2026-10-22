package com.unknowncoder05.escudo400

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.Date

private val Navy = Color(0xFF153646)
private val Background = Color(0xFFF4F7F8)

@Composable
fun EscudoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF006A67),
        secondaryContainer = Color(0xFFD7EFE7), background = Background)) {
        Surface(Modifier.fillMaxSize(), color = Background, content = content)
    }
}

class MainActivity : ComponentActivity() {
    private var resumed by mutableIntStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EscudoTheme { EscudoApp(this, resumed) } }
    }
    override fun onResume() { super.onResume(); resumed++ }

    fun isSelected(): Boolean = if (Build.VERSION.SDK_INT >= 29) {
        getSystemService(RoleManager::class.java).isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    } else getSystemService(TelecomManager::class.java).defaultDialerPackage == packageName

    @Suppress("DEPRECATION")
    fun requestActivation() {
        val request = if (Build.VERSION.SDK_INT >= 29) {
            val manager = getSystemService(RoleManager::class.java)
            if (!manager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                message("Este dispositivo no ofrece filtrado de llamadas."); return
            }
            manager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        } else Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        try { startActivity(request) }
        catch (_: android.content.ActivityNotFoundException) { message("No se puede activar el servicio en este dispositivo.") }
    }

    fun placeCall(number: String) {
        try {
            getSystemService(TelecomManager::class.java).placeCall(Uri.fromParts("tel", number, null), Bundle())
        } catch (_: SecurityException) { message("Necesitas permitir llamadas y seleccionar Escudo 400 como teléfono.") }
    }

    fun message(text: String) { Toast.makeText(this, text, Toast.LENGTH_LONG).show() }
}

@Composable
private fun EscudoApp(activity: MainActivity, resumed: Int) {
    val store = remember { CallStore.get(activity) }
    val revision = store.revision
    val allowed = remember(revision) { store.allowedNumbers() }
    val events = remember(revision) { store.events() }
    val selected = remember(resumed) { activity.isSelected() }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize().safeDrawingPadding()) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_escudo), null, Modifier.size(44.dp))
            Column(Modifier.padding(start = 12.dp)) {
                Text("ESCUDO 400", fontWeight = FontWeight.Bold, fontSize = 23.sp, color = Navy)
                Text("Tu teléfono, sin interrupciones comerciales", fontSize = 12.sp)
            }
        }
        TabRow(selectedTabIndex = tab) {
            listOf("Protección", "Lista blanca", "Actividad").forEachIndexed { index, label ->
                Tab(selected = tab == index, onClick = { tab = index }, text = { Text(label) })
            }
        }
        if (LiveCalls.calls.isNotEmpty()) TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { activity.startActivity(Intent(activity, CallActivity::class.java)) }
        ) { Text("Volver a la llamada") }
        when (tab) {
            0 -> Protection(activity, store, selected, allowed.size, events, resumed)
            1 -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("Números que siempre pueden llamarte", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Navy)
                    Text("Las excepciones tienen prioridad sobre el bloqueo 400. Se guardan solo en este dispositivo.", Modifier.padding(vertical = 12.dp))
                    Button(onClick = { editingId = null; editorOpen = true }) { Text("Añadir número") }
                }
                if (allowed.isEmpty()) item { Text("La lista blanca está vacía.") }
                items(allowed, key = { it.id }) { entry ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                            Text(entry.phone, Modifier.padding(top = 6.dp))
                            Row {
                                TextButton(onClick = { editingId = entry.id; editorOpen = true }) { Text("Editar") }
                                TextButton(onClick = { deletingId = entry.id }) { Text("Eliminar") }
                            }
                        }
                    }
                }
            }
            2 -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("Últimas llamadas filtradas", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Navy)
                    Text("Se conservan las últimas 100 decisiones del servicio de Android.", Modifier.padding(top = 8.dp))
                }
                if (events.isEmpty()) item { Text("Todavía no se ha recibido ninguna llamada para filtrar.") }
                items(events, key = { it.id }) { event ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(if (event.blocked) "Bloqueada" else "Permitida", fontWeight = FontWeight.Bold,
                                color = if (event.blocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            Text(event.phone, fontSize = 19.sp)
                            Text(event.reason.label)
                            Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(event.time)), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    if (editorOpen) NumberEditor(allowed.find { it.id == editingId }, store) { editorOpen = false }
    val deleting = allowed.find { it.id == deletingId }
    if (deleting != null) AlertDialog(onDismissRequest = { deletingId = null },
        title = { Text("Eliminar de la lista blanca") },
        text = { Text("${deleting.name} (${deleting.phone}) dejará de ser una excepción. Si empieza por 400 volverá a bloquearse.") },
        confirmButton = { TextButton(onClick = { store.delete(deleting.id); deletingId = null }) { Text("Confirmar eliminación") } },
        dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } })
}

@Composable
private fun Protection(activity: MainActivity, store: CallStore, selected: Boolean, count: Int, events: List<CallEvent>, resumed: Int) {
    var permissionRevision by remember { mutableIntStateOf(0) }
    val contactsGranted = remember(resumed, permissionRevision) {
        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }
    val contactsRequest = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionRevision++ }
    var dial by rememberSaveable { mutableStateOf(activity.intent.data?.schemeSpecificPart.orEmpty()) }
    val phoneRequest = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) activity.placeCall(dial) else activity.message("Permiso para realizar llamadas denegado.")
    }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFD7EFE7))) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(when { !selected -> "Falta activar el filtro"; store.enabled -> "Protección activa"; else -> "Protección pausada" },
                        fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Navy)
                    Text(if (selected) "Android ha seleccionado Escudo 400 para filtrar las llamadas." else
                        if (Build.VERSION.SDK_INT < 29) "Selecciona Escudo 400 como aplicación de teléfono para activar el bloqueo en Android 7–9."
                        else "Selecciona Escudo 400 como aplicación de filtrado de llamadas.")
                    if (!selected) Button(onClick = activity::requestActivation) { Text("Activar protección") }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Bloquear números 400", Modifier.weight(1f))
                        Switch(checked = store.enabled, onCheckedChange = { store.enabled = it })
                    }
                }
            }
        }
        item {
            Text("Solo el prefijo comercial español", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Navy)
            Text("Se rechazan números de 9 cifras que empiezan por 400, también con +34 o 0034. Los demás números y las excepciones de tu lista blanca se permiten.", Modifier.padding(top = 8.dp))
            Text("$count excepciones · ${events.count { it.blocked }} bloqueos entre las últimas 100 llamadas", Modifier.padding(top = 12.dp), fontSize = 13.sp)
        }
        if (!contactsGranted) item {
            Text("Números guardados en contactos", fontWeight = FontWeight.Bold)
            Text("Android puede omitirlos del filtrado sin este permiso. Permite el acceso para incluir también los contactos; no se importan ni se envían a ningún servidor.", Modifier.padding(top = 8.dp))
            OutlinedButton(onClick = { contactsRequest.launch(Manifest.permission.READ_CONTACTS) }) { Text("Incluir contactos en el filtro") }
        }
        if (Build.VERSION.SDK_INT < 29 && selected) item {
            Text("Teléfono", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Navy)
            OutlinedTextField(value = dial, onValueChange = { dial = it.take(40) }, label = { Text("Número para llamar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            Button(enabled = dial.isNotBlank(), onClick = {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) activity.placeCall(dial)
                else phoneRequest.launch(Manifest.permission.CALL_PHONE)
            }) { Text("Llamar") }
        }
    }
}

@Composable
private fun NumberEditor(entry: AllowedNumber?, store: CallStore, dismiss: () -> Unit) {
    var name by rememberSaveable(entry?.id) { mutableStateOf(entry?.name.orEmpty()) }
    var phone by rememberSaveable(entry?.id) { mutableStateOf(entry?.phone.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = dismiss,
        title = { Text(if (entry == null) "Añadir a la lista blanca" else "Editar número") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it.take(80); error = null },
                    label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it.take(40); error = null },
                    label = { Text("Teléfono") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                Text("Ejemplo: 400 123 456 o +34 400 123 456", fontSize = 12.sp)
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = { TextButton(onClick = {
            try { store.save(name, phone, entry?.id); dismiss() }
            catch (_: SQLiteConstraintException) { error = "Este número ya está en la lista blanca." }
            catch (invalid: IllegalArgumentException) { error = invalid.message }
        }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = dismiss) { Text("Cancelar") } })
}
