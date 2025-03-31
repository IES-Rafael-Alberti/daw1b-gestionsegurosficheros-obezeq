package org.example.model

enum class Auto {
    COCHE,
    MOTO,
    CAMION;

    companion object {
        fun getAuto(valor: String): Auto {
            for (auto in entries) {
                if (auto.name.equals(valor, ignoreCase = true)) {
                    return auto
                }
            }
            return COCHE
        }
    }
}