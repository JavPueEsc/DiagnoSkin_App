package es.studium.opcionpacientes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ResumenDiagnosticoActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_apellidosNombre: TextView
    private lateinit var lbl_fecha: TextView
    private lateinit var img_fotoDiagnostico: ImageView
    private lateinit var lbl_diagnosticoDiagnostico: TextView
    private lateinit var lbl_tipoDiagnostico: TextView
    private lateinit var lbl_medicoDiagnostico: TextView
    private lateinit var lbl_centroMedicoDiagnostico: TextView
    private lateinit var btn_guardarDiagnostico: Button
    private lateinit var btn_volver: ImageView

    //Declaración de variables para recibir los extras
    //Variable para extra recibido
    private var idPacienteRecibido: String? = ""
    private var nombrePacienteRecibido: String? = ""
    private var apellidosPacienteRecibido: String? = ""
    private var sexoPacienteRecibido: String? = ""
    private var fechaNacPacienteRecibido: String? = ""
    private var nuhsaPacienteRecibido: String? = ""
    private var telefonoPacienteRecibido: String? = ""
    private var emailPacienteRecibido: String? = ""
    private var dniPacienteRecibido: String? = ""
    private var direccionPacienteRecibido: String? = ""
    private var localidadPacienteRecibido: String? = ""
    private var provinciaPacienteRecibido: String? = ""
    private var codigoPostalPacienteRecibido: String? = ""
    private var esAdminMedicoRecibido: String? = ""
    private var idMedicoRecibido: String? = ""
    private var idUsuarioRecibido: String? = ""
    private var fechaDiagnosticoRecibida: String? = ""
    private var diagnosticoRecibido: String? = ""
    private var tipoDiagnosticoRecibido: String? = ""
    private var fotoDiagnosticoRecibida: String? = ""

    //Declaración de las variables para extraer medico y nombre del centroMedico de bbdd
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var nombreMedicoBD : String
    private lateinit var apellidosMedicoBD : String
    private lateinit var idCentroMedicoFKBD : String
    private lateinit var nombreCentroMedicoBD : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_xdiag_activity_resumen_diagnostico)
        val extras = intent.extras
        if (extras != null) {
            idPacienteRecibido = extras.getString("idPaciente")
            nombrePacienteRecibido = extras.getString("nombrePaciente")
            apellidosPacienteRecibido = extras.getString("apellidosPaciente")
            sexoPacienteRecibido = extras.getString("sexoPaciente")
            fechaNacPacienteRecibido = extras.getString("fechaNacPaciente")
            nuhsaPacienteRecibido = extras.getString("nuhsaPaciente")
            telefonoPacienteRecibido = extras.getString("telefonoPaciente")
            emailPacienteRecibido = extras.getString("emailPaciente")
            dniPacienteRecibido = extras.getString("dniPaciente")
            direccionPacienteRecibido = extras.getString("direccionPaciente")
            localidadPacienteRecibido = extras.getString("localidadPaciente")
            provinciaPacienteRecibido = extras.getString("provinciaPaciente")
            codigoPostalPacienteRecibido = extras.getString("codigoPostalPaciente")
            esAdminMedicoRecibido = extras.getString("esAdminMedico")
            idMedicoRecibido = extras.getString("idMedico")
            idUsuarioRecibido = extras.getString("idUsuario")
            fechaDiagnosticoRecibida = extras.getString("fechaDiagnostico")
            diagnosticoRecibido = extras.getString("diagnosticoDiagnostico")
            tipoDiagnosticoRecibido = extras.getString("tipoDiagnostico")
            fotoDiagnosticoRecibida = extras.getString("fotoDiagnostico")
        }
        //Enlazar vistas
        lbl_apellidosNombre = findViewById(R.id.PA_XDIAG_lbl_apellidosNombre_ResumenDiagnostico)
        lbl_fecha = findViewById(R.id.PA_XDIAG_lbl_fecha_ResumenDiagnostico)
        img_fotoDiagnostico = findViewById(R.id.PA_XDIAG_fotoDiagnostico_ResumenDiagnosticos)
        lbl_diagnosticoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_diagnostico_ResumenDiagnostico)
        lbl_tipoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_tipo_ResumenDiagnostico)
        lbl_medicoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_medico_ResumenDiagnostico)
        lbl_centroMedicoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_centro_ResumenDiagnostico)
        btn_guardarDiagnostico =
            findViewById(R.id.PA_XDIAG_btn_GuardarDiagnostico_ResumenDiagnosticos)
        btn_volver = findViewById(R.id.btnVolver_ResumenDiagnostico)

        //Gestión del botón volver
        btn_volver.setOnClickListener {
            enviarIntentVuelta(
                PrincipalDiagnosticosActivity::class.java,
                "ResumenDiagnosticosActivity", idPacienteRecibido,
                nombrePacienteRecibido, apellidosPacienteRecibido, sexoPacienteRecibido, fechaNacPacienteRecibido, nuhsaPacienteRecibido, telefonoPacienteRecibido,
                emailPacienteRecibido, dniPacienteRecibido, direccionPacienteRecibido, localidadPacienteRecibido, provinciaPacienteRecibido, codigoPostalPacienteRecibido,
                esAdminMedicoRecibido, idMedicoRecibido, idUsuarioRecibido
            )
        }

        //Establecemos la información en las etiquetas correspondientes
        lbl_apellidosNombre.text = getString(R.string.PA_XDIA_lbl_apellidosNombre_ResumenDiagnosticos,apellidosPacienteRecibido,nombrePacienteRecibido)
        lbl_fecha.text = getString(R.string.PA_XDIA_lbl_fecha_ResumenDiagnosticos,fechaMysqlAEuropea(fechaDiagnosticoRecibida))
        if (fotoDiagnosticoRecibida != null) {
            establecerImagenDesdeUri(Uri.parse(fotoDiagnosticoRecibida),img_fotoDiagnostico)
        } else {
            // Maneja el caso en que no se recibe la imagen
            Toast.makeText(this, R.string.PA_XDIA_lbl_ToastErrorImagenVacia_ResumenDiagnosticos, Toast.LENGTH_SHORT).show()
        }
        lbl_diagnosticoDiagnostico.text = getString(R.string.PA_XDIA_lbl_diagnostico_ResumenDiagnosticos,diagnosticoRecibido)
        lbl_tipoDiagnostico.text = getString(R.string.PA_XDIA_lbl_tipo_ResumenDiagnosticos, tipoDiagnosticoRecibido)
        if(idMedicoRecibido!=null){
            consultarMedico(idMedicoRecibido)
            lbl_medicoDiagnostico.text=getString(R.string.PA_XDIA_lbl_medico_ResumenDiagnosticos,apellidosMedicoBD,nombreMedicoBD)
        }
        if(idCentroMedicoFKBD!=null){
            consultaCentroMedico(idCentroMedicoFKBD)
            lbl_centroMedicoDiagnostico.text = getString(R.string.PA_XDIA_lbl_nombreCentro_ResumenDiagnosticos,nombreCentroMedicoBD)
        }

        //Gestión del botón Guardar
        btn_guardarDiagnostico.setOnClickListener {
            //Insertar diagnostico en bbdd
            //Intent a principalDiagnosticos (mandar mismos extras que recibe ya principalDiagnosticos) <------------------
        }


    }
    // Metodo para establecer la imagen en el ImageView a partir de una URI
    private fun establecerImagenDesdeUri(uri: Uri, imageView: ImageView) {
        imageView.setImageURI(uri)
    }

    //Metodo para transformar de URI a ByteArray y así poder pasarselo al
    // metodo que inserta en la base de datos
    fun uriAByteArray(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: ByteArray(0)
    }

    //Enviar intent de vuelta (a RealizarDiagnosticosActivity)
    private fun enviarIntentVuelta(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?,
        sexo: String?, fechaNac: String?, nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?,
        localidad: String?, provincia: String?, codigoPostal: String?, esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@ResumenDiagnosticoActivity, activityDestino::class.java)
        intent.putExtra("origenResumenDiagnosticosActivity", claveOrigen)
        intent.putExtra("idPaciente", idPaciente)
        intent.putExtra("nombrePaciente", nombre)
        intent.putExtra("apellidosPaciente", apellidos)
        intent.putExtra("sexoPaciente", sexo)
        intent.putExtra("fechaNacPaciente", fechaNac)
        intent.putExtra("nuhsaPaciente", nuhsa)
        intent.putExtra("telefonoPaciente", telefono)
        intent.putExtra("emailPaciente", email)
        intent.putExtra("dniPaciente", dni)
        intent.putExtra("direccionPaciente", direccion)
        intent.putExtra("localidadPaciente", localidad)
        intent.putExtra("provinciaPaciente", provincia)
        intent.putExtra("codigoPostalPaciente", codigoPostal)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    fun fechaMysqlAEuropea(fecha: String?): String {
        var fechaTransformada = getString(R.string.PA_error_ModificarFecha)
        if (fecha != null) {
            val elementosFecha = fecha.split("-")
            if (elementosFecha.size == 3) {
                fechaTransformada = "${elementosFecha[2]}/${elementosFecha[1]}/${elementosFecha[0]}"
            }
        }
        return fechaTransformada
    }

    fun consultarMedico(idMedico: String?) {
        var existeMedico: Boolean = false
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
                    var idMedicoBD = jsonObject.getString("idMedico")
                    nombreMedicoBD = jsonObject.getString("nombreMedico")
                    apellidosMedicoBD = jsonObject.getString("apellidosMedico")
                    var telefonoMedicoBD = jsonObject.getString("telefonoMedico")
                    var emailMedicoBD = jsonObject.getString("emailMedico")
                    var especialidadMedicoBD = jsonObject.getString("especialidadMedico")
                    var numColegiadoMedicoBD = jsonObject.getString("numColegiadoMedico")
                    var esAdminMedicoBD = jsonObject.getString("esAdminMedico")
                    idCentroMedicoFKBD = jsonObject.getString("idCentroMedicoFK")
                    var idUsuarioFKBD = jsonObject.getString("idUsuarioFK")
                    if (idMedicoBD == idMedico) {
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
                    var telefonoCentroMedicoBD = jsonObject.getString("telefonoCentroMedico")
                    var latitudCentroMedicoBD = jsonObject.getString("latitudCentroMedico")
                    var longitudCentroMedicoBD = jsonObject.getString("longitudCentroMedico")
                    var direccionCentroMedicoBD = jsonObject.getString("direccionCentroMedico")
                    var localidadCentroMedicoBD = jsonObject.getString("localidadCentroMedico")
                    var codigoPostalCentroMedicoBD = jsonObject.getString("codigoPostalCentroMedico")
                    var provinciaCentroMedicoBD = jsonObject.getString("provinciaCentroMedico")
                    var esHospitalCentroMedicoBD = jsonObject.getString("esHospitalCentroMedico")

                    if (idCentroMedicoBD == idCentroMedico) {
                        break //<-- salimos del bucle
                    }

                }
            } else {
                Log.e("MainActivity", "El JSONObject de centro medico está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
    }
}
