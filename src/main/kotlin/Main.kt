package org.example


import org.example.app.CargadorInicial
import org.example.app.ControlAcceso
import org.example.app.GestorMenu
import org.example.data.*
import org.example.model.Perfil
import org.example.service.GestorSeguros
import org.example.service.GestorUsuarios
import org.example.ui.Consola
import org.example.utils.Ficheros
import org.example.utils.Seguridad
import java.time.LocalDate

fun main() {
    val ui = Consola()
    val ficheros = Ficheros(ui)
    val seguridad = Seguridad()

    try {
        ui.limpiarPantalla()
        ui.mostrar("────── SISTEMA DE GESTIÓN DE SEGUROS ──────")

        // 1. Preguntar modo de ejecución
        val modoSimulacion = ui.preguntar("[+] ¿Iniciar en modo SIMULACIÓN (sin persistencia)?")

        // 2. Configurar repositorios
        val repoUsuarios = if (modoSimulacion) {
            RepoUsuariosMem()
        } else {
            RepoUsuariosFich("Usuarios.txt", ficheros)
        }

        val repoSeguros = if (modoSimulacion) {
            RepoSegurosMem()
        } else {
            RepoSegurosFich("Seguros.txt", ficheros)
        }

        if (!modoSimulacion) {
            val cargador = CargadorInicial(
                repoUsuarios as ICargarUsuariosIniciales,
                repoSeguros as ICargarSegurosIniciales,
                ui
            )

            cargador.cargarUsuarios()
            cargador.cargarSeguros()
        }

        val gestorUsuarios = GestorUsuarios(repoUsuarios, seguridad)
        val gestorSeguros = GestorSeguros(repoSeguros)

        val controlAcceso = ControlAcceso(
            "Usuarios.txt",
            gestorUsuarios,
            ui,
            ficheros
        )

        val credenciales = controlAcceso.autenticar() ?: run {
            ui.mostrar("[:] Saliendo del programa...")
            return
        }

        val gestorMenu = GestorMenu(
            credenciales.first,
            credenciales.second,
            ui,
            gestorUsuarios,
            gestorSeguros
        )

        gestorMenu.iniciarMenu()

    } catch (e: Exception) {
        ui.mostrarError("[-] Error crítico: ${e.message}")
        e.printStackTrace()
    } finally {
        ui.mostrar("\n[+] Gracias por usar el sistema. ¡Hasta pronto!")
    }
}