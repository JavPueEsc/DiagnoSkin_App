package es.studium.opcionpacientes

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_pacientes.ConsultaRemotaPacientes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PrincipalPacientesActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var txt_buscarPorNuhsa : EditText
    private lateinit var btn_NuevoPaciente : Button

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_activity_principal_pacientes)

        //Recibir EXTRA con los datos del usuario médico
        //val usuarioMedico = intent.getParcelableExtra("usuarioMedico",ModeloMedico::class.java)

        //Enlazar variables con vistas
        txt_buscarPorNuhsa = findViewById(R.id.PA_txt_filtroNuhsa)
        btn_NuevoPaciente = findViewById(R.id.PA_btn_NuevoPaciente)

        cargarPacientes()

        //Bloque para actualizar los datos cuando se producen modificaciones
        val recargar = intent.getBooleanExtra("Recargar", false)
        if (recargar) {
            listaPacientes.clear()
            cargarPacientes()
            //Hay que indicarle al adaptador que los datos han cambiado
            adaptadorPacientes.notifyDataSetChanged()
        }

        //Ponemos la lista al adaptador y configuramos el recyclerView
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.PA_recyclerView_Pacientes)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adaptadorPacientes
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
}