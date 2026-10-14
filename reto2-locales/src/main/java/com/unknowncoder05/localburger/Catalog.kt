package com.unknowncoder05.localburger

import androidx.annotation.DrawableRes
import java.text.NumberFormat
import java.util.Locale

// Datos locales de ejemplo, como el catálogo de la referencia de Jorge.
data class Local(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val precio: Int,
    val operacion: String,
    val superficie: Int,
    val plazas: Int,
    val descripcion: String,
    @DrawableRes val imagen: Int
)

val locales = listOf(
    Local("brasa", "Brasa Club", "Malasaña · Madrid", 165000, "Traspaso", 145, 54,
        "Hamburguesería con cocina abierta, barra central y terraza. Incluye el negocio y el equipamiento; el inmueble continúa en alquiler.", R.drawable.local_madrid),
    Local("esquina", "La Esquina Smash", "Gràcia · Barcelona", 245000, "Traspaso", 180, 68,
        "Local en esquina, con grandes ventanales y cocina equipada. Incluye el negocio y el equipamiento; el inmueble continúa en alquiler.", R.drawable.local_barcelona),
    Local("distrito", "Distrito Burger", "Ruzafa · Valencia", 395000, "Venta", 210, 82,
        "Comedor amplio, cocina a la vista y acceso adaptado. El precio incluye el inmueble y el negocio equipado.", R.drawable.local_valencia),
    Local("norte", "Norte & Fuego", "Casco Viejo · Bilbao", 189000, "Traspaso", 130, 46,
        "Un espacio acogedor con barra, cocina y almacén. Incluye el negocio y el equipamiento; el inmueble continúa en alquiler.", R.drawable.local_bilbao),
    Local("patio", "Patio de Barrio", "Alameda · Sevilla", 129000, "Traspaso", 120, 42,
        "Hamburguesería con patio interior e instalaciones recientes. Incluye el negocio y el equipamiento; el inmueble continúa en alquiler.", R.drawable.local_sevilla),
    Local("costa", "Costa Brasa", "Centro · Málaga", 520000, "Venta", 240, 96,
        "Restaurante con terraza y una cocina preparada para grandes servicios. El precio incluye el inmueble y el negocio equipado.", R.drawable.local_malaga)
)

fun precioEnEuros(precio: Int): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-ES"))
    .apply { maximumFractionDigits = 0 }.format(precio)
