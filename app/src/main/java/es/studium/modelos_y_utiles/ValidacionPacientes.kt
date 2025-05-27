package es.studium.modelos_y_utiles

import es.studium.opcion_pacientes.AltaPacienteActivity

class ValidacionPacientes {

    val altaPacienteActivity = AltaPacienteActivity()

    fun esNombreValido(nombre: String): Boolean {
        return nombre.isNotEmpty()
    }

    fun esApellidosValido(apellidos: String): Boolean {
        return apellidos.isNotEmpty()
    }

    fun esSexoValido(sexo: String, itemCeroSpinner: String): Boolean {
        return sexo != itemCeroSpinner
    }

    fun esNuhsaValido(nuhsa: String): Boolean {
        return nuhsa.isNotEmpty()
    }

    fun esNuhsaUnico(nuhsa: String): Boolean {
        return !altaPacienteActivity.existePaciente(nuhsa)
    }

    fun esTelefonoValido(telefono: String): Boolean {
        return telefono.isNotEmpty()
    }

    fun esNuhsaValidoParaModificacion(nuhsaModificado: String, nuhsaOriginal: String): Boolean {
        return (nuhsaModificado == nuhsaOriginal) || !altaPacienteActivity.existePaciente(nuhsaModificado)
    }

    fun esDireccionValida(direccion: String): Boolean {
        return direccion.isNotEmpty()
    }

    fun esLocalidadValida(localidad: String): Boolean {
        return localidad.isNotEmpty()
    }

    fun esProvinciaValida(provincia: String): Boolean {
        return provincia.isNotEmpty()
    }

    fun esCodigoPostalValido(codigoPostal: String): Boolean {
        return codigoPostal.isNotEmpty()
    }

    fun esCodigoPostalLongitudCorrecta(codigoPostal: String): Boolean {
        return codigoPostal.length == 5
    }

}