package es.studium.opcion_administrador

import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorMedicos
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloMedico
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_pacientes.ConsultaRemotaPacientes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PrincipalMedicosActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var txt_buscarPorNumColegiado : EditText

    //Variables para mostrar las tarjetas
    private var listaMedicos: MutableList<ModeloMedico> = mutableListOf()
    val adaptadorMedicos = AdaptadorMedicos(listaMedicos)
    private lateinit var recyclerView: RecyclerView

    //Variablespara la consulta de los pacientes en la base de datos.
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

    //Variable para extra recibido
    private lateinit var esMedicoAdminRecibido : String
    private lateinit var idMedicoRecibido : String
    private lateinit var idUsuarioRecibido : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_principal_medicos)

        //Recibir EXTRAs con los datos del usuario médico
        val extras = intent.extras
        if (extras != null) {
            procesarExtras(extras)
        }
        //Enlazar variables con vistas
        txt_buscarPorNumColegiado = findViewById(R.id.ADMIN_txt_filtro_PrincipalMedicos)

        cargarMedicos()

        //Bloque para actualizar los datos cuando se producen modificaciones
        val recargar = intent.getBooleanExtra("Recargar", false)
        if (recargar) {
            listaMedicos.clear()
            cargarMedicos()
            //Hay que indicarle al adaptador que los datos han cambiado
            adaptadorMedicos.notifyDataSetChanged()
        }

        //Gestión de búsqueda dinámica por nuhsa
        txt_buscarPorNumColegiado.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val numColIntroducido = s.toString()
                listaMedicos.clear()
                cargarMedicosPorNumCol(numColIntroducido)
                //Hay que indicarle al adaptador que los datos han cambiado
                adaptadorMedicos.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        //Ponemos la lista al adaptador y configuramos el recyclerView
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.ADMIN_recyclerView_PrincipalMedicos)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adaptadorMedicos

        //IMPLEMENTAR LA PULSACIÓN SOBRE LAS TARJETAS <--------------

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

    //Cargar médicos
    fun cargarMedicos(){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerListado()
        //verificamos que result no está vacio
        try{
            if(result.length() > 0){
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

                    listaMedicos.add(ModeloMedico(idMedicoBD,nombreMedicoBD,apellidosMedicoBD,telefonoMedicoBD,
                        emailMedicoBD,especialidadMedicoBD,numColegiadoMedicoBD,esAdminMedicoBD,idCentroMedicoFKBD,idUsuarioFKBD))
                }
            }
            else{
                Log.e("PrincipalMedicosActivity", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("PrincipalMedicosActivity", "Error al procesar el JSON", e)
        }
    }

    fun cargarMedicosPorNumCol(numCol : String){
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorNumCol(numCol)
        //verificamos que result no está vacio
        try{
            if(result.length() > 0){
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

                    listaMedicos.add(ModeloMedico(idMedicoBD,nombreMedicoBD,apellidosMedicoBD,telefonoMedicoBD,
                        emailMedicoBD,especialidadMedicoBD,numColegiadoMedicoBD,esAdminMedicoBD,idCentroMedicoFKBD,idUsuarioFKBD))
                }
            }
            else{
                Log.e("PrincipalMedicosActivity", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("PrincipalMedicosActivity", "Error al procesar el JSON", e)
        }
    }
}