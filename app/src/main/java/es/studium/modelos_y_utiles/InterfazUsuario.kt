package es.studium.modelos_y_utiles

interface InterfazUsuario {
    fun consultarExistenciaUsuario(nombreUsuario: String): Boolean
    fun consultarExistenciaMedico(numColegiado: String): Boolean
}
