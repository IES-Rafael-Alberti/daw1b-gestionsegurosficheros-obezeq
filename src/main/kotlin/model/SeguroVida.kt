package org.example.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeguroVida private constructor(
    numPoliza: Int,
    dniTitular: String,
    importe: Double,
    private val fechaNac: LocalDate,
    private val nivelRiesgo: Riesgo,
    private val indemnizacion: Double
) : Seguro(numPoliza, dniTitular, importe) {

    companion object {
        var numPolizasVida = 800000

        fun crearSeguro(datos: List<String>): SeguroVida? {
            return try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                SeguroVida(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    LocalDate.parse(datos[3], formatter),
                    Riesgo.getRiesgo(datos[4]),
                    datos[5].toDouble()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        fechaNacimiento: LocalDate,
        nivelRiesgo: Riesgo,
        indemnizacion: Double
    ) : this(
        numPolizasVida++,
        dniTitular,
        importe,
        fechaNacimiento,
        nivelRiesgo,
        indemnizacion
    )

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val edad = LocalDate.now().year - fechaNac.year
        return importe * (1 + interes + (edad * 0.0005) + (nivelRiesgo.interesAplicado / 100))
    }

    override fun serializar(separador: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return "$numPoliza$separador${getDniTitular()}$separador$importe$separador" +
                "${fechaNac.format(formatter)}$separador${nivelRiesgo.name}$separador" +
                "$indemnizacion$separador${tipoSeguro()}"
    }

    override fun toString(): String {
        return "SeguroVida(numPoliza=$numPoliza, dniTitular=${getDniTitular()}, " +
                "importe=${"%.2f".format(importe)}, fechaNac=$fechaNac, " +
                "riesgo=${nivelRiesgo.name}, indemnizacion=$indemnizacion)"
    }
}