package es.studium.modelos_y_utiles

class ValidacionPacientesBBDD (private val interfazPaciente : InterfazPaciente){

    fun esNuhsaUnico(nuhsa: String): Boolean {
        return !interfazPaciente.existePaciente(nuhsa)
    }

    fun esNuhsaValidoParaModificacion(nuhsaModificado: String, nuhsaOriginal: String): Boolean {
        return (nuhsaModificado == nuhsaOriginal) || !interfazPaciente.existePaciente(nuhsaModificado)
    }
}