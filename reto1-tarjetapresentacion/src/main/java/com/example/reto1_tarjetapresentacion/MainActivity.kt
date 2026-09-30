package com.example.reto1_tarjetapresentacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto1_tarjetapresentacion.ui.theme.Reto1TarjetaPresentacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto1TarjetaPresentacionTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TarjetaPresentacion()
                }
            }
        }
    }
}

// Adaptado del reto de joscanoav: Column, foto circular, nombre, rol y Button.
@Composable
fun TarjetaPresentacion(
    modifier: Modifier = Modifier,
    onOpenGitHub: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val githubUrl = stringResource(R.string.github_url)
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.foto_perfil),
            contentDescription = stringResource(R.string.profile_image_description),
            modifier = Modifier.size(150.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.profile_name),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.profile_role),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { onOpenGitHub?.invoke() ?: openGitHub(context, githubUrl) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.github_button), textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaPreview() {
    Reto1TarjetaPresentacionTheme {
        TarjetaPresentacion(onOpenGitHub = {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480, fontScale = 1.5f)
@Composable
fun TarjetaSmallScreenPreview() {
    Reto1TarjetaPresentacionTheme {
        TarjetaPresentacion(onOpenGitHub = {})
    }
}
