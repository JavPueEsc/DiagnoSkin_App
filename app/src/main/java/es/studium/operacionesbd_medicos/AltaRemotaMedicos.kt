package es.studium.operacionesbd_medicos

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class AltaRemotaMedicos {
    val client = OkHttpClient()

    fun darAltaMedicoEnBD(nombre:String, apellidos : String, telefono : String, email:String, especialidad : String, numColegiado : String, esAdmin:String, idCentroMedicoFK : String, idUsuarioFK : String) : Boolean{
        var correcta : Boolean = true;

        //Montamos la peticion POST
        val formBody = FormBody.Builder()
            .add("nombreMedico",nombre)
            .add("apellidosMedico", apellidos)
            .add("telefonoMedico", telefono)
            .add("emailMedico",email)
            .add("especialidadMedico", especialidad)
            .add("numColegiadoMedico", numColegiado)
            .add("esAdminMedico",esAdmin)
            .add("idCentroMedicoFK", idCentroMedicoFK)
            .add("idUsuarioFK", idUsuarioFK)
            .build()

        var request : Request = Request.Builder()
            .url("http://192.168.0.217/ApiRestDiagnoSkin/medicos.php")
            .post(formBody)
            .build()

        val call = client.newCall(request)

        try{
            val response = call.execute()
            Log.d("AltaRemotaMedicos", response.body?.string() ?: "Sin cuerpo")
            return response.isSuccessful
        }
        catch(e : IOException){
            return false
        }
    }
}