package es.studium.opcion_administrador

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.AdaptadorMedicos
import es.studium.modelos_y_utiles.AdaptadorPacientes
import es.studium.modelos_y_utiles.ModeloMedico
import es.studium.modelos_y_utiles.ModeloPaciente
import es.studium.modelos_y_utiles.RecyclerTouchListener
import es.studium.opcion_pacientes.AltaPacienteActivity
import es.studium.opcion_pacientes.DatosDelPacienteActivity
import es.studium.opcion_perfil.ModificarMedicoActivity
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_medicos.ModificacionRemotaMedicos
import es.studium.operacionesbd_pacientes.ConsultaRemotaPacientes
import es.studium.operacionesbd_pacientes.EliminacionRemotaPacientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        //Gestión de pulsaciones sobre las tarjetas del recyclerView
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(this, recyclerView, object : RecyclerTouchListener.ClickListener {
                //Consultar datos Paciente
                override fun onClick(view: View, position: Int) {
                    var pacienteSeleccionado = listaMedicos[position]

                    enviarIntentModificarmedico(ModificarPerfilAdminActivity::class.java,"PrincipalMedicosActivity",pacienteSeleccionado.idMedico, pacienteSeleccionado.nombreMedico, pacienteSeleccionado.apellidosMedico,
                        pacienteSeleccionado.telefonoMedico,pacienteSeleccionado.emailMedico, pacienteSeleccionado.especialidadMedico, pacienteSeleccionado.numColegiadoMedico, pacienteSeleccionado.esAdminMedico,
                        pacienteSeleccionado.idCentroMedicoFK, pacienteSeleccionado.idUsuarioFK, esMedicoAdminRecibido, idMedicoRecibido, idUsuarioRecibido)
                }

                override fun onLongClick(view: View, position: Int) {
                    // Eliminación de un paciente - Pulsación larga
                    val medicoAEliminar = listaMedicos[position]
                    val tituloPersonalizado = layoutInflater.inflate(R.layout.xx_titulo_dialogo_personalizado_bloqueo, null)
                    val dialogo = AlertDialog
                        .Builder(this@PrincipalMedicosActivity)
                        .setPositiveButton(view.context.getString(R.string.PA_dlg_opcionSi), object : DialogInterface.OnClickListener {
                            override fun onClick(dialogo: DialogInterface, which: Int) {
                                if(esMedicoAdminRecibido==medicoAEliminar.idMedico){
                                    Toast.makeText(this@PrincipalMedicosActivity, R.string.ADMIN_toastErrorEliminarseAsiMismo_PrincipalMedicos, Toast.LENGTH_SHORT).show()
                                    Toast.makeText(this@PrincipalMedicosActivity, R.string.ADMIN_toastConsultaOtroAdmin_ModificarPerfilAdmin, Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    // Realizar la actualización eb bbdd
                                    val modificarcionMedico = ModificacionRemotaMedicos()
                                    // Como ModificacionRemotaMedicos es suspend, se necesita una corrutina
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val resultado = modificarcionMedico.modificarMedico(
                                            medicoAEliminar.idMedico,
                                            medicoAEliminar.nombreMedico,
                                            medicoAEliminar.apellidosMedico,
                                            medicoAEliminar.telefonoMedico,
                                            medicoAEliminar.emailMedico,
                                            medicoAEliminar.especialidadMedico,
                                            medicoAEliminar.numColegiadoMedico,
                                            "2",
                                            medicoAEliminar.idCentroMedicoFK,
                                            medicoAEliminar.idUsuarioFK
                                        )
                                        //Para poder usar un toast en la rutina hay que usar este bloque
                                        runOnUiThread {
                                            if (resultado) {
                                                Toast.makeText(this@PrincipalMedicosActivity, R.string.ADMIN_toast_exitoBloqueo, Toast.LENGTH_SHORT).show()
                                                listaMedicos.clear()
                                                cargarMedicos()
                                                adaptadorMedicos.notifyDataSetChanged()
                                            } else {
                                                Toast.makeText(this@PrincipalMedicosActivity, R.string.ADMIN_toast_errorBloqueo, Toast.LENGTH_SHORT).show()
                                            }
                                        }
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
                        .setMessage(view.context.getString(R.string.ADMIN_mensajeDialogo_ModificarPerfilAdmin))
                        .create()

                    //El setOnShowListener permite que se apliquen los cambios en los colores cuando se muestre el dialogo
                    dialogo.setOnShowListener {
                        val fondoDialogo = ContextCompat.getDrawable(this@PrincipalMedicosActivity, R.drawable.rectangulo_tarjetas)
                        val textoAzulDialogo = ContextCompat.getColor(this@PrincipalMedicosActivity, R.color.azulBrillante)

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
    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        enviarIntentAMenu(idUsuarioRecibido)
    }

    //Enviar intent de vuelta
    fun enviarIntentAMenu(idUsuario:String?){
        val intent = Intent(this@PrincipalMedicosActivity, MainActivity::class.java)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Enviar intent a Activity a ModificarMedico
    private fun enviarIntentModificarmedico(
        activityDestino: Class<out Activity>, claveOrigen: String, id: String?, nombre: String?, apellidos: String?, telefono: String?, email: String?,
        especialidad: String?, numColegiado: String?, esAdmin: String?, idCentroMedicoFK: String?, idUsuarioFK: String?,
        esAdminMedicoAdmin: String?, idMedicoAdmin: String?, idUsuarioAdmin: String?
    ) {
        val intent = Intent(this@PrincipalMedicosActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("idMedico", id)
        intent.putExtra("nombreMedico", nombre)
        intent.putExtra("apellidosMedico", apellidos)
        intent.putExtra("telefonoMedico", telefono)
        intent.putExtra("emailMedico", email)
        intent.putExtra("especialidadMedico", especialidad)
        intent.putExtra("numColegiadoMedico", numColegiado)
        intent.putExtra("esAdminMedico", esAdmin)
        intent.putExtra("idCentroMedicoFK", idCentroMedicoFK)
        intent.putExtra("idUsuarioFK", idUsuarioFK)
        intent.putExtra("esAdminMedicoRecibido", esAdminMedicoAdmin)
        intent.putExtra("idMedicoRecibido", idMedicoAdmin)
        intent.putExtra("idUsuarioRecibido", idUsuarioAdmin)
        startActivity(intent)
    }
}