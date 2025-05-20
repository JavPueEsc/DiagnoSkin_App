package es.studium.operacionesbd_pacientes

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class AltaRemotaPacientes {
    val client = OkHttpClient()

    suspend fun altaPaciente(
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
                val formBody = FormBody.Builder()
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

                val request = Request.Builder()
                    .url("http://192.168.0.216/ApiRestDiagnoSkin/pacientes.php")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                Log.d("RESPUESTA AltaRemotaPacientes", response.body?.string() ?: "Sin cuerpo")
                response.isSuccessful
            } catch (e: IOException) {
                Log.e("ErrorInsercion", "Error al insertar diagnóstico: ${e.message}")
                false
            }
        }
    }
}