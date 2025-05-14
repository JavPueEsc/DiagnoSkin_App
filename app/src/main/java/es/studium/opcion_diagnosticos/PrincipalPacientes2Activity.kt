package es.studium.opcion_diagnosticos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.modelos_y_utiles.RecyclerTouchListener
import es.studium.opcion_informes.BuscadorInformesActivity
import es.studium.opcion_pacientes.RealizarDiagnosticoActivity
import es.studium.operacionesbd_pacientes.ConsultaRemotaPacientes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PrincipalPacientes2Activity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var txt_buscarPorNuhsa : EditText
    private lateinit var btn_volver : ImageView

    //Variables para mostrar las tarjetas
    private var listaPacientes: MutableList<ModeloPaciente> = mutableListOf()
    val adaptadorPacientes = AdaptadorPacientes(listaPacientes)
    private lateinit var recyclerView: RecyclerView

    //Variablespara la consulta de los pacientes en la base de datos.
    private lateinit var result : JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var idPacienteBD : String
    private lateinit var nombrePacienteBD : String
    private lateinit var apellidosPacienteBD : String
    private lateinit var sexoPacienteBD : String
    private lateinit var fechaNacPacienteBD : String
    private lateinit var nuhsaPacienteBD : String
    private lateinit var telefonoPacienteBD : String
    private lateinit var emailPacienteBD : String
    private lateinit var dniPacienteBD : String
    private lateinit var direccionPacienteBD : String
    private lateinit var localidadPacienteBD : String
    private lateinit var provinciaPacienteBD : String
    private lateinit var codigoPostalPacienteBD : String

    //Variable para extras recibidos
    private lateinit var esMedicoAdminRecibido : String
    private lateinit var idMedicoRecibido : String
    private lateinit var idUsuarioRecibido : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diag_activity_principal_pacientes2)

        //Recibir EXTRA con los datos del usuario médico
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("OrigenBtnDiagnosticos")) {
                procesarExtras(extras)
            } else if (extras.containsKey("OrigenBtnInformes")){
                procesarExtras(extras)
            }
        }

        //Enlazar variables con vistas
        txt_buscarPorNuhsa = findViewById(R.id.PA_txt_filtroNuhsa2)
        btn_volver = findViewById(R.id.btnVolver_PrincipalPacientesActivity2)
        btn_volver.visibility = View.GONE

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            enviarIntentAMenu(idUsuarioRecibido)
        }

        cargarPacientes()

        //Bloque para actualizar los datos cuando se producen modificaciones
        val recargar = intent.getBooleanExtra("Recargar", false)
        if (recargar) {
            listaPacientes.clear()
            cargarPacientes()
            //Hay que indicarle al adaptador que los datos han cambiado
            adaptadorPacientes.notifyDataSetChanged()
        }

        //Gestión de búsqueda dinámica por nuhsa
        txt_buscarPorNuhsa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nuhsaIntroducido = s.toString()
                listaPacientes.clear()
                cargarPacientesPorNuhsa(nuhsaIntroducido)
                //Hay que indicarle al adaptador que los datos han cambiado
                adaptadorPacientes.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        //Ponemos la lista al adaptador y configuramos el recyclerView
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.PA_recyclerView_Pacientes2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adaptadorPacientes

        //Gestión de pulsaciones sobre las tarjetas del recyclerView
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(this, recyclerView, object : RecyclerTouchListener.ClickListener {
                //Consultar datos Paciente
                override fun onClick(view: View, position: Int) {
                    var pacienteSeleccionado = listaPacientes[position]


                    if (extras != null) {
                        if (extras.containsKey("OrigenBtnDiagnosticos")) {
                            enviarIntentSiguiente(
                                RealizarDiagnosticoActivity::class.java,"PrincipalPacientes2Activity", pacienteSeleccionado.idPaciente, pacienteSeleccionado.nombrePaciente,
                                pacienteSeleccionado.apellidosPaciente, pacienteSeleccionado.sexoPaciente, pacienteSeleccionado.fechaNacPaciente, pacienteSeleccionado.nuhsaPaciente, pacienteSeleccionado.telefonoPaciente,
                                pacienteSeleccionado.emailPaciente, pacienteSeleccionado.dniPaciente, pacienteSeleccionado.direccionPaciente, pacienteSeleccionado.localidadPaciente, pacienteSeleccionado.provinciaPaciente,
                                pacienteSeleccionado.codigoPostalPaciente, esMedicoAdminRecibido, idMedicoRecibido, idUsuarioRecibido)
                        } else if (extras.containsKey("OrigenBtnInformes")){
                            //Toast.makeText(this@PrincipalPacientes2Activity,"Paciente Seleccionado: ${pacienteSeleccionado.idPaciente}, ${pacienteSeleccionado.nombrePaciente}",Toast.LENGTH_SHORT).show()
                            enviarIntentSiguiente(
                                BuscadorInformesActivity::class.java,"PrincipalPacientes2Activity", pacienteSeleccionado.idPaciente, pacienteSeleccionado.nombrePaciente,
                                pacienteSeleccionado.apellidosPaciente, pacienteSeleccionado.sexoPaciente, pacienteSeleccionado.fechaNacPaciente, pacienteSeleccionado.nuhsaPaciente, pacienteSeleccionado.telefonoPaciente,
                                pacienteSeleccionado.emailPaciente, pacienteSeleccionado.dniPaciente, pacienteSeleccionado.direccionPaciente, pacienteSeleccionado.localidadPaciente, pacienteSeleccionado.provinciaPaciente,
                                pacienteSeleccionado.codigoPostalPaciente, esMedicoAdminRecibido, idMedicoRecibido, idUsuarioRecibido)
                        }
                    }


                }

                override fun onLongClick(view: View, position: Int) {
                    // No admite pulsación larga
                }
            })
        )
    }

    fun cargarPacientes(){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaPacientes = ConsultaRemotaPacientes()
        result = consultaRemotaPacientes.obtenerListado() //<-------AQUÍ HAY QUE LLAMAR A CARGAR PACIENTES!!!
        //verificamos que result no está vacio
        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idPacienteBD = jsonObject.getString("idPaciente")
                    nombrePacienteBD = jsonObject.getString("nombrePaciente")
                    apellidosPacienteBD = jsonObject.getString("apellidosPaciente")
                    sexoPacienteBD = jsonObject.getString("sexoPaciente")
                    fechaNacPacienteBD = jsonObject.getString("fechaNacPaciente")
                    nuhsaPacienteBD = jsonObject.getString("nuhsaPaciente")
                    telefonoPacienteBD = jsonObject.getString("telefonoPaciente")
                    emailPacienteBD = jsonObject.getString("emailPaciente")
                    dniPacienteBD = jsonObject.getString("dniPaciente")
                    direccionPacienteBD = jsonObject.getString("direccionPaciente")
                    localidadPacienteBD = jsonObject.getString("localidadPaciente")
                    provinciaPacienteBD = jsonObject.getString("provinciaPaciente")
                    codigoPostalPacienteBD = jsonObject.getString("codigoPostalPaciente")

                    listaPacientes.add(ModeloPaciente(idPacienteBD,nombrePacienteBD,apellidosPacienteBD,sexoPacienteBD,
                        fechaNacPacienteBD,nuhsaPacienteBD,telefonoPacienteBD,emailPacienteBD,dniPacienteBD,direccionPacienteBD,
                        localidadPacienteBD,provinciaPacienteBD,codigoPostalPacienteBD))
                }
            }
            else{
                Log.e("PrincipalPacientesActivity", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("PrincipalPacientesActivity", "Error al procesar el JSON", e)
        }
    }

    fun cargarPacientesPorNuhsa(nuhsa:String){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaPacientes = ConsultaRemotaPacientes()
        result = consultaRemotaPacientes.obtenerPacientePorNuhsa(nuhsa)
        //verificamos que result no está vacio
        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idPacienteBD = jsonObject.getString("idPaciente")
                    nombrePacienteBD = jsonObject.getString("nombrePaciente")
                    apellidosPacienteBD = jsonObject.getString("apellidosPaciente")
                    sexoPacienteBD = jsonObject.getString("sexoPaciente")
                    fechaNacPacienteBD = jsonObject.getString("fechaNacPaciente")
                    nuhsaPacienteBD = jsonObject.getString("nuhsaPaciente")
                    telefonoPacienteBD = jsonObject.getString("telefonoPaciente")
                    emailPacienteBD = jsonObject.getString("emailPaciente")
                    dniPacienteBD = jsonObject.getString("dniPaciente")
                    direccionPacienteBD = jsonObject.getString("direccionPaciente")
                    localidadPacienteBD = jsonObject.getString("localidadPaciente")
                    provinciaPacienteBD = jsonObject.getString("provinciaPaciente")
                    codigoPostalPacienteBD = jsonObject.getString("codigoPostalPaciente")

                    listaPacientes.add(ModeloPaciente(idPacienteBD,nombrePacienteBD,apellidosPacienteBD,sexoPacienteBD,
                        fechaNacPacienteBD,nuhsaPacienteBD,telefonoPacienteBD,emailPacienteBD,dniPacienteBD,direccionPacienteBD,
                        localidadPacienteBD,provinciaPacienteBD,codigoPostalPacienteBD))
                }
            }
            else{
                Log.e("PrincipalPacientesActivity", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("PrincipalPacientesActivity", "Error al procesar el JSON", e)
        }
    }

    //Enviar intent de vuelta
    fun enviarIntentAMenu(idUsuario:String?){
        val intent = Intent(this@PrincipalPacientes2Activity, MainActivity::class.java)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Enviar intent a Activity Realizar Diagnostico
    private fun enviarIntentSiguiente(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@PrincipalPacientes2Activity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
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

    //Médodo para recibir los extras independientemente del Activity del que provengan
    private fun procesarExtras(extras: Bundle) {
        esMedicoAdminRecibido = extras.getString("esAdminMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuario")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }
}