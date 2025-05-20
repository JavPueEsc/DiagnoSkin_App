package es.studium.operacionesbd_medicos

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class ModificacionRemotaMedicos {
    private  var client : OkHttpClient = OkHttpClient()

    suspend fun modificarMedico(
        id : String,
        nombre: String,
        apellidos: String,
        telefono: String,
        email: String,
        especialidad: String,
        numColegiado: String,
        esAdminMedico: String,
        idCentroMedicoFK: String,
        idUsuarioFK: String,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                // Construir el cuerpo de la solicitud con los parámetros
                val formBody = FormBody.Builder()
                    .add("idMedico", id)
                    .add("nombreMedico", nombre)
                    .add("apellidosMedico", apellidos)
                    .add("telefonoMedico", telefono)
                    .add("emailMedico", email)
                    .add("especialidadMedico", especialidad)
                    .add("numColegiadoMedico", numColegiado)
                    .add("esAdminMedico", esAdminMedico)
                    .add("idCentroMedicoFK", idCentroMedicoFK)
                    .add("idUsuarioFK", idUsuarioFK)
                    .build()

                // Crear la solicitud PUT
                val request = Request.Builder()
                    .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php")
                    .put(formBody)
                    .build()

                // Realizar la petición y obtener la respuesta
                val response = client.newCall(request).execute()
                Log.d("ModificacionRemotaMedicos", response.body?.string() ?: "Sin cuerpo")

                // Verificar si la respuesta fue exitosa
                response.isSuccessful
            } catch (e: IOException) {
                Log.e("ModificacionRemotaMedicos", "Error al modificar paciente: ${e.message}")
                false
            }
        }
    }
}