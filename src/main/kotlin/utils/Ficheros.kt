package org.example.utils

import org.example.model.IExportable
import org.example.ui.IEntradaSalida
import java.io.File
import java.io.IOException

class Ficheros(private val ui: IEntradaSalida) : IUtilFicheros {

    override fun leerArchivo(ruta: String): List<String> {
        return try {
            val archivo = File(ruta)
            if (archivo.exists()) archivo.readLines() else emptyList()
        } catch (e: IOException) {
            ui.mostrarError("Error leyendo archivo: ${e.message}")
            emptyList()
        }
    }

    override fun agregarLinea(ruta: String, linea: String): Boolean {
        return try {
            File(ruta).appendText("$linea\n")
            true
        } catch (e: Exception) {
            ui.mostrarError("Error escribiendo en archivo: ${e.message}")
            false
        }
    }

    override fun <T : IExportable> escribirArchivo(ruta: String, elementos: List<T>): Boolean {
        return try {
            val contenido = elementos.joinToString("\n") { it.serializar() }
            File(ruta).writeText(contenido)
            true
        } catch (e: Exception) {
            ui.mostrarError("Error sobrescribiendo archivo: ${e.message}")
            false
        }
    }

    override fun existeFichero(ruta: String): Boolean = File(ruta).exists()

    override fun existeDirectorio(ruta: String): Boolean = File(ruta).isDirectory()
}