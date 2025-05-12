package es.studium.opcion_pacientes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.studium.diagnoskin_app.R
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DatosDelDiagnosticoActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_idDiagnostico : TextView
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
    private var idPacienteRecibido: String? = null
    private var nombrePacienteRecibido: String? = null
    private var apellidosPacienteRecibido: String? = null
    private var sexoPacienteRecibido: String? = null
    private var fechaNacPacienteRecibido: String? = null
    private var nuhsaPacienteRecibido: String? = null
    private var telefonoPacienteRecibido: String? = null
    private var emailPacienteRecibido: String? = null
    private var dniPacienteRecibido: String? = null
    private var direccionPacienteRecibido: String? = null
    private var localidadPacienteRecibido: String? = null
    private var provinciaPacienteRecibido: String? = null
    private var codigoPostalPacienteRecibido: String? = null
    private var esAdminMedicoRecibido: String? = null
    private var idMedicoRecibido: String? = null
    private var idUsuarioRecibido: String? = null
    private var idDiagnosticoRecibido: String? = null
    private var fechaDiagnosticoRecibida: String? = null
    private var diagnosticoRecibido: String? = null
    private var tipoDiagnosticoRecibido: String? = null
    private var fotoDiagnosticoRecibida: ByteArray? = null

    //Declaración de las variables para extraer medico y nombre del centroMedico de bbdd
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var nombreMedicoBD : String
    private lateinit var apellidosMedicoBD : String
    private lateinit var idCentroMedicoFKBD : String
    private lateinit var nombreCentroMedicoBD : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_xdiag_activity_datos_del_diagnostico)
        //Recibir extras
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
            idDiagnosticoRecibido = extras.getString("idDiagnostico")
            fechaDiagnosticoRecibida = extras.getString("fechaDiagnostico")
            diagnosticoRecibido = extras.getString("diagnosticoDiagnostico")
            tipoDiagnosticoRecibido = extras.getString("tipoDiagnostico")
            fotoDiagnosticoRecibida = extras.getByteArray("fotoDiagnostico")
        }

        //Enlazar vistas
        lbl_idDiagnostico = findViewById(R.id.PA_XDIA_lbl_titulo_DatosDelDiagnostico)
        lbl_apellidosNombre = findViewById(R.id.PA_XDIAG_lbl_apellidosNombre_DatosDelDiagnostico)
        lbl_fecha = findViewById(R.id.PA_XDIAG_lbl_fecha_DatosDelDiagnostico)
        img_fotoDiagnostico = findViewById(R.id.PA_XDIAG_fotoDiagnostico_DatosDelDiagnosticos)
        lbl_diagnosticoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_diagnostico_DatosDelDiagnostico)
        lbl_tipoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_tipo_DatosDelDiagnostico)
        lbl_medicoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_medico_DatosDelDiagnostico)
        lbl_centroMedicoDiagnostico = findViewById(R.id.PA_XDIAG_lbl_centro_DatosDelDiagnostico)
        btn_volver = findViewById(R.id.btnVolver_DatosDelDiagnostico)

        //Gestión del botón volver
        btn_volver.setOnClickListener {
            enviarIntentVuelta(
                PrincipalDiagnosticosActivity::class.java,
                "DatosDelDiagnosticoActivity", idPacienteRecibido,
                nombrePacienteRecibido, apellidosPacienteRecibido, sexoPacienteRecibido, fechaNacPacienteRecibido, nuhsaPacienteRecibido, telefonoPacienteRecibido,
                emailPacienteRecibido, dniPacienteRecibido, direccionPacienteRecibido, localidadPacienteRecibido, provinciaPacienteRecibido, codigoPostalPacienteRecibido,
                esAdminMedicoRecibido, idMedicoRecibido, idUsuarioRecibido
            )
        }

        //Establecemos la información en las etiquetas correspondientes
        lbl_idDiagnostico.text = getString(R.string.PA_XDIA_lbl_titulo_DatosDelDiagnosticos,idDiagnosticoRecibido)
        lbl_apellidosNombre.text = getString(R.string.PA_XDIA_lbl_apellidosNombre_ResumenDiagnosticos,apellidosPacienteRecibido,nombrePacienteRecibido)
        lbl_fecha.text = getString(R.string.PA_XDIA_lbl_fecha_ResumenDiagnosticos,fechaDiagnosticoRecibida)
        if (fotoDiagnosticoRecibida != null) {
            val fotoBitmap = byteArrayABitmap(fotoDiagnosticoRecibida)
            img_fotoDiagnostico.setImageBitmap(fotoBitmap)
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
    }


    fun byteArrayABitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if (bitmap != null) {
                Log.d("CONVERSION_IMAGEN", "Conversión a Bitmap exitosa. Tamaño: ${byteArray.size} bytes")
            } else {
                Log.e("CONVERSION_IMAGEN", "Fallo en la conversión a Bitmap. ByteArray no válido.")
            }
            bitmap
        } else {
            Log.w("CONVERSION_IMAGEN", "ByteArray es null. No se puede convertir.")
            null
        }
    }

    //Enviar intent de vuelta (a RealizarDiagnosticosActivity)
    private fun enviarIntentVuelta(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?,
        sexo: String?, fechaNac: String?, nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?,
        localidad: String?, provincia: String?, codigoPostal: String?, esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@DatosDelDiagnosticoActivity, activityDestino::class.java)
        intent.putExtra("origenDatosDelDiagnosticoActivity", claveOrigen)
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
                    idCentroMedicoFKBD = jsonObject.getString("idCentroMedicoFK")
                    if (idMedicoBD == idMedico) {

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

    private fun enviarIntentSiguiente(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@DatosDelDiagnosticoActivity, activityDestino)
        intent.putExtra("DatosDelDiagnosticoActivity", claveOrigen)
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
}