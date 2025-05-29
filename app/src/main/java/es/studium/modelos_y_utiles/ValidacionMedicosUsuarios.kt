package es.studium.modelos_y_utiles

import es.studium.login.AltaDatosDeAccesoActivity
import es.studium.opcion_perfil.ModificarMedicoActivity

class ValidacionMedicosUsuarios {


    fun esNombreUsuarioValido(nombre: String): Boolean {
        return nombre.isNotEmpty()
    }

    fun esNumColegiadoValido(num: String): Boolean {
        return num.isNotEmpty()
    }

    fun esClave1Valida(clave1: String): Boolean {
        return clave1.isNotEmpty()
    }

    fun esClave1LongitudCorrecta(clave1: String): Boolean {
        return clave1.length >= 6
    }

    fun esClave2Valida(clave2: String): Boolean {
        return clave2.isNotEmpty()
    }

    fun clavesCoinciden(clave1: String, clave2: String): Boolean {
        return clave1 == clave2
    }

    fun esNombreMedicoValido(nombre: String): Boolean {
        return nombre.isNotEmpty()
    }

    fun esApellidosMedicoValidos(apellidos: String): Boolean {
        return apellidos.isNotEmpty()
    }

    fun esEspecialidadSeleccionada(posicionSpinner: Int): Boolean {
        return posicionSpinner != 0
    }

    fun esCentroMedicoSeleccionado(posicionSpinner: Int): Boolean {
        return posicionSpinner != 0
    }

    fun esTelefonoValido(telefono: String): Boolean {
        return telefono.isNotEmpty()
    }

    fun esEmailValido(email: String): Boolean {
        return email.isNotEmpty()
    }


}