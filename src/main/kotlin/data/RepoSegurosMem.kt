package org.example.data

import org.example.model.Seguro

open class RepoSegurosMem : IRepoSeguros {
    val seguros = mutableListOf<Seguro>()

    override fun agregar(seguro: Seguro): Boolean = seguros.add(seguro)

    override fun buscar(numPoliza: Int): Seguro? {

        for (seguro in seguros) {
            if (seguro.numPoliza == numPoliza) {
                return seguro
            }
        }

        return null
    }

    override fun eliminar(seguro: Seguro): Boolean = seguros.remove(seguro)

    override fun eliminar(numPoliza: Int): Boolean {

        val seguroEncontrado = buscar(numPoliza)

        if (seguroEncontrado != null) {
            return eliminar(seguroEncontrado)
        }

        return false
    }

    override fun obtenerTodos(): List<Seguro> = seguros.toList()

    override fun obtener(tipoSeguro: String): List<Seguro> {
        return seguros.filter { it.tipoSeguro() == tipoSeguro }
    }
}