package org.example.model

class SeguroAuto private constructor(
    numPoliza: Int,
    dniTitular: String,
    importe: Double,
    private val descripcion: String,
    private val combustible: String,
    private val tipoAuto: Auto,
    private val cobertura: Cobertura,
    private val asistenciaCarretera: Boolean,
    private val numPartes: Int
) : Seguro(numPoliza, dniTitular, importe) {

    companion object {
        private var numPolizasAuto = 400000
        private const val PORCENTAJE_INCREMENTO_PARTES = 2

        fun crearSeguro(datos: List<String>): SeguroAuto? {
            return try {
                SeguroAuto(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    datos[3],
                    datos[4],
                    Auto.getAuto(datos[5]),
                    Cobertura.getCobertura(datos[6]),
                    datos[7].toBoolean(),
                    datos[8].toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        descripcion: String,
        combustible: String,
        tipoAuto: Auto,
        cobertura: Cobertura,
        asistenciaCarretera: Boolean,
        numPartes: Int
    ) : this(
        numPolizasAuto++,
        dniTitular,
        importe,
        descripcion,
        combustible,
        tipoAuto,
        cobertura,
        asistenciaCarretera,
        numPartes
    )

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        return importe * (1 + interes + (numPartes * (PORCENTAJE_INCREMENTO_PARTES / 100.0)))
    }

    override fun serializar(separador: String): String {
        return "$numPoliza$separador${getDniTitular()}$separador$importe$separador" +
                "$descripcion$separador$combustible$separador${tipoAuto.name}$separador" +
                "${cobertura.name}$separador$asistenciaCarretera$separador" +
                "$numPartes$separador${tipoSeguro()}"
    }

    override fun toString(): String {
        return "SeguroAuto(numPoliza=$numPoliza, dniTitular=${getDniTitular()}, " +
                "importe=${"%.2f".format(importe)}, descripcion='$descripcion', " +
                "combustible='$combustible', tipoAuto=${tipoAuto.name}, " +
                "cobertura=${cobertura}, asistencia=$asistenciaCarretera, " +
                "partes=$numPartes)"
    }
}