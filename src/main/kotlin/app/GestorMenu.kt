package org.example.app

import org.example.model.*
import org.example.service.IServSeguros
import org.example.service.IServUsuarios
import org.example.ui.IEntradaSalida
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GestorMenu(
    val nombreUsuario: String,
    val perfilUsuario: Perfil,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val gestorSeguros: IServSeguros
) {

    /**
     * Inicia un menú según el índice correspondiente al perfil actual.
     *
     * @param indice Índice del menú que se desea mostrar (0 = principal).
     */
    fun iniciarMenu(indice: Int = 0) {
        val (opciones, acciones) = ConfiguracionesApp.obtenerMenuYAcciones(perfilUsuario.toString(), indice)
        ejecutarMenu(opciones, acciones)
    }

    /**
     * Formatea el menú en forma numerada.
     */
    private fun formatearMenu(opciones: List<String>): String {
        var cadena = ""
        opciones.forEachIndexed { index, opcion ->
            cadena += "${index + 1}. $opcion\n"
        }
        return cadena
    }

    /**
     * Muestra el menú limpiando pantalla y mostrando las opciones numeradas.
     */
    private fun mostrarMenu(opciones: List<String>) {
        ui.limpiarPantalla()
        ui.mostrar(formatearMenu(opciones), salto = false)
    }

    /**
     * Ejecuta el menú interactivo.
     *
     * @param opciones Lista de opciones que se mostrarán al usuario.
     * @param ejecutar Mapa de funciones por número de opción.
     */
    private fun ejecutarMenu(opciones: List<String>, ejecutar: Map<Int, (GestorMenu) -> Boolean>) {
        do {
            mostrarMenu(opciones)
            val opcion = ui.pedirInfo("Elige opción > ").toIntOrNull()
            if (opcion != null && opcion in 1..opciones.size) {
                // Buscar en el mapa las acciones a ejecutar en la opción de menú seleccionada
                val accion = ejecutar[opcion]
                // Si la accion ejecutada del menú retorna true, debe salir del menú
                if (accion != null && accion(this)) return
            }
            else {
                ui.mostrarError("Opción no válida!")
            }
        } while (true)
    }

    fun nuevoUsuario() {
        if (perfilUsuario != Perfil.ADMIN) {
            ui.mostrarError("Solo los administradores pueden crear usuarios")
            return
        }

        val nombre = ui.pedirInfo("Nombre del nuevo usuario:", "El nombre no puede estar vacío") { it.isNotBlank() }
        val clave = ui.pedirInfoOculta("Clave para $nombre:")
        val perfil = Perfil.values()[ui.pedirEntero(
            "Perfil (1-ADMIN, 2-GESTION, 3-CONSULTA):",
            "Debe ser entre 1 y 3",
            "Número inválido"
        ) { it in 1..3 } - 1]

        if (gestorUsuarios.agregarUsuario(nombre, clave, perfil)) {
            ui.mostrar("Usuario creado con éxito!")
        } else {
            ui.mostrarError("Error: El usuario ya existe")
        }
    }

    fun eliminarUsuario() {
        if (perfilUsuario != Perfil.ADMIN) {
            ui.mostrarError("Solo los administradores pueden eliminar usuarios")
            return
        }

        val nombre = ui.pedirInfo("Nombre del usuario a eliminar:")
        if (gestorUsuarios.eliminarUsuario(nombre)) {
            ui.mostrar("Usuario eliminado!")
        } else {
            ui.mostrarError("El usuario no existe")
        }
    }

    fun cambiarClaveUsuario() {
        val usuarioActual = gestorUsuarios.buscarUsuario(nombreUsuario) ?: run {
            ui.mostrarError("Usuario no encontrado")
            return
        }

        val claveActual = ui.pedirInfoOculta("Clave actual:")
        val perfil = gestorUsuarios.iniciarSesion(nombreUsuario, claveActual)

        if (perfil == null) {
            ui.mostrarError("Clave actual incorrecta")
            return
        }

        val nuevaClave = ui.pedirInfoOculta("Nueva clave:")
        val confirmacion = ui.pedirInfoOculta("Confirme nueva clave:")

        if (nuevaClave != confirmacion) {
            ui.mostrarError("Las claves no coinciden")
            return
        }

        if (gestorUsuarios.cambiarClave(usuarioActual, nuevaClave)) {
            ui.mostrar("Clave actualizada correctamente!")
        } else {
            ui.mostrarError("Error al actualizar la clave")
        }
    }

    fun consultarUsuarios() {
        val usuarios = when (perfilUsuario) {
            Perfil.ADMIN -> gestorUsuarios.consultarTodos()
            else -> gestorUsuarios.consultarPorPerfil(perfilUsuario)
        }

        ui.limpiarPantalla()
        ui.mostrar("────── LISTADO DE USUARIOS ──────")
        usuarios.forEach {
            ui.mostrar(" - ${it.nombre} (${it.perfil})")
        }
        ui.pausar()
    }

    private fun pedirDni(): String {
        return ui.pedirInfo(
            "DNI del titular (8 dígitos y letra):",
            "Formato DNI incorrecto. Ej: 12345678X"
        ) { it.matches(Regex("[0-9]{8}[A-Za-z]")) }.uppercase()
    }

    private fun pedirImporte(): Double {
        return ui.pedirDouble(
            "Importe anual:",
            "Debe ser mayor que 0",
            "Debe ser un número válido"
        ) { it > 0.0 }
    }

    fun contratarSeguroHogar() {
        val dni = pedirDni()
        val importe = pedirImporte()
        val metros = ui.pedirEntero(
            "Metros cuadrados de la vivienda:",
            "Debe ser mayor que 0",
            "Número inválido"
        ) { it > 0 }

        val valorContenido = ui.pedirDouble(
            "Valor del contenido:",
            "Debe ser positivo",
            "Número inválido"
        ) { it >= 0.0 }

        val direccion = ui.pedirInfo("Dirección completa:")
        val anio = ui.pedirEntero(
            "Año de construcción:",
            "Debe ser > 1900",
            "Año inválido"
        ) { it > 1900 }

        if (gestorSeguros.contratarSeguroHogar(dni, importe, metros, valorContenido, direccion, anio)) {
            ui.mostrar("Seguro de hogar contratado!")
        } else {
            ui.mostrarError("Error al contratar el seguro")
        }
    }

    fun contratarSeguroAuto() {
        val dni = pedirDni()
        val importe = pedirImporte()
        val descripcion = ui.pedirInfo("Descripción del vehículo:")
        val combustible = ui.pedirInfo("Tipo de combustible:")
        val tipoAuto = Auto.values()[ui.pedirEntero(
            "Tipo (1-Coche, 2-Moto, 3-Camion):",
            "Opción 1-3",
            "Número inválido"
        ) { it in 1..3 } - 1]

        val cobertura = Cobertura.values()[ui.pedirEntero(
            "Cobertura (1-TERCEROS, 2-TERCEROS_AMPLIADO...):",
            "Opción válida",
            "Número inválido"
        ) { it in 1..Cobertura.values().size } - 1]

        val asistencia = ui.preguntar("¿Incluir asistencia en carretera? (s/n)")
        val partes = ui.pedirEntero(
            "Número de partes últimos 2 años:",
            "Debe ser >= 0",
            "Número inválido"
        ) { it >= 0 }

        if (gestorSeguros.contratarSeguroAuto(dni, importe, descripcion, combustible, tipoAuto, cobertura, asistencia, partes)) {
            ui.mostrar("Seguro de auto contratado!")
        } else {
            ui.mostrarError("Error al contratar el seguro")
        }
    }

    fun contratarSeguroVida() {
        val dni = pedirDni()
        val importe = pedirImporte()
        val fechaNac = LocalDate.parse(ui.pedirInfo(
            "Fecha nacimiento (dd/MM/yyyy):",
            "Formato inválido. Ej: 15/05/1985"
        ) { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) }, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val riesgo = Riesgo.values()[ui.pedirEntero(
            "Nivel riesgo (1-BAJO, 2-MEDIO, 3-ALTO):",
            "Opción 1-3",
            "Número inválido"
        ) { it in 1..3 } - 1]

        val indemnizacion = ui.pedirDouble(
            "Indemnización:",
            "Debe ser positivo",
            "Número inválido"
        ) { it > 0.0 }

        if (gestorSeguros.contratarSeguroVida(dni, importe, fechaNac, riesgo, indemnizacion)) {
            ui.mostrar("Seguro de vida contratado!")
        } else {
            ui.mostrarError("Error al contratar el seguro")
        }
    }

    fun eliminarSeguro() {
        if (perfilUsuario == Perfil.CONSULTA) {
            ui.mostrarError("No tienes permisos para esta acción")
            return
        }

        val poliza = ui.pedirEntero(
            "Número de póliza a eliminar:",
            "Debe ser positivo",
            "Número inválido"
        ) { it > 0 }

        if (gestorSeguros.eliminarSeguro(poliza)) {
            ui.mostrar("Seguro eliminado!")
        } else {
            ui.mostrarError("No existe seguro con esa póliza")
        }
    }

    fun consultarSeguros() {
        mostrarSeguros(gestorSeguros.consultarTodos())
    }

    fun consultarSegurosHogar() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroHogar"))
    }

    fun consultarSegurosAuto() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroAuto"))
    }

    fun consultarSegurosVida() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroVida"))
    }

    private fun mostrarSeguros(seguros: List<Seguro>) {
        ui.limpiarPantalla()
        if (seguros.isEmpty()) {
            ui.mostrar("No hay seguros registrados")
        } else {
            ui.mostrar("────── LISTADO DE SEGUROS ──────")
            seguros.forEach { ui.mostrar(it.toString()) }
        }
        ui.pausar()
    }
}