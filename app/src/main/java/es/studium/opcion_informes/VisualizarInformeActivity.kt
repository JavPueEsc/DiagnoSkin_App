package es.studium.opcion_informes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.ModeloDiagnostico
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesdb_diagnosticos.ConsultaRemotaDiagnosticos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class VisualizarInformeActivity : AppCompatActivity() {
    //Declarar las variables de las vistas
    private lateinit var lbl_apellidosNombrePaciente : TextView
    private lateinit var lbl_nuhsaPaciente : TextView
    private lateinit var lbl_fechaNacPaciente : TextView
    private lateinit var lbl_edadPaciente : TextView
    private lateinit var lbl_sexoPaciente : TextView
    private lateinit var lbl_localidadPaciente : TextView
    private lateinit var lbl_codigoPostal : TextView
    private lateinit var lbl_fechaDiagnostico : TextView
    private lateinit var lbl_diagnosticoDiagnostico : TextView
    private lateinit var lbl_tipoLesionDiagnostico : TextView
    private lateinit var lbl_apellidosNombreMedico : TextView
    private lateinit var lbl_especialidadMedico : TextView
    private lateinit var lbl_nombreCentroMedico : TextView
    private lateinit var btn_volver : ImageView
    private lateinit var btn_generarInforme : Button
    private lateinit var img_fotoDiagnostico : ImageView

    //Declaración de variables para recibir los extras
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

    //Variables para las consultas en la base de datos
    private lateinit var result : JSONArray
    //consulta diagnostico
    private var listaDiagnosticos: MutableList<ModeloDiagnostico> = mutableListOf()
    private lateinit var jsonObject: JSONObject
    private lateinit var idDiagnosticoBD : String
    private lateinit var fechaDiagnosticoBD : String
    private lateinit var diagnosticoDiagnosticoBD : String
    private lateinit var gravedadDiagnosticoBD : String
    private lateinit var fotoDiagnosticoBD : String
    private lateinit var idMedicoFKBD : String
    private lateinit var idPacienteFKBD : String
    //Consulta médico
    private lateinit var nombreMedicoBD : String
    private lateinit var apellidosMedicoBD : String
    private lateinit var especialidadMedicoBD : String
    private lateinit var idCentroMedicoFKBD : String
    //ConsultaCentro Medico
    private lateinit var nombreCentroMedicoBD : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inf_activity_visualizar_informe)
        //Recibir extras
        val extras = intent.extras
        if (extras != null) {
            procesarExtras(extras)
        }

        //Enlazar las vistas
        lbl_apellidosNombrePaciente = findViewById(R.id.INF_lbl_apellidosNombrePaciente_visualizarInformes)
        lbl_nuhsaPaciente = findViewById(R.id.INF_lbl_nuhsaPaciente_visualizarInformes)
        lbl_fechaNacPaciente = findViewById(R.id.INF_lbl_fechaNacPaciente_visualizarInformes)
        lbl_edadPaciente = findViewById(R.id.INF_lbl_edadPaciente_visualizarInformes)
        lbl_sexoPaciente = findViewById(R.id.INF_lbl_sexoPaciente_visualizarInformes)
        lbl_localidadPaciente = findViewById(R.id.INF_lbl_localidadPaciente_visualizarInformes)
        lbl_codigoPostal = findViewById(R.id.INF_lbl_codigoPostalPaciente_visualizarInformes)
        lbl_fechaDiagnostico = findViewById(R.id.INF_lbl_fechaDiagnostico_visualizarInformes)
        lbl_diagnosticoDiagnostico = findViewById(R.id.INF_lbl_diagnosticoDiagnostico_visualizarInformes)
        lbl_tipoLesionDiagnostico = findViewById(R.id.INF_lbl_tipoDeLesionDiagnostico_visualizarInformes)
        lbl_apellidosNombreMedico = findViewById(R.id.INF_lbl_apellidosNombreMedico_visualizarInformes)
        lbl_especialidadMedico = findViewById(R.id.INF_lbl_especialidadMedico_visualizarInformes)
        lbl_nombreCentroMedico = findViewById(R.id.INF_lbl_nombreCentro_visualizarInformes)
        btn_volver = findViewById(R.id.INF_btnVolver_visualizarInforme)
        btn_generarInforme = findViewById(R.id.INF_btn_generarInforme_visualizarInformes)
        img_fotoDiagnostico = findViewById(R.id.INF_XDIAG_fotoDiagnostico_DatosDelDiagnosticos)

        //Setear datos del paciente
        lbl_apellidosNombrePaciente.text = getString(R.string.INF_lbl_apellidosNombrePaciente_visualizarInformes,apellidosPacienteRecibido,nombrePacienteRecibido)
        lbl_nuhsaPaciente.text = getString(R.string.INF_lbl_nuhsaPaciente_visualizarInformes,nuhsaPacienteRecibido)
        lbl_fechaNacPaciente.text = getString(R.string.INF_lbl_fechaNacPaciente_visualizarInformes, fechaNacPacienteRecibido)
        var edadPaciente = calcularEdad(fechaNacPacienteRecibido)
        lbl_edadPaciente.text = getString(R.string.INF_lbl_edadPaciente_visualizarInformes, "$edadPaciente")
        lbl_sexoPaciente.text = getString(R.string.INF_lbl_sexoPaciente_visualizarInformes, sexoPacienteRecibido)
        lbl_localidadPaciente.text = getString(R.string.INF_lbl_localidadPaciente_visualizarInformes, localidadPacienteRecibido, provinciaPacienteRecibido)
        lbl_codigoPostal.text = getString(R.string.INF_lbl_codigoPostalPaciente_visualizarInformes, codigoPostalPacienteRecibido)
        //consulta de diagnostico
        cargarDatosDiagnostico(idPacienteRecibido)
        lbl_fechaDiagnostico.text = getString(R.string.INF_lbl_fechaDiagnostico_visualizarInformes, fechaDiagnosticoBD)
        lbl_diagnosticoDiagnostico.text = getString(R.string.INF_lbl_diagnosticoDiagnostico_visualizarInformes,diagnosticoDiagnosticoBD)
        lbl_tipoLesionDiagnostico.text = getString(R.string.INF_lbl_tipoDeLesionDiagnostico_visualizarInformes,gravedadDiagnosticoBD)
        img_fotoDiagnostico.setImageBitmap(byteArrayABitmap(base64AByteArray(fotoDiagnosticoBD)))
        cargarDatosMedico(idMedicoRecibido)
        lbl_apellidosNombreMedico.text = getString(R.string.INF_lbl_apellidosNombreMedico_visualizarInformes,apellidosMedicoBD, nombreMedicoBD)
        lbl_especialidadMedico.text = getString(R.string.INF_lbl_especialidadMedico_visualizarInformes, especialidadMedicoBD)
        //Consulta centro médico
        cargarDatosCentroMedico(idCentroMedicoFKBD)
        lbl_nombreCentroMedico.text = getString(R.string.INF_lbl_NombreCentro_visualizarInformes,nombreCentroMedicoBD)
    }

    //Médodo para recibir los extras independientemente del Activity del que provengan
    private fun procesarExtras(extras: Bundle) {
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
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuario")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idDiagnosticoRecibido = extras.getString("idDiagnostico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    fun calcularEdad(fechaNacimientoStr: String?): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fechaNacimiento = LocalDate.parse(fechaNacimientoStr, formatter)
        val fechaActual = LocalDate.now()
        val edad = Period.between(fechaNacimiento, fechaActual).years
        return edad
    }

    fun cargarDatosDiagnostico(idPaciente : String?){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaDiagnosticos = ConsultaRemotaDiagnosticos()
        result = consultaRemotaDiagnosticos.obtenerDiagnosticoPorIdPacienteFK(idPaciente)
        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idDiagnosticoBD = jsonObject.getString("idDiagnostico")
                    fechaDiagnosticoBD = jsonObject.getString("fechaDiagnostico")
                    diagnosticoDiagnosticoBD = jsonObject.getString("diagnosticoDiagnostico")
                    gravedadDiagnosticoBD = jsonObject.getString("gravedadDiagnostico")
                    fotoDiagnosticoBD = jsonObject.getString("fotoDiagnostico")
                    idMedicoFKBD = jsonObject.getString("idMedicoFK")
                    idPacienteFKBD = jsonObject.getString("idPacienteFK")

                    if(idPacienteFKBD==idPaciente){
                        break
                    }
                }
            }
            else{
                Log.e("VisualizarInformeActivity_consultaDiagnostico", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("VisualizarInformeActivity_consultaDiagnostico", "Error al procesar el JSON", e)
        }
    }
    fun cargarDatosMedico(idMedico: String?) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorId(idMedico)
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    var idMedicoBD = jsonObject.getString("idMedico")
                    nombreMedicoBD = jsonObject.getString("nombreMedico")
                    apellidosMedicoBD = jsonObject.getString("apellidosMedico")
                    especialidadMedicoBD = jsonObject.getString("especialidadMedico")
                    idCentroMedicoFKBD = jsonObject.getString("idCentroMedicoFK")
                    if (idMedicoBD == idMedico) {
                        break //<-- salimos del bucle
                    }
                }
            } else {
                Log.e("VisualizarInformeActivity_consultaMedico", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("VisualizarInformeActivity_consultaMedico", "Error al procesar el JSON", e)
        }
    }
    fun cargarDatosCentroMedico(idCentroMedico: String?) {
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
    //Metodo para pasar de StringBase64 a ByteArray
    fun base64AByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
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

    //Metodo para pasar fechas MySQL a Europeo
    fun fechaMysqlAEuropea(fecha: String): String {
        lateinit var fechaTransformada: String
        var elementosFecha = fecha.split("-")
        if (elementosFecha.size == 3) {
            fechaTransformada = "${elementosFecha[2]}/${elementosFecha[1]}/${elementosFecha[0]}"
        } else {
            fechaTransformada = getString(R.string.PA_error_ModificarFecha)
        }
        return fechaTransformada
    }
}