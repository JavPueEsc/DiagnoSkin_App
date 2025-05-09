package es.studium.opcionpacientes

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloMedico
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.modelos_y_utiles.RecyclerTouchListener
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_pacientes.ConsultaRemotaPacientes
import es.studium.operacionesbd_pacientes.EliminacionRemotaPacientes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PrincipalPacientesActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var txt_buscarPorNuhsa : EditText
    private lateinit var btn_NuevoPaciente : Button
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

    //Variable para extra recibido
    private lateinit var esMedicoAdminRecibido : String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_activity_principal_pacientes)

        //Recibir EXTRA con los datos del usuario médico
        val extras = intent.extras
        if (extras != null) {
             esMedicoAdminRecibido = extras.getString("esAdminMedico")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
        }

        //Enlazar variables con vistas
        txt_buscarPorNuhsa = findViewById(R.id.PA_txt_filtroNuhsa)
        btn_NuevoPaciente = findViewById(R.id.PA_btn_NuevoPaciente)
        btn_volver = findViewById(R.id.btnVolver_PrincipalPacientesActivity)

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        txt_buscarPorNuhsa.addTextChangedListener(object : TextWatcher{
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
        recyclerView = findViewById(R.id.PA_recyclerView_Pacientes)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adaptadorPacientes


        //Gestión de pulsaciones sobre las tarjetas del recyclerView
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(this, recyclerView, object : RecyclerTouchListener.ClickListener {
                //Consultar datos Paciente
                override fun onClick(view: View, position: Int) {
                    var pacienteSeleccionado = listaPacientes[position]
                    var intentConsultar = Intent(this@PrincipalPacientesActivity,DatosDelPacienteActivity::class.java)
                    intentConsultar.putExtra("origenPrincipalPacienteActivity", "PrincipalPacienteActivity")
                    intentConsultar.putExtra("idPaciente", pacienteSeleccionado.idPaciente)
                    intentConsultar.putExtra("nombrePaciente", pacienteSeleccionado.nombrePaciente)
                    intentConsultar.putExtra("apellidosPaciente", pacienteSeleccionado.apellidosPaciente)
                    intentConsultar.putExtra("sexoPaciente", pacienteSeleccionado.sexoPaciente)
                    intentConsultar.putExtra("fechaNacPaciente", pacienteSeleccionado.fechaNacPaciente)
                    intentConsultar.putExtra("nuhsaPaciente", pacienteSeleccionado.nuhsaPaciente)
                    intentConsultar.putExtra("telefonoPaciente", pacienteSeleccionado.telefonoPaciente)
                    intentConsultar.putExtra("emailPaciente", pacienteSeleccionado.emailPaciente)
                    intentConsultar.putExtra("dniPaciente", pacienteSeleccionado.dniPaciente)
                    intentConsultar.putExtra("direccionPaciente", pacienteSeleccionado.direccionPaciente)
                    intentConsultar.putExtra("localidadPaciente", pacienteSeleccionado.localidadPaciente)
                    intentConsultar.putExtra("provinciaPaciente", pacienteSeleccionado.provinciaPaciente)
                    intentConsultar.putExtra("codigoPostalPaciente", pacienteSeleccionado.codigoPostalPaciente)
                    intentConsultar.putExtra("esAdminMedico", esMedicoAdminRecibido)

                    startActivity(intentConsultar)
                    //Toast.makeText(view.context,"Pulsación corta",Toast.LENGTH_SHORT).show()
                }

                override fun onLongClick(view: View, position: Int) {
                        // Eliminación de un diagnostico - Pulsación larga
                        val pacienteAEliminar = listaPacientes[position]
                        val tituloPersonalizado = layoutInflater.inflate(R.layout.xx_titulo_dialogo_personalizado, null)
                        val dialogo = AlertDialog
                            .Builder(this@PrincipalPacientesActivity)
                            .setPositiveButton(view.context.getString(R.string.PA_dlg_opcionSi), object : DialogInterface.OnClickListener {
                                override fun onClick(dialogo: DialogInterface, which: Int) {
                                    if(esMedicoAdminRecibido=="0"){
                                        Toast.makeText(this@PrincipalPacientesActivity, R.string.PA_toast_NoEsAdmin, Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        val eliminacionRemota = EliminacionRemotaPacientes()
                                        val resultado = eliminacionRemota.eliminarPaciente(pacienteAEliminar.idPaciente)

                                        if (resultado) {
                                            listaPacientes.clear()
                                            cargarPacientes()
                                            adaptadorPacientes.notifyDataSetChanged()

                                        } else {
                                            Toast.makeText(this@PrincipalPacientesActivity, R.string.PA_toast_errorEliminacion, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(view.context.getString(R.string.PA_dlg_opcionNo), object : DialogInterface.OnClickListener {
                                override fun onClick(dialogo: DialogInterface, which: Int) {
                                    dialogo.dismiss()
                                }
                            })
                            .setCustomTitle(tituloPersonalizado)
                            .setMessage(view.context.getString(R.string.PA_dlg_mensaje))
                            .create()

                    //El setOnShowListener permite que se apliquen los cambios en los colores cuando se muestre el dialogo
                    dialogo.setOnShowListener {
                        val fondoDialogo = ContextCompat.getDrawable(this@PrincipalPacientesActivity, R.drawable.rectangulo_tarjetas)
                        val textoAzulDialogo = ContextCompat.getColor(this@PrincipalPacientesActivity, R.color.azulBrillante)

                        dialogo.window?.setBackgroundDrawable(fondoDialogo)

                        dialogo.findViewById<TextView>(android.R.id.message)?.setTextColor(textoAzulDialogo)
                        dialogo.findViewById<TextView>(resources.getIdentifier("alertTitle", "id", "android"))?.setTextColor(textoAzulDialogo)

                        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textoAzulDialogo)
                        dialogo.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textoAzulDialogo)
                    }

                    dialogo.show()
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
}