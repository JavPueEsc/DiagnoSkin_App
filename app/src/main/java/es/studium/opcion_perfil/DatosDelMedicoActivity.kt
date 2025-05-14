package es.studium.opcion_perfil

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.ModeloMedico
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DatosDelMedicoActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_idMedico : TextView
    private lateinit var lbl_nombreMedico : TextView
    private lateinit var lbl_apellidosMedico : TextView
    private lateinit var lbl_NumColegiadoMedico : TextView
    private lateinit var lbl_especialidadMedico : TextView
    private lateinit var lbl_centroTrabajoMedico : TextView
    private lateinit var lbl_telefonoMedico : TextView
    private lateinit var lbl_emailMedico : TextView
    private lateinit var btn_volver : ImageView
    private lateinit var btn_modificar : Button

    //Declaración de los extras recibidos
    private lateinit var esMedicoAdminRecibido : String
    private lateinit var idMedicoRecibido : String
    private lateinit var idUsuarioRecibido : String

    //Variables para consulta base de datos
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject

    //Variables consulta Medicos
    private lateinit var idMedicoBD : String
    private lateinit var nombreMedicoBD : String
    private lateinit var apellidosMedicoBD : String
    private lateinit var telefonoMedicoBD : String
    private lateinit var emailMedicoBD : String
    private lateinit var especialidadMedicoBD : String
    private lateinit var numColegiadoMedicoBD : String
    private lateinit var esAdminMedicoBD : String
    private lateinit var idCentroMedicoFKBD : String
    private lateinit var idUsuarioFKBD : String

    //consulta centro medico
    private lateinit var idCentroMedicoBD: String
    private lateinit var nombreCentroMedicoBD: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.per_activity_datos_del_medico)
        //Recibir EXTRAs con los datos del usuario médico
        val extras = intent.extras
        if (extras != null) {
                procesarExtras(extras)
        }

        //Enlazar las vistas
        lbl_idMedico = findViewById(R.id.PER_lbl_id_datosDelMedico)
        lbl_nombreMedico = findViewById(R.id.PER_lbl_nombre_datosDelMedico)
        lbl_apellidosMedico = findViewById(R.id.PER_lbl_apellidos_datosDelMedico)
        lbl_NumColegiadoMedico =findViewById(R.id.PER_lbl_numColegiado_datosDelMedico)
        lbl_especialidadMedico =findViewById(R.id.PER_lbl_especialidad_datosDelMedico)
        lbl_centroTrabajoMedico =findViewById(R.id.PER_lbl_centro_de_trabajo_datosDelMedico)
        lbl_telefonoMedico =findViewById(R.id.PER_lbl_telefono_datosDelMedico)
        lbl_emailMedico =findViewById(R.id.PER_lbl_email_datosDelMedico)
        //btn_volver = findViewById(R.id.btnVolver_DatosDelMedico)
        btn_modificar = findViewById(R.id.PER_btn_Modificar_datosPaciente)

        consultarDatosMedico(idMedicoRecibido)
        lbl_idMedico.text = getString(R.string.PER_lbl_id_datosDelMedico, idMedicoBD)
        lbl_nombreMedico.text = getString(R.string.PER_lbl_nombre_datosDelMedico,nombreMedicoBD)
        lbl_apellidosMedico.text = getString(R.string.PER_lbl_apellidos_datosDelMedico,apellidosMedicoBD)
        lbl_NumColegiadoMedico.text =getString(R.string.PER_lbl_numColegiado_datosDelMedico,numColegiadoMedicoBD)
        lbl_especialidadMedico.text =getString(R.string.PER_lbl_especialidad_datosDelMedico,especialidadMedicoBD)
        lbl_telefonoMedico.text =getString(R.string.PER_lbl_telefono_datosDelMedico, telefonoMedicoBD)
        lbl_emailMedico.text =getString(R.string.PER_lbl_email_datosDelMedico,emailMedicoBD)

        consultaCentroMedico(idCentroMedicoFKBD)
        lbl_centroTrabajoMedico.text = getString(R.string.PER_lbl_centro_de_trabajo_datosDelMedico, nombreCentroMedicoBD)

        //gestión del botón volver
        /*btn_volver.setOnClickListener{
            enviarIntentAMenu(idUsuarioRecibido)
        }*/

        //Gestión del botón modificar
        btn_modificar.setOnClickListener {
            //logica aquí <----------------------------------
        }
    }

    fun consultarDatosMedico(idMedico: String) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorId(idMedico)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idMedicoBD = jsonObject.getString("idMedico")
                    nombreMedicoBD = jsonObject.getString("nombreMedico")
                    apellidosMedicoBD = jsonObject.getString("apellidosMedico")
                    telefonoMedicoBD = jsonObject.getString("telefonoMedico")
                    emailMedicoBD = jsonObject.getString("emailMedico")
                    especialidadMedicoBD = jsonObject.getString("especialidadMedico")
                    numColegiadoMedicoBD = jsonObject.getString("numColegiadoMedico")
                    esAdminMedicoBD = jsonObject.getString("esAdminMedico")
                    idCentroMedicoFKBD = jsonObject.getString("idCentroMedicoFK")
                    idUsuarioFKBD = jsonObject.getString("idUsuarioFK")
                }
            } else {
                Log.e("DatosDelMedicoActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("DatosDelMedicoActivity", "Error al procesar el JSON", e)
        }
    }

    fun consultaCentroMedico(idCentroMedico: String?) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = consultaRemotaCentrosMedicos.obtenerCentroMedicoPorId(idCentroMedico)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    var idCentroMedicoBD = jsonObject.getString("idCentroMedico")
                    nombreCentroMedicoBD = jsonObject.getString("nombreCentroMedico")
                }
            } else {
                Log.e("DatosDelMedicoActivity", "El JSONObject de centro medico está vacío")
            }
        } catch (e: JSONException) {
            Log.e("DatosDelMedicoActivity", "Error al procesar el JSON", e)
        }
    }

    //Médodo para recibir los extras
    private fun procesarExtras(extras: Bundle) {
        esMedicoAdminRecibido = extras.getString("esAdminMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuario")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    //Enviar intent de vuelta
    fun enviarIntentAMenu(idUsuario:String?){
        val intent = Intent(this@DatosDelMedicoActivity, MainActivity::class.java)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        enviarIntentAMenu(idUsuarioRecibido)
    }
}