package org.example.data

import org.example.model.Usuario
import org.example.utils.IUtilFicheros

class RepoUsuariosFich(
    private val rutaArchivo: String,
    private val fich: IUtilFicheros
) : RepoUsuariosMem(), ICargarUsuariosIniciales {

    override fun agregar(usuario: Usuario): Boolean {
        if (buscar(usuario.nombre) != null) return false
        return if (fich.agregarLinea(rutaArchivo, usuario.serializar())) {
            super.agregar(usuario)
        } else false
    }

    override fun eliminar(usuario: Usuario): Boolean {
        val nuevos = usuarios.filter { it != usuario }
        return if (fich.escribirArchivo(rutaArchivo, nuevos)) {
            super.eliminar(usuario)
        } else false
    }

    override fun cargarUsuarios(): Boolean {
        try {
            val lineas = fich.leerArchivo(rutaArchivo)
            if (lineas.isEmpty()) return false

            for (linea in lineas) {
                val datos = linea.split(";")
                val usuario = Usuario.crearUsuario(datos)
                if (usuario != null) {
                    super.agregar(usuario)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}