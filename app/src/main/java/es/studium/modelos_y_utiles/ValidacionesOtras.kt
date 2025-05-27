package es.studium.modelos_y_utiles

import android.net.Uri
import es.studium.login.AutenticacionActivity
import java.util.Date

class ValidacionesOtras {

    val autenticacionActivity = AutenticacionActivity()

    //Login
    fun esUsuarioValido(usuario: String): Boolean {
        return usuario.isNotEmpty()
    }

    fun esClaveValida(clave: String): Boolean {
        return clave.isNotEmpty()
    }

    fun sonCredencialesCorrectas(usuario: String, clave: String): Boolean {
        return autenticacionActivity.comprobarCredenciales(usuario, clave)
    }

    fun estaBloqueado(usuario: String): Boolean {
        return autenticacionActivity.comprobarBloqueo(usuario)
    }

    //Rangos de fechas válidos
    fun esFechaDesdeNoVacia(fechaDesde: String): Boolean {
        return fechaDesde.isNotEmpty()
    }

    fun esFechaHastaNoVacia(fechaHasta: String): Boolean {
        return fechaHasta.isNotEmpty()
    }

    fun esRangoFechasValido(fechaDesde: Date?, fechaHasta: Date?): Boolean {
        return fechaDesde != null && fechaHasta != null && !fechaDesde.after(fechaHasta)
    }

}