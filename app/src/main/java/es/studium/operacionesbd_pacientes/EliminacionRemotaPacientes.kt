package es.studium.operacionesbd_pacientes

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class EliminacionRemotaPacientes {
    val client = OkHttpClient()
    private var correcta: Boolean = true
    fun eliminarPaciente(id: String): Boolean {

        var request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/pacientes.php?idPaciente=$id")
            .delete()
            .build()
        var call = client.newCall(request)
        try {
            var response = call.execute()
            Log.i("EliminacionRemotaPacientes", response.toString())
            correcta = true
            return correcta
        } catch (e: IOException) {
            Log.e("EliminacionRemotaPacientes", e.message.toString())
            correcta = false
            return correcta
        }
    }
}