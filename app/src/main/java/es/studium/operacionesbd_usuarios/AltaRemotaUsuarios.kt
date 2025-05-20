package es.studium.operacionesbd_usuarios

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
class AltaRemotaUsuarios {
    val client = OkHttpClient()

    fun darAltaUsuarioEnBD(nombre:String, clave : String, fecha : String) : Boolean{
        var correcta : Boolean = true;

        //Montamos la peticion POST
        val formBody = FormBody.Builder()
            .add("nombreUsuario",nombre)
            .add("contrasenaUsuario", clave)
            .add("fechaAltaUsuario", fecha)
            .build()

        var request : Request = Request.Builder()
            .url("http://192.168.0.216/ApiRestDiagnoSkin/usuarios.php")
            .post(formBody)
            .build()

        val call = client.newCall(request)

        try{
            val response = call.execute()
            Log.d("AltaRemotaUsuarios", response.body?.string() ?: "Sin cuerpo")
            return response.isSuccessful
        }
        catch(e : IOException){
            return false
        }
    }
}