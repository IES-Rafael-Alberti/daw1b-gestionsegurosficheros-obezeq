package org.example.service

import org.example.data.IRepoSeguros
import org.example.model.*
import java.time.LocalDate

class GestorSeguros(
    private val repo: IRepoSeguros
) : IServSeguros {

    override fun contratarSeguroHogar(
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Int,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: Int
    ): Boolean {
        val seguro = SeguroHogar(
            dniTitular = dniTitular,
            importe = importe,
            metrosCuadrados = metrosCuadrados,
            valorContenido = valorContenido,
            direccion = direccion,
            anioConstruccion = anioConstruccion
        )
        return repo.agregar(seguro)
    }

    override fun contratarSeguroAuto(
        dniTitular: String,
        importe: Double,
        descripcion: String,
        combustible: String,
        tipoAuto: Auto,
        cobertura: Cobertura,
        asistenciaCarretera: Boolean,
        numPartes: Int
    ): Boolean {
        val seguro = SeguroAuto(
            dniTitular = dniTitular,
            importe = importe,
            descripcion = descripcion,
            combustible = combustible,
            tipoAuto = tipoAuto,
            cobertura = cobertura,
            asistenciaCarretera = asistenciaCarretera,
            numPartes = numPartes
        )
        return repo.agregar(seguro)
    }

    override fun contratarSeguroVida(
        dniTitular: String,
        importe: Double,
        fechaNacimiento: LocalDate,
        nivelRiesgo: Riesgo,
        indemnizacion: Double
    ): Boolean {
        val seguro = SeguroVida(
            dniTitular = dniTitular,
            importe = importe,
            fechaNacimiento = fechaNacimiento,
            nivelRiesgo = nivelRiesgo,
            indemnizacion = indemnizacion
        )
        return repo.agregar(seguro)
    }

    override fun eliminarSeguro(numPoliza: Int): Boolean {
        return repo.eliminar(numPoliza)
    }

    override fun consultarTodos(): List<Seguro> {
        return repo.obtenerTodos()
    }

    override fun consultarPorTipo(tipoSeguro: String): List<Seguro> {
        return repo.obtener(tipoSeguro)
    }
}