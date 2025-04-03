package org.example.model

import java.time.LocalDate

class SeguroHogar : Seguro {

    private val metrosCuadrados: Double
    private val valorContenido: Double
    private val direccion: String
    private val anioConstruccion: Int

    companion object {
        var numPolizasHogar = 10000
        private const val PORCENTAJE_INCREMENTO_ANIOS = 0.02
        private const val CICLO_ANIOS_INCREMENTO = 5

        fun crearSeguro(datos: List<String>): SeguroHogar? {
            return try {
                SeguroHogar(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    datos[3].toDouble(),
                    datos[4].toDouble(),
                    datos[5],
                    datos[6].toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Int,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: Int
    ) : super(numPolizasHogar++, dniTitular, importe) {
        this.metrosCuadrados = metrosCuadrados
        this.valorContenido = valorContenido
        this.direccion = direccion
        this.anioConstruccion = anioConstruccion
    }

    private constructor(
        numPoliza: Int,
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Double,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: Int
    ) : super(numPoliza, dniTitular, importe) {
        this.metrosCuadrados = metrosCuadrados
        this.valorContenido = valorContenido
        this.direccion = direccion
        this.anioConstruccion = anioConstruccion
    }

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val aniosAntiguedad: Int = LocalDate.now().year - anioConstruccion
        val ciclos: Double = aniosAntiguedad.toDouble() / CICLO_ANIOS_INCREMENTO.toDouble()
        return importe * (1 + interes + (ciclos * PORCENTAJE_INCREMENTO_ANIOS))
    }

    override fun serializar(separador: String): String {
        return super.serializar(separador) +
                "$separador$metrosCuadrados$separador$valorContenido$separador$direccion$separador$anioConstruccion"
    }

    override fun toString(): String {
        return "SeguroHogar(numPoliza=$numPoliza, dniTitular=${getDniTitular()}, importe=${"%.2f".format(importe)}, " +
                "metrosCuadrados=$metrosCuadrados, valorContenido=$valorContenido, direccion=$direccion, " +
                "anioConstruccion=$anioConstruccion)"
    }
}