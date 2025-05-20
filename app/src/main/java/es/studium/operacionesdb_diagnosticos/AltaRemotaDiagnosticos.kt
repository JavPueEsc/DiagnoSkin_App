package es.studium.operacionesdb_diagnosticos

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class AltaRemotaDiagnosticos {
    val client = OkHttpClient()

    suspend fun darAltaDiagnosticoEnBD(
        fecha: String,
        diagnostico: String,
        gravedad: String,
        imagenBytes: ByteArray,
        idMedicoFK: String,
        idPacienteFK: String,

    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val imagenBase64 = Base64.encodeToString(imagenBytes, Base64.DEFAULT)

                val formBody = FormBody.Builder()
                    .add("fechaDiagnostico", fecha)
                    .add("diagnosticoDiagnostico", diagnostico)
                    .add("gravedadDiagnostico", gravedad)
                    .add("fotoDiagnostico", imagenBase64)
                    .add("idMedicoFK", idMedicoFK)
                    .add("idPacienteFK", idPacienteFK)
                    .build()

                val request = Request.Builder()
                    .url("http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                Log.d("RESPUESTA_AltaDiagnostico", response.body?.string() ?: "Sin cuerpo")
                response.isSuccessful
            } catch (e: IOException) {
                Log.e("ErrorInsercionDiagnostico", "Error al insertar diagnóstico: ${e.message}")
                false
            }
        }
    }
}