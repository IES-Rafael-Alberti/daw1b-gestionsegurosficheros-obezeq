package org.example.data

import org.example.model.Perfil
import org.example.model.Usuario

open class RepoUsuariosMem : IRepoUsuarios {
    protected val usuarios = mutableListOf<Usuario>()

    override fun agregar(usuario: Usuario): Boolean {
        if (buscar(usuario.nombre) != null) return false
        return usuarios.add(usuario)
    }

    override fun buscar(nombreUsuario: String): Usuario? {
        for (usuario in usuarios) {
            if (usuario.nombre == nombreUsuario) {
                return usuario
            }
        }

        return null
    }

    override fun eliminar(usuario: Usuario): Boolean = usuarios.remove(usuario)

    override fun eliminar(nombreUsuario: String): Boolean {
        val usuarioEncontrado = buscar(nombreUsuario)

        if (usuarioEncontrado != null) {
            return eliminar(usuarioEncontrado)
        }

        return false
    }

    override fun obtenerTodos(): List<Usuario> = usuarios.toList()

    override fun obtener(perfil: Perfil): List<Usuario> {
        return usuarios.filter { it.perfil == perfil }
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        usuario.cambiarClave(nuevaClave)
        return true
    }
}