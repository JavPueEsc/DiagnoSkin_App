package es.studium.operacionesbdusuarios
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
class ConsultaRemotaUsuarios {
    //Crear una instancia de okHttpClient
    val client = OkHttpClient()
    var resultado : JSONArray = JSONArray()

    //obtener listado de todos los usuarios
    fun obtenerListado():JSONArray{
        val request = Request.Builder()
            .url("http://192.168.1.49/ApiRestDiagnoSkin/usuarios.php")
            .build()
        return ejecutarPeticion(request)
    }

    private fun ejecutarPeticion(request: Request): JSONArray {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("AccesoRemoto", "Respuesta cruda: $responseBody") // Muestra el JSON recibido
                resultado = JSONArray(responseBody)
            } else {
                Log.e("AccesoRemoto", "Error HTTP: ${response.message}")
            }
        } catch (e: IOException) {
            Log.e("AccesoRemoto", "IOException: ${e.message}")
        } catch (e: JSONException) {
            Log.e("AccesoRemoto", "JSONException: ${e.message}")
            throw RuntimeException(e)
        }
        return resultado
    }

}