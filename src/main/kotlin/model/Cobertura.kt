package org.example.model

enum class Cobertura {
    TERCEROS,
    TERCEROS_AMPLIADO,
    FRANQUICIA_200,
    FRANQUICIA_300,
    FRANQUICIA_400,
    FRANQUICIA_500,
    TODO_RIESGO;

    companion object {
        fun getCobertura(valor: String) : Cobertura = entries.find { it.name.equals(valor, ignoreCase = true) } ?: TERCEROS
    }
}