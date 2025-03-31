package org.example.model

abstract class Seguro (
    val numPoliza: Int,
    private val dniTitular: String,
    protected val importe: Double
) : IExportable {

    protected fun getDniTitular() : String {
        return dniTitular
    }

    abstract fun calcularImporteAnioSiguiente(interes: Double): Double

    fun tipoSeguro(): String {
        return this::class.simpleName ?: "Desconocido"
    }

    override fun serializar(separador: String): String {
        return "$numPoliza$separador$dniTitular$separador${"%.2f".format(importe)}"
    }

    override fun toString(): String {
        return "Seguro(numPoliza=${numPoliza}, dniTitular=${dniTitular}, importe=${"%.2f".format(importe)})"
    }

    override fun hashCode(): Int {
        return numPoliza.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Seguro) return false
        return this.numPoliza == other.numPoliza
    }

}