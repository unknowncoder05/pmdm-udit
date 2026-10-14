package com.unknowncoder05.localburger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Verde = Color(0xFF244C3C)
private val Crema = Color(0xFFF7F7F2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TemaLocalBurger { CatalogoLocales() } }
    }
}

@Composable
private fun TemaLocalBurger(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Verde, background = Crema)) {
        Surface(Modifier.fillMaxSize(), color = Crema, content = contenido)
    }
}

@Composable
fun CatalogoLocales(omitirCarga: Boolean = false) {
    var mostrandoPortada by rememberSaveable { mutableStateOf(!omitirCarga) }
    var operacion by rememberSaveable { mutableStateOf("Todos") }
    var seleccionadoId by rememberSaveable { mutableStateOf<String?>(null) }
    var compraSimulada by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1500); mostrandoPortada = false }

    if (mostrandoPortada) {
        Column(Modifier.fillMaxSize().safeDrawingPadding(),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.ic_local), null, Modifier.size(72.dp))
            Text("LOCAL BURGER", Modifier.padding(20.dp), fontWeight = FontWeight.Bold)
            CircularProgressIndicator(color = Verde)
            Text("Encontrando tu próximo local…", Modifier.padding(20.dp))
        }
        return
    }

    val disponibles = locales.filter { operacion == "Todos" || it.operacion == operacion }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 250.dp),
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.ic_local), null, Modifier.size(40.dp))
                    Text("LOCAL BURGER", Modifier.padding(start = 12.dp), color = Verde, fontWeight = FontWeight.Bold)
                }
                Text("Tu próximo negocio\nya tiene local.", fontSize = 34.sp, lineHeight = 39.sp,
                    fontFamily = FontFamily.Serif, color = Verde)
                Text("Hamburgueserías en venta y traspaso. Encuentra el espacio para tu próxima idea.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "Traspaso", "Venta").forEach { opcion ->
                        FilterChip(operacion == opcion, { operacion = opcion }, label = { Text(opcion) })
                    }
                }
                Text("${disponibles.size} locales disponibles", fontSize = 14.sp, color = Color.DarkGray)
            }
        }
        items(disponibles, key = { it.id }) { local ->
            TarjetaLocal(local) { seleccionadoId = local.id; compraSimulada = false }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("Proyecto académico · Negocios y precios ficticios · Fotografías ilustrativas · Sin pagos reales",
                fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
        }
    }

    val seleccionado = locales.find { it.id == seleccionadoId }
    if (seleccionado != null) {
        AlertDialog(
            onDismissRequest = { seleccionadoId = null },
            title = { Text(if (compraSimulada) "Compra simulada" else seleccionado.nombre) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (compraSimulada) {
                        Text("Has seleccionado ${seleccionado.nombre} por ${precioEnEuros(seleccionado.precio)}.")
                        Text("Esta es una demostración: no se ha realizado ningún pago ni una compraventa real.")
                    } else {
                        Text(seleccionado.ubicacion)
                        Text(precioEnEuros(seleccionado.precio), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Verde)
                        Text("${seleccionado.superficie} m² · ${seleccionado.plazas} plazas · ${seleccionado.operacion}")
                        Text(seleccionado.descripcion)
                        Text("Negocio ficticio. La compra es una simulación sin pagos.", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (compraSimulada) seleccionadoId = null else compraSimulada = true
                }) { Text(if (compraSimulada) "Entendido" else "Simular compra") }
            },
            dismissButton = {
                if (!compraSimulada) TextButton(onClick = { seleccionadoId = null }) { Text("Volver") }
            }
        )
    }
}

@Composable
private fun TarjetaLocal(local: Local, verLocal: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Image(painterResource(local.imagen), "Interior ilustrativo de ${local.nombre}",
            modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("${local.operacion} · ${local.ubicacion}", fontSize = 12.sp, color = Verde,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(local.nombre, fontSize = 25.sp, fontFamily = FontFamily.Serif,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${local.superficie} m² · ${local.plazas} plazas", fontSize = 14.sp)
            Text(precioEnEuros(local.precio), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Verde)
            Button(verLocal, Modifier.fillMaxWidth()) { Text("Ver local") }
        }
    }
}

@Preview(name = "Móvil", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun VistaMovil() { TemaLocalBurger { CatalogoLocales(omitirCarga = true) } }

@Preview(name = "Tablet", widthDp = 1024, heightDp = 768, showBackground = true)
@Composable
private fun VistaTablet() { TemaLocalBurger { CatalogoLocales(omitirCarga = true) } }
