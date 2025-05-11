package es.studium.opcionpacientes

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorDiagnosticos
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloDiagnostico
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.operacionesdb_diagnosticos.ConsultaRemotaDiagnosticos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PrincipalDiagnosticosActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_nombreApellidos : TextView
    private lateinit var btn_NuevoDiagnostico : Button
    private lateinit var btn_volver : ImageView

    //Variables para mostrar las tarjetas
    private var listaDiagnosticos: MutableList<ModeloDiagnostico> = mutableListOf()
    val adaptadorDiagnosticos = AdaptadorDiagnosticos(listaDiagnosticos)
    private lateinit var recyclerView: RecyclerView

    //Variablespara la consulta de los diagnosticos en la base de datos.
    private lateinit var result : JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var idDiagnosticoBD : String
    private lateinit var fechaDiagnosticoBD : String
    private lateinit var diagnosticoDiagnosticoBD : String
    private lateinit var gravedadDiagnosticoBD : String
    private lateinit var fotoDiagnosticoBD_stringBase64 : String
    private var fotoDiagnosticoBD = byteArrayOf()
    private lateinit var idMedicoFKBD : String
    private lateinit var idPacienteFKBD : String

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_xdiag_activity_principal_diagnosticos)
        //Recibir EXTRA con los datos del usuario médico y paciente
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
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
            idMedicoRecibido = extras.getString("idMedico")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
            idUsuarioRecibido = extras.getString("idUsuario")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
        }

        //Enlazar variables con vistas
        lbl_nombreApellidos = findViewById(R.id.PA_XDIAG_lbl_nombreApellidosPaciente_PrincipalDiagnosticos)
        lbl_nombreApellidos.setText("${apellidosPacienteRecibido}, ${nombrePacienteRecibido}")
        btn_NuevoDiagnostico = findViewById(R.id.PA_XDIAG_btn_NuevoDiagnostico_PrincipalDiagnosticos)
        btn_volver = findViewById(R.id.btnVolver_PrincipalPrincipalDiagnosticos)

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            enviarIntent(DatosDelPacienteActivity::class.java,"principalDiagnosticosActivity",idPacienteRecibido,nombrePacienteRecibido,apellidosPacienteRecibido,
                sexoPacienteRecibido,fechaNacPacienteRecibido,nuhsaPacienteRecibido,telefonoPacienteRecibido,
                emailPacienteRecibido,dniPacienteRecibido,direccionPacienteRecibido,localidadPacienteRecibido,provinciaPacienteRecibido,codigoPostalPacienteRecibido,
                esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
        }

        cargarDiagnosticos(idPacienteRecibido)

        //<------------------------------------------------------ FALTA GESTIÓN DE PULSACION TARJETAS

        //Bloque para actualizar los datos cuando se producen modificaciones
        val recargar = intent.getBooleanExtra("Recargar", false)
        if (recargar) {
            listaDiagnosticos.clear()
            cargarDiagnosticos(idPacienteRecibido)
            //Hay que indicarle al adaptador que los datos han cambiado
            adaptadorDiagnosticos.notifyDataSetChanged()
        }

        //Ponemos la lista al adaptador y configuramos el recyclerView
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.PA_XDIAG_recyclerView_diagnosticos)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adaptadorDiagnosticos

        //Gestión del botón Nuevo diagnóstico
        btn_NuevoDiagnostico.setOnClickListener {
            enviarIntent(RealizarDiagnosticoActivity::class.java,"principalDiagnosticosActivity",idPacienteRecibido,nombrePacienteRecibido,apellidosPacienteRecibido,
                sexoPacienteRecibido,fechaNacPacienteRecibido,nuhsaPacienteRecibido,telefonoPacienteRecibido,
                emailPacienteRecibido,dniPacienteRecibido,direccionPacienteRecibido,localidadPacienteRecibido,provinciaPacienteRecibido,codigoPostalPacienteRecibido,
                esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
        }
    }

    //Enviar intent
    private fun enviarIntent(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@PrincipalDiagnosticosActivity, activityDestino)
        intent.putExtra("origenPrincipalDiagnosticosActivity", claveOrigen)
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

    fun cargarDiagnosticos(idPacienteFK : String?){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaDiagnosticos = ConsultaRemotaDiagnosticos()
        result = consultaRemotaDiagnosticos.obtenerDiagnosticoPorIdPacienteFK(idPacienteFK)
        //verificamos que result no está vacio
        listaDiagnosticos.clear()
        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idDiagnosticoBD = jsonObject.getString("idDiagnostico")
                    fechaDiagnosticoBD = jsonObject.getString("fechaDiagnostico")
                    diagnosticoDiagnosticoBD = jsonObject.getString("diagnosticoDiagnostico")
                    gravedadDiagnosticoBD = jsonObject.getString("gravedadDiagnostico")
                    fotoDiagnosticoBD_stringBase64 = jsonObject.getString("fotoDiagnostico")
                    //pasar el string de imagen a bitArray
                    fotoDiagnosticoBD = base64AByteArray(fotoDiagnosticoBD_stringBase64)
                    idMedicoFKBD = jsonObject.getString("idMedicoFK")
                    idPacienteFKBD = jsonObject.getString("idPacienteFK")

                    if(idPacienteFKBD==idPacienteFK){
                        listaDiagnosticos.add(ModeloDiagnostico(idDiagnosticoBD,fechaMysqlAEuropea(fechaDiagnosticoBD),diagnosticoDiagnosticoBD,gravedadDiagnosticoBD,
                            fotoDiagnosticoBD,idMedicoFKBD,idPacienteFKBD))
                    }
                }
            }
            else{
                Log.e("PrincipalDiagnosticoActivity_consultaDiahnosticos", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("PrincipalDiagnosticoActivity_consultaDiagnosticos", "Error al procesar el JSON", e)
        }
    }

    //Metodo para pasar de StringBase64 a ByteArray
    fun base64AByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
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