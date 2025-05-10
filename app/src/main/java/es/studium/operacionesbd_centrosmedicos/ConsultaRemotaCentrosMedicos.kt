package es.studium.operacionesbd_centrosmedicos

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import okio.IOException

class ConsultaRemotaCentrosMedicos {
    //Crear una instancia de okHttpClient
    val client = OkHttpClient()
    var resultado : JSONArray = JSONArray()

    //obtener listado de todos los centros médicos
    fun obtenerListado(): JSONArray {
        val request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/centrosmedicos.php")
            .build()
        return ejecutarPeticion(request)
    }
    //Obtener centromedico por nombre
    fun obtenerIdCentroMedicoPorNombre(nombreCentroMedico: String): JSONArray {
        val url = "http://192.168.0.217/ApiRestDiagnoSkin/centrosmedicos.php?nombreCentroMedico=$nombreCentroMedico"
        val request = Request.Builder()
            .url(url)
            .build()
        return ejecutarPeticion(request)
    }
    //Obtener centromedico por nombre
    fun obtenerCentroMedicoPorId(idCentroMedico: String?): JSONArray {
        val url = "http://192.168.0.217/ApiRestDiagnoSkin/centrosmedicos.php?idCentroMedico=$idCentroMedico"
        val request = Request.Builder()
            .url(url)
            .build()
        return ejecutarPeticion(request)
    }

    private fun ejecutarPeticion(request: Request): JSONArray {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("ConsultaRemotaCentrosMedicos", "Respuesta cruda: $responseBody") // Muestra el JSON recibido
                resultado = JSONArray(responseBody)
            } else {
                Log.e("ConsultaRemotaCentrosMedicos", "Error HTTP: ${response.message}")
            }
        } catch (e: IOException) {
            Log.e("ConsultaRemotaCentrosMedicos", "IOException: ${e.message}")
        } catch (e: JSONException) {
            Log.e("ConsultaRemotaCentrosMedicos", "JSONException: ${e.message}")
            throw RuntimeException(e)
        }
        return resultado
    }
}