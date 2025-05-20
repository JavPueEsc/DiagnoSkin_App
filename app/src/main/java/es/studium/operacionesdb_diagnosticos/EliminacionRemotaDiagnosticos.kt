package es.studium.operacionesdb_diagnosticos

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class EliminacionRemotaDiagnosticos {
    val client = OkHttpClient()
    private var correcta: Boolean = true
    fun eliminarDiagnostico(id: String): Boolean {

        var request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/diagnosticos.php?idDiagnostico=$id")
            .delete()
            .build()
        var call = client.newCall(request)
        try {
            var response = call.execute()
            Log.i("EliminacionRemotaDiagnosticos", response.toString())
            correcta = true
            return correcta
        } catch (e: IOException) {
            Log.e("EliminacionRemotaDiagnosticos", e.message.toString())
            correcta = false
            return correcta
        }
    }
}