package org.example.service

import org.example.data.IRepoUsuarios
import org.example.model.Perfil
import org.example.model.Usuario
import org.example.utils.IUtilSeguridad

class GestorUsuarios(
    private val repo: IRepoUsuarios,
    private val seguridad: IUtilSeguridad
) : IServUsuarios {

    override fun iniciarSesion(nombre: String, clave: String): Perfil? {
        val usuario = repo.buscar(nombre)
        return if (usuario != null && seguridad.verificarClave(clave, usuario.getUsuarioClave())) {
            usuario.perfil
        } else {
            null
        }
    }

    override fun agregarUsuario(nombre: String, clave: String, perfil: Perfil): Boolean {
        val claveEncriptada = seguridad.encriptarClave(clave)
        val usuario = Usuario(nombre, claveEncriptada, perfil)
        return repo.agregar(usuario)
    }

    override fun eliminarUsuario(nombre: String): Boolean {
        return repo.eliminar(nombre)
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        val nuevaClaveEncriptada = seguridad.encriptarClave(nuevaClave)
        return repo.cambiarClave(usuario, nuevaClaveEncriptada)
    }

    override fun buscarUsuario(nombre: String): Usuario? {
        return repo.buscar(nombre)
    }

    override fun consultarTodos(): List<Usuario> {
        return repo.obtenerTodos()
    }

    override fun consultarPorPerfil(perfil: Perfil): List<Usuario> {
        return repo.obtener(perfil)
    }
}