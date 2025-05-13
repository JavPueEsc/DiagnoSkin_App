package es.studium.login

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.studium.diagnoskin_app.R
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_usuarios.ConsultaRemotaUsuarios
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AltaDatosDeAccesoActivity : AppCompatActivity() {
    //Declaracion de las vistas
    private lateinit var txt_nombreUsuario: EditText
    private lateinit var txt_numColegiado: EditText
    private lateinit var txt_clave1: EditText
    private lateinit var txt_clave2: EditText
    private lateinit var btn_Aceptar: Button
    private lateinit var btn_volver : ImageView

    //Declaración datos introducidos
    private lateinit var nombreUsuarioIntroducido: String
    private lateinit var numColegiadoIntroducido: String
    private lateinit var clave1Introducida: String
    private lateinit var clave2Introducida: String

    //Declaración objetos consulta
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    lateinit var idUsuarioBD: String
    lateinit var nombreUsuarioBD: String
    lateinit var claveUsuarioBD: String
    lateinit var fechaAltaUsuarioBD: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_alta_datos_de_acceso)
        //Asociación de variables con Vistas
        txt_nombreUsuario = findViewById(R.id.LO_txt_IntroducirUsuario)
        txt_numColegiado = findViewById(R.id.LO_txt_IntroducirNumColegiado)
        txt_clave1 = findViewById(R.id.LO_txt_IntroducirClave1)
        txt_clave2 = findViewById(R.id.LO_txt_IntroducirClave2)
        btn_Aceptar = findViewById(R.id.LO_btn_AceptarDatosAcceso)
        btn_volver = findViewById(R.id.btnVolver_AltaDatosDeAccesoActivity)

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Gestión del botón Aceptar
        btn_Aceptar.setOnClickListener {
            nombreUsuarioIntroducido = txt_nombreUsuario.text.toString()
            numColegiadoIntroducido = txt_numColegiado.text.toString()
            clave1Introducida = txt_clave1.text.toString()
            clave2Introducida = txt_clave2.text.toString()

            //Control de errores
            if (nombreUsuarioIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_NombreUsuarioVacio, Toast.LENGTH_SHORT)
                    .show()
            } else if (numColegiadoIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_NumColegiadoVacio, Toast.LENGTH_SHORT).show()
            } else if (clave1Introducida.isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_Clave1Vacia, Toast.LENGTH_SHORT).show()
            }else if(clave1Introducida.length<6){
                Toast.makeText(this, R.string.LO_Toast_CleveMenos6caracteres, Toast.LENGTH_SHORT).show()
            }else if (clave2Introducida.isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_Clave2Vacia, Toast.LENGTH_SHORT).show()
            }else if (clave1Introducida != clave2Introducida) {
                Toast.makeText(this, R.string.LO_Toast_ClavesNoCoinciden, Toast.LENGTH_SHORT)
                    .show()
            } else {
                //1. Comprobación de que no existe el usuario en la BBDD
                if (consultarExistenciaUsuario(nombreUsuarioIntroducido.trim())) {
                    Toast.makeText(this, R.string.LO_Toast_NombreUsuarioExiste, Toast.LENGTH_SHORT)
                        .show()
                } else if (consultarExistenciaMedico(numColegiadoIntroducido.trim())) {
                    Toast.makeText(this, R.string.LO_Toast_MedicoExiste, Toast.LENGTH_SHORT).show()
                }  else {
                    val bundle = Bundle()
                    bundle.putString("numColegiadoMedico", numColegiadoIntroducido.trim())
                    bundle.putString("nombreUsuario", nombreUsuarioIntroducido)
                    bundle.putString("claveUsuario", clave1Introducida)

                    val intent = Intent(this, AltaDatosPersonalesActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        }
    }

    fun consultarExistenciaUsuario(nombreUsuario: String): Boolean {
        var existeUsuario: Boolean = false
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaUsuarios = ConsultaRemotaUsuarios()
        result = consultaRemotaUsuarios.obtenerListado()
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idUsuarioBD = jsonObject.getString("idUsuario")
                    nombreUsuarioBD = jsonObject.getString("nombreUsuario")
                    claveUsuarioBD = jsonObject.getString("contrasenaUsuario")
                    fechaAltaUsuarioBD = jsonObject.getString("fechaAltaUsuario")

                    if (nombreUsuarioBD == nombreUsuario) {
                        existeUsuario = true
                        break //<-- salimos del bucle
                    }
                }
            } else {
                Log.e("MainActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
        return existeUsuario
    }

    fun consultarExistenciaMedico(numColegiadoMedico: String): Boolean {
        var existeMedico: Boolean = false
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorNumCol(numColegiadoMedico)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    var idMedicoBD = jsonObject.getString("idMedico")
                    var nombreMedicoBD = jsonObject.getString("nombreMedico")
                    var apellidosMedicoBD = jsonObject.getString("apellidosMedico")
                    var telefonoMedicoBD = jsonObject.getString("telefonoMedico")
                    var emailMedicoBD = jsonObject.getString("emailMedico")
                    var especialidadMedicoBD = jsonObject.getString("especialidadMedico")
                    var numColegiadoMedicoBD = jsonObject.getString("numColegiadoMedico")
                    var esAdminMedicoBD = jsonObject.getString("esAdminMedico")
                    var idCentroMedicoFKBD = jsonObject.getString("idCentroMedicoFK")
                    var idUsuarioFKBD = jsonObject.getString("idUsuarioFK")
                    if (numColegiadoMedicoBD == numColegiadoMedico) {
                        existeMedico = true
                        break //<-- salimos del bucle
                    }
                }
            } else {
                Log.e("MainActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
        return existeMedico
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }
}
