package es.studium.login

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.studium.diagnoskin_app.R
import es.studium.operacionesbdmedicos.ConsultaRemotaMedicos
import es.studium.operacionesbdusuarios.ConsultaRemotaUsuarios
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AltaDatosPersonalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_alta_datos_personales)

    }
}