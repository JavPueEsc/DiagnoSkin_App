package es.studium.operacionesbd_medicos
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
class ConsultaRemotaMedicos {
    //Crear una instancia de okHttpClient
    val client = OkHttpClient()
    var resultado : JSONArray = JSONArray()

    //obtener listado de todos los médicos
    fun obtenerListado():JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php")
            .build()
        return ejecutarPeticion(request)
    }

    fun obtenerMedicoPorId(idMedico:String?):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php?idMedico=$idMedico")
            .build()
        return ejecutarPeticion(request)
    }

    fun obtenerMedicoPorNumCol(numColegiadoMedico:String):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php?numColegiadoMedico=$numColegiadoMedico")
            .build()
        return ejecutarPeticion(request)
    }

    fun obtenerMedicoPorIdUsuarioFK(idUsuarioFK:String):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php?idUsuarioFK=$idUsuarioFK")
            .build()
        return ejecutarPeticion(request)
    }

    fun saberSiUsuarioEstaBloqueado(nombreUsuario:String):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/medicos.php?nombreUsuario=$nombreUsuario")
            .build()
        return ejecutarPeticion(request)
    }

    private fun ejecutarPeticion(request: Request): JSONArray {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("ConsultaRemotaMedicos", "Respuesta cruda: $responseBody") // Muestra el JSON recibido
                resultado = JSONArray(responseBody)
            } else {
                Log.e("ConsultaRemotaMedicos", "Error HTTP: ${response.message}")
            }
        } catch (e: IOException) {
            Log.e("ConsultaRemotaMedicos", "IOException: ${e.message}")
        } catch (e: JSONException) {
            Log.e("ConsultaRemotaMedicos", "JSONException: ${e.message}")
            throw RuntimeException(e)
        }
        return resultado
    }
}