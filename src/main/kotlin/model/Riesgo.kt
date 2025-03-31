package org.example.model

enum class Riesgo {
    BAJO,
    MEDIO,
    ALTO;

    companion object {
        fun getRiesgo(valor: String) : Riesgo = entries.find { it.name.equals(valor, ignoreCase = true) } ?: MEDIO
    }
}