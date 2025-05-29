package es.studium.modelos_y_utiles

interface InterfazCredenciales {
    fun comprobarCredenciales(usuario: String, clave: String): Boolean
    fun comprobarBloqueo(usuario: String): Boolean
}