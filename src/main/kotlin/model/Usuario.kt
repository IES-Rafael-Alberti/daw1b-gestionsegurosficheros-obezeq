package org.example.model

class Usuario (
    val nombre: String,
    private var clave: String,
    val perfil: Perfil
) : IExportable {

    companion object {
        fun crearUsuario(datos: List<String>) : Usuario? {
            return try {
                Usuario(
                    datos[0],
                    datos[1],
                    Perfil.getPerfil(datos[2])
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun cambiarClave(nuevaClaveEncriptada: String) {

        // Actualizo la clave del usuario directamente
        // Pero en el servicio que gestiona los usuarios debe solicitar la antigua clave, verificarla y pedir la nueva

        this.clave = nuevaClaveEncriptada
    }

    override fun serializar(separador: String): String {
        return "$nombre$separador$clave$separador$perfil$separador"
    }


}