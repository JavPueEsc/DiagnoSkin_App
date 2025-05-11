package es.studium.opcion_pacientes

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
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
import es.studium.modelos_y_utiles.AdaptadorDiagnosticos
import es.studium.modelos_y_utiles.ModeloDiagnostico
import es.studium.modelos_y_utiles.RecyclerTouchListener
import es.studium.operacionesdb_diagnosticos.ConsultaRemotaDiagnosticos
import es.studium.operacionesdb_diagnosticos.EliminacionRemotaDiagnosticos
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

        //Gestion de pulsaciones sobre las tarjetas del recyclerView - Pulsación corta
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(this, recyclerView, object : RecyclerTouchListener.ClickListener {
                override fun onClick(view: View, position: Int) {
                    // pasar a la Activity de consultarDiagnostico
                    var diagnosticoSeleccionado = listaDiagnosticos[position]
                    enviarIntentAConsultar(DatosDelDiagnosticoActivity::class.java,"DatosDelDiagnosticosActivity",idPacienteRecibido,nombrePacienteRecibido,apellidosPacienteRecibido,
                        sexoPacienteRecibido,fechaNacPacienteRecibido,nuhsaPacienteRecibido,telefonoPacienteRecibido,
                        emailPacienteRecibido,dniPacienteRecibido,direccionPacienteRecibido,localidadPacienteRecibido,provinciaPacienteRecibido,codigoPostalPacienteRecibido,
                        esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido,diagnosticoSeleccionado.idDiagnostico,diagnosticoSeleccionado.fechaDiagnostico,diagnosticoSeleccionado
                            .diagnosticoDiagnostico,diagnosticoSeleccionado.gravedadDiagnostico,diagnosticoSeleccionado.fotoDiagnostico)
                }
                // Eliminación de un paciente - Pulsación larga
                override fun onLongClick(view: View, position: Int) {
                    val diagnosticoAEliminar = listaDiagnosticos[position]
                    val tituloPersonalizado = layoutInflater.inflate(R.layout.xx_titulo_dialogo_personalizado, null)
                    val dialogo = AlertDialog
                        .Builder(this@PrincipalDiagnosticosActivity)
                        .setPositiveButton(view.context.getString(R.string.PA_DialogoDiagnosticos_opcionSi), object : DialogInterface.OnClickListener {
                            override fun onClick(dialogo: DialogInterface, which: Int) {
                                if(esAdminMedicoRecibido=="0"){
                                    Toast.makeText(this@PrincipalDiagnosticosActivity, R.string.PA_toastDialogoDiagnosticos_NoEsAdmin, Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    val eliminacionRemota = EliminacionRemotaDiagnosticos()
                                    val resultado = eliminacionRemota.eliminarDiagnostico(diagnosticoAEliminar.idDiagnostico)

                                    if (resultado) {
                                        listaDiagnosticos.clear()
                                        cargarDiagnosticos(idPacienteRecibido)
                                        adaptadorDiagnosticos.notifyDataSetChanged()

                                    } else {
                                        Toast.makeText(this@PrincipalDiagnosticosActivity, R.string.PA_toastDialogoDiagnosticos_errorEliminacion, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })
                        .setNegativeButton(view.context.getString(R.string.PA_DialogoDiagnosticos_opcionNo), object : DialogInterface.OnClickListener {
                            override fun onClick(dialogo: DialogInterface, which: Int) {
                                dialogo.dismiss()
                            }
                        })
                        .setCustomTitle(tituloPersonalizado)
                        .setMessage(view.context.getString(R.string.PA_DialogoDiagnosticos_mensaje))
                        .create()

                    //El setOnShowListener permite que se apliquen los cambios en los colores cuando se muestre el dialogo
                    dialogo.setOnShowListener {
                        val fondoDialogo = ContextCompat.getDrawable(this@PrincipalDiagnosticosActivity, R.drawable.rectangulo_tarjetas)
                        val textoAzulDialogo = ContextCompat.getColor(this@PrincipalDiagnosticosActivity, R.color.azulBrillante)

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

    //Enviar intent a ConsultarActivity
    private fun enviarIntentAConsultar(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?, idDiagnostico: String?, fechaDiagnostico: String, diagnostico: String, tipoDiagnostico : String, fotoDiagnostico : ByteArray
    ) {
        val intent = Intent(this@PrincipalDiagnosticosActivity, activityDestino)
        intent.putExtra("origenRealizarDiagnosticosActivity", claveOrigen)
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
        intent.putExtra("idDiagnostico", idDiagnostico)
        intent.putExtra("fechaDiagnostico", fechaDiagnostico)
        intent.putExtra("diagnosticoDiagnostico", diagnostico)
        intent.putExtra("tipoDiagnostico", tipoDiagnostico)
        intent.putExtra("fotoDiagnostico", fotoDiagnostico)
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

    //Metodo para pasar de byteArray a String
    fun byteArrayString(byteArray: ByteArray): String {
        return String(byteArray, Charsets.UTF_8)
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