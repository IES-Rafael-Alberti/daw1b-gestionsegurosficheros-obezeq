package org.example.data

import org.example.model.Seguro
import kotlin.reflect.KFunction1

interface ICargarSegurosIniciales {
    fun cargarSeguros(mapa: Map<String, KFunction1<List<String>, Seguro?>>): Boolean
}