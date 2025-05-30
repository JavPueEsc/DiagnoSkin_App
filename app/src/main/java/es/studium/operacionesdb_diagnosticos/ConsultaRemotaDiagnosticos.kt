package es.studium.operacionesdb_diagnosticos

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONArray
import org.json.JSONException

class ConsultaRemotaDiagnosticos {
    //Crear una instancia de okHttpClient
    val client = OkHttpClient()
    var resultado : JSONArray = JSONArray()

    //Obtener todos los diagnosticos
    fun obtenerListado():JSONArray{
        val request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php")
            .build()
        return ejecutarPeticion(request)
    }

    // Obtener los diagnosticos entre dos fechas
    fun obtenerListadoEntreFechas(fechaDesde: String, fechaHasta: String): JSONArray {
        val url = "http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php?fechaDesde=$fechaDesde&fechaHasta=$fechaHasta"
        val request = Request.Builder()
            .url(url)
            .build()
        return ejecutarPeticion(request)
    }

    //Obtener diagnóstico por idPacienteFK
    fun obtenerDiagnosticoPorIdPacienteFK(idPacienteFK: String?): JSONArray {
        val url = "http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php?idDiagnostico=$idPacienteFK"
        val request = Request.Builder()
            .url(url)
            .build()
        return ejecutarPeticion(request)
    }

    //Obtener numero de diagnostico de una patologia determinada
    fun obtenerNumDiagPorPatologia(idCentroMedico: String?, patologia: String, fechaDesde: String, fechaHasta: String): JSONArray {
        val url = "http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php?idCentroMedico=${idCentroMedico}&diagnosticoDiagnostico=${patologia}&fechaDesde=${fechaDesde}&fechaHasta=${fechaHasta}"
        val request = Request.Builder()
            .url(url)
            .build()
        return ejecutarPeticion(request)
    }

    // Metodo ejecutar la solicitud
    private fun ejecutarPeticion(request: Request): JSONArray {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: ""
                Log.d("ConsultaRemotaDiagnosticos", "Respuesta: $bodyString")  // Para ver qué recibes

                return try {
                    JSONArray(bodyString)  // Intentas parsear aquí
                } catch (e: JSONException) {
                    Log.e("ConsultaRemotaDiagnosticos", "Respuesta no es JSON válido")
                    Log.e("ConsultaRemotaDiagnosticos", bodyString)
                    JSONArray()  // Devuelves un JSONArray vacío para evitar crash
                }
            } else {
                Log.e("ConsultaRemotaDiagnosticos", "Error HTTP: ${response.code} - ${response.message}")
            }
        } catch (e: IOException) {
            Log.e("ConsultaRemotaDiagnosticos", e.message ?: "Error desconocido")
        }
        return JSONArray()  // Devuelve vacío si falla
    }

}