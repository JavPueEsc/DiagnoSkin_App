package es.studium.operacionesbd_pacientes

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class ModificacionRemotaPacientes {
    private  var client : OkHttpClient = OkHttpClient()

    suspend fun modificarPaciente(
        id : String,
        nombre: String,
        apellidos: String,
        sexo: String,
        fechaNac: String,
        nuhsa: String,
        telefono: String,
        email: String,
        dni: String,
        direccion: String,
        localidad: String,
        provincia: String,
        codigoPostal: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                // Construir el cuerpo de la solicitud con los parámetros
                val formBody = FormBody.Builder()
                    .add("idPaciente", id)
                    .add("nombrePaciente", nombre)
                    .add("apellidosPaciente", apellidos)
                    .add("sexoPaciente", sexo)
                    .add("fechaNacPaciente", fechaNac)
                    .add("nuhsaPaciente", nuhsa)
                    .add("telefonoPaciente", telefono)
                    .add("emailPaciente", email)
                    .add("dniPaciente", dni)
                    .add("direccionPaciente", direccion)
                    .add("localidadPaciente", localidad)
                    .add("provinciaPaciente", provincia)
                    .add("codigoPostalPaciente", codigoPostal)
                    .build()

                // Crear la solicitud PUT
                val request = Request.Builder()
                    .url("http://192.168.0.216/ApiRestDiagnoSkin/pacientes.php")
                    .put(formBody)
                    .build()

                // Realizar la petición y obtener la respuesta
                val response = client.newCall(request).execute()
                Log.d("ModificacionRemotaPacientes", response.body?.string() ?: "Sin cuerpo")

                // Verificar si la respuesta fue exitosa
                response.isSuccessful
            } catch (e: IOException) {
                Log.e("ModificacionRemotaPacientes", "Error al modificar paciente: ${e.message}")
                false
            }
        }
    }
}