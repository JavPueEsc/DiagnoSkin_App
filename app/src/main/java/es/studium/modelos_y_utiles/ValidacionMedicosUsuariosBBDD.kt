package es.studium.modelos_y_utiles


class ValidacionMedicosUsuariosBBDD (private val interfazUsuario : InterfazUsuario, private val interfazMedico : InterfazMedico){

    fun existeUsuario(nombreUsuario: String): Boolean {
        return interfazUsuario.consultarExistenciaUsuario(nombreUsuario.trim())
    }

    fun existeMedico(numColegiado: String): Boolean {
        return interfazMedico.consultarExistenciaMedico(numColegiado.trim())
    }

    fun esNumColegiadoValidoParaModificacion(numColegiadoModificado: String, numColegiadoOriginal: String
    ): Boolean {
        return (numColegiadoModificado == numColegiadoOriginal) || !interfazMedico.consultarExistenciaMedico(numColegiadoModificado)
    }
}