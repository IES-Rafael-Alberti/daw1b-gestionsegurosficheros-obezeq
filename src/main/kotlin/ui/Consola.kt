package org.example.ui

import jdk.internal.org.jline.reader.EndOfFileException

import jdk.internal.org.jline.reader.UserInterruptException
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import java.util.*

class Consola : IEntradaSalida {

    override fun mostrar(msj: String, salto: Boolean, pausa: Boolean) {
        if (salto) println(msj) else print(msj)
        if (pausa) pausar()
    }

    override fun mostrarError(msj: String, pausa: Boolean) {
        val mensaje = if (msj.startsWith("ERROR - ")) msj else "ERROR - $msj"
        mostrar(mensaje, pausa = pausa)
    }

    override fun pedirInfo(msj: String): String {
        if (msj.isNotEmpty()) print("$msj ")
        return readLine()?.trim() ?: ""
    }

    override fun pedirInfo(msj: String, error: String, debeCumplir: (String) -> Boolean): String {
        while (true) {
            val input = pedirInfo(msj)
            if (debeCumplir(input)) return input
            mostrarError(error)
        }
    }

    override fun pedirDouble(
        prompt: String,
        error: String,
        errorConv: String,
        debeCumplir: (Double) -> Boolean
    ): Double {
        while (true) {
            val input = pedirInfo(prompt).replace(',', '.')
            val valor = input.toDoubleOrNull()

            when {
                valor == null -> mostrarError(errorConv)
                !debeCumplir(valor) -> mostrarError(error)
                else -> return valor
            }
        }
    }

    override fun pedirEntero(
        prompt: String,
        error: String,
        errorConv: String,
        debeCumplir: (Int) -> Boolean
    ): Int {
        while (true) {
            val input = pedirInfo(prompt)
            val valor = input.toIntOrNull()

            when {
                valor == null -> mostrarError(errorConv)
                !debeCumplir(valor) -> mostrarError(error)
                else -> return valor
            }
        }
    }

    override fun pedirInfoOculta(prompt: String): String {
        return try {
            val terminal = TerminalBuilder.builder()
                .dumb(true) // Para entornos no interactivos como IDEs
                .build()

            val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build()

            reader.readLine(prompt, '*') // Oculta la contraseña con '*'
        } catch (e: UserInterruptException) {
            mostrarError("Entrada cancelada por el usuario (Ctrl + C).", pausa = false)
            ""
        } catch (e: EndOfFileException) {
            mostrarError("Se alcanzó el final del archivo (EOF ó Ctrl+D).", pausa = false)
            ""
        } catch (e: Exception) {
            mostrarError("Problema al leer la contraseña: ${e.message}", pausa = false)
            ""
        }
    }

    override fun pausar(msj: String) {
        print(msj)
        readLine()
    }

    override fun limpiarPantalla(numSaltos: Int) {
        if (System.console() != null) {
            mostrar("\u001b[H\u001b[2J", false)
            System.out.flush()
        } else {
            repeat(numSaltos) {
                mostrar("")
            }
        }
    }

    override fun preguntar(mensaje: String): Boolean {
        while (true) {
            val respuesta = pedirInfo("$mensaje (s/n)").lowercase(Locale.getDefault())
            when (respuesta) {
                "s" -> return true
                "n" -> return false
                else -> mostrarError("Debe responder 's' o 'n'")
            }
        }
    }
}