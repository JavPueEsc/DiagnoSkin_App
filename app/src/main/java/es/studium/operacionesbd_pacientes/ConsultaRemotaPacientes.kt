package es.studium.operacionesbd_pacientes
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
class ConsultaRemotaPacientes {
    //Crear una instancia de okHttpClient
    val client = OkHttpClient()
    var resultado : JSONArray = JSONArray()

    //obtener listado de todos los pacientes
    fun obtenerListado():JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/pacientes.php")
            .build()
        return ejecutarPeticion(request)
    }

    fun obtenerPacientePorNuhsa(nuhsaPaciente:String):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/pacientes.php?nuhsaPaciente=$nuhsaPaciente")
            .build()
        return ejecutarPeticion(request)
    }

    fun obtenerPacientePorId(idPaciente:String):JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/pacientes.php?idPaciente=$idPaciente")
            .build()
        return ejecutarPeticion(request)
    }

    private fun ejecutarPeticion(request: Request): JSONArray {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("ConsultaRemoraPacientes", "Respuesta cruda: $responseBody") // Muestra el JSON recibido
                resultado = JSONArray(responseBody)
            } else {
                Log.e("ConsultaRemoraPacientes", "Error HTTP: ${response.message}")
            }
        } catch (e: IOException) {
            Log.e("ConsultaRemoraPacientes", "IOException: ${e.message}")
        } catch (e: JSONException) {
            Log.e("ConsultaRemoraPacientes", "JSONException: ${e.message}")
            throw RuntimeException(e)
        }
        return resultado
    }
}