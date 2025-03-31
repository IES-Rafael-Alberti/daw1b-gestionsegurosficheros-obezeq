package org.example.model

enum class Perfil {
    ADMIN,
    GESTION,
    CONSULTA;

    companion object {
        fun getPerfil(valor: String) : Perfil = entries.find { it.name.equals(valor, ignoreCase = true) } ?: CONSULTA
    }
}