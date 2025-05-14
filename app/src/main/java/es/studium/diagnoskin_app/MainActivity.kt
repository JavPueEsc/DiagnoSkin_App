package es.studium.diagnoskin_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.studium.modelos_y_utiles.ModeloMedico
import es.studium.opcion_Estadisticas.EstadisticasActivity
import es.studium.opcion_administrador.PrincipalMedicosActivity
import es.studium.opcion_diagnosticos.PrincipalPacientes2Activity
import es.studium.opcion_pacientes.PrincipalPacientesActivity
import es.studium.opcion_perfil.DatosDelMedicoActivity
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var btn_pacientes : View
    private lateinit var btn_diagnosticos : View
    private lateinit var btn_informes : View
    private lateinit var btn_estadisticas : View
    private lateinit var btn_perfil : View
    private lateinit var btn_administrador : View

    //Declaración del usuario recibido del Login
    private lateinit var idUsuarioRecibido : String

    //Declaración objetos consulta de los datos del médico usuario
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
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

    //Datos usuario Medico que maneja la sesión
    private lateinit var usuarioMedico : ModeloMedico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_pacientes = findViewById(R.id.MP_btn_Pacientes)
        btn_diagnosticos = findViewById(R.id.MP_btn_Diagnosticos)
        btn_informes = findViewById(R.id.MP_btn_Informes)
        btn_estadisticas = findViewById(R.id.MP_btn_Estadisticas)
        btn_perfil = findViewById(R.id.MP_btn_Perfil)
        btn_administrador = findViewById(R.id.MP_btn_Admin)

        //Recepción del idUsuario proveniente del Login
        val extras = intent.extras
        if (extras != null) {
            idUsuarioRecibido = extras.getString("idUsuario")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
        }

        //Consulta a la base de datos de medicos por idUsuarioFK
        consultarDatosMedicoUsuario(idUsuarioRecibido)

        //1. Gestión del botón Pacientes
        btn_pacientes.setOnClickListener {
            enviarIntent(PrincipalPacientesActivity::class.java,"MainActivity",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
        //2. Gestión del botón Diagnósticos
        btn_diagnosticos.setOnClickListener {
            enviarIntent(PrincipalPacientes2Activity::class.java,"OrigenBtnDiagnosticos",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
        //3. Gestión del botón informes
        btn_informes.setOnClickListener {
            enviarIntent(PrincipalPacientes2Activity::class.java,"OrigenBtnInformes",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
        //4. Gestión del botón estadísticas
        btn_estadisticas.setOnClickListener {
            enviarIntent(EstadisticasActivity::class.java,"OrigenBtnEstadisticas",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
        //5. Gestión del botón perfil
        btn_perfil.setOnClickListener {
            enviarIntent(DatosDelMedicoActivity::class.java,"OrigenBtnPerfil",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
        //6. Gestión del botón Administrador
        btn_administrador.setOnClickListener {
            enviarIntent(PrincipalMedicosActivity::class.java,"OrigenBtnAdministrador",usuarioMedico.esAdminMedico, usuarioMedico.idMedico, idUsuarioRecibido)
        }
    }

    fun consultarDatosMedicoUsuario(idUsuarioFK: String): Boolean {
        var datosExtraidos: Boolean = false
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorIdUsuarioFK(idUsuarioFK)
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
                    if (idUsuarioFKBD == idUsuarioFK) {
                        datosExtraidos = true
                        usuarioMedico = ModeloMedico(idMedicoBD, nombreMedicoBD, apellidosMedicoBD, telefonoMedicoBD, emailMedicoBD,
                            especialidadMedicoBD, numColegiadoMedicoBD, esAdminMedicoBD, idCentroMedicoFKBD, idUsuarioFKBD)
                        //Se oculta el botón de Administrador si el usuario no lo es.
                        if(usuarioMedico.esAdminMedico=="0"){
                            Log.d("esAdmin", "esAdminRecibido: $usuarioMedico.esAdminMedicoBD")
                            btn_administrador.visibility = View.GONE
                        }
                        break //<-- salimos del bucle
                    }
                }
            } else {
                Log.e("MainActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
        return datosExtraidos
    }

    private fun enviarIntent(
        activityDestino: Class<out Activity>, claveOrigen: String ,esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@MainActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Cierra completamente la app
        finishAffinity() // Cierra todas las actividades
    }
}