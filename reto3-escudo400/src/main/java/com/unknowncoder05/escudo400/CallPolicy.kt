package com.unknowncoder05.escudo400

enum class Reason(val label: String) {
    COMMERCIAL("Prefijo comercial 400"),
    WHITELIST("Número en la lista blanca"),
    OTHER("Fuera del rango 400 de España"),
    DISABLED("Protección pausada")
}

data class Decision(val blocked: Boolean, val reason: Reason)

object CallPolicy {
    // Se comparan números completos; nunca sufijos ni extensiones.
    fun normalize(raw: String?): String? {
        if (raw == null) return null
        val compact = raw.filterNot { it.isWhitespace() || it in "()-. " }
        if (compact.any { it !in '0'..'9' && it != '+' }) return null
        val international = when {
            compact.startsWith("+") -> compact
            compact.startsWith("00") -> "+" + compact.drop(2)
            compact.length == 9 && compact.all { it in '0'..'9' } -> "+34$compact"
            else -> return null
        }
        val digits = international.drop(1)
        if (digits.length !in 8..15 || digits.firstOrNull() == '0' || digits.any { it !in '0'..'9' }) return null
        if (international.startsWith("+34") && digits.length != 11) return null
        return international
    }

    fun decide(raw: String?, enabled: Boolean, whitelisted: Boolean): Decision = when {
        !enabled -> Decision(false, Reason.DISABLED)
        whitelisted -> Decision(false, Reason.WHITELIST)
        normalize(raw)?.matches(Regex("\\+34400[0-9]{6}")) == true -> Decision(true, Reason.COMMERCIAL)
        else -> Decision(false, Reason.OTHER)
    }
}
