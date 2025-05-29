package es.studium.modelos_y_utiles

import es.studium.login.AutenticacionActivity

class ValidacionesOtrasBBDD (private val interfazCredenciales : InterfazCredenciales){

    fun sonCredencialesCorrectas(usuario: String, clave: String): Boolean {
        return interfazCredenciales.comprobarCredenciales(usuario, clave)
    }

    fun estaBloqueado(usuario: String): Boolean {
        return interfazCredenciales.comprobarBloqueo(usuario)
    }
}