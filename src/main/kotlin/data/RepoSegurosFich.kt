package org.example.data

import org.example.model.Seguro
import org.example.model.SeguroAuto
import org.example.model.SeguroHogar
import org.example.model.SeguroVida
import org.example.utils.IUtilFicheros
import kotlin.reflect.KFunction1

class RepoSegurosFich(
    private val rutaArchivo: String,
    private val fich: IUtilFicheros
) : RepoSegurosMem(), ICargarSegurosIniciales {

    override fun agregar(seguro: Seguro): Boolean {
        return if (fich.agregarLinea(rutaArchivo, seguro.serializar())) {
            super.agregar(seguro)
        } else false
    }

    override fun eliminar(seguro: Seguro): Boolean {
        val nuevos = seguros.filter { it != seguro }
        return if (fich.escribirArchivo(rutaArchivo, nuevos)) {
            super.eliminar(seguro)
        } else false
    }

    override fun cargarSeguros(mapa: Map<String, KFunction1<List<String>, Seguro?>>): Boolean {
        try {
            val lineas = fich.leerArchivo(rutaArchivo)
            if (lineas.isEmpty()) return false

            for (linea in lineas) {
                val partes = linea.split(";")
                if (partes.size < 2) continue

                val tipo = partes.last()
                val datos = partes.dropLast(1)

                val constructor = mapa[tipo] ?: continue
                val seguro = constructor(datos)

                if (seguro != null) {
                    super.agregar(seguro)
                }
            }
            actualizarContadores(seguros)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun actualizarContadores(seguros: List<Seguro>) {
        var maxHogar = 100000
        var maxAuto = 400000
        var maxVida = 800000

        for (seguro in seguros) {
            when (seguro.tipoSeguro()) {
                "SeguroHogar" -> if (seguro.numPoliza > maxHogar) maxHogar = seguro.numPoliza
                "SeguroAuto" -> if (seguro.numPoliza > maxAuto) maxAuto = seguro.numPoliza
                "SeguroVida" -> if (seguro.numPoliza > maxVida) maxVida = seguro.numPoliza
            }
        }

        SeguroHogar.numPolizasHogar = maxHogar
        SeguroAuto.numPolizasAuto = maxAuto
        SeguroVida.numPolizasVida = maxVida
    }
}