package es.studium.opcion_perfil

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.InterfazMedico
import es.studium.modelos_y_utiles.InterfazUsuario
import es.studium.modelos_y_utiles.ValidacionMedicosUsuarios
import es.studium.modelos_y_utiles.ValidacionMedicosUsuariosBBDD
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ConsultaRemotaMedicos
import es.studium.operacionesbd_medicos.ModificacionRemotaMedicos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ModificarMedicoActivity : AppCompatActivity(), InterfazUsuario, InterfazMedico {
    //Declaración de las vistas
    private lateinit var lbl_idMedico: TextView
    private lateinit var txt_nombreMedico: EditText
    private lateinit var txt_apellidosMedico: EditText
    private lateinit var txt_numColegiadoMedico: EditText
    private lateinit var spinner_especialidadMedico: AppCompatSpinner
    private lateinit var spinner_centroTrabajoMedico: AppCompatSpinner
    private lateinit var txt_telefonomedico: EditText
    private lateinit var txt_emailMedico: EditText
    private lateinit var btn_aceptar: Button
    private lateinit var btn_volver: ImageView

    //Declaración de extras recibidos
    private lateinit var idMedicoRecibido : String
    private lateinit var nombreMedicoRecibido : String
    private lateinit var apellidosMedicoRecibido : String
    private lateinit var telefonoMedicoRecibido : String
    private lateinit var emailMedicoRecibido : String
    private lateinit var especialidadMedicoRecibido : String
    private lateinit var numColegiadoMedicoRecibido : String
    private lateinit var esAdminMedicoRecibido : String
    private lateinit var nombreCentroMedicoRecibido : String
    private lateinit var idUsuarioRecibido : String

    //Declaración objetos consulta tabla centros médicos
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var idCentroMedicoBD: String
    private lateinit var nombreCentroMedicoBD: String
    private lateinit var telefonoCentroMedicoBD: String
    private lateinit var latitudCentroMedicoBD: String
    private lateinit var longitudCentroMedicoBD: String
    private lateinit var direccionCentroMedicoBD: String
    private lateinit var localidadCentroMedicoBD: String
    private lateinit var codigoPostalCentroMedicoBD: String
    private lateinit var provinciaCentroMedicoBD: String
    private lateinit var esHospitalCentroMedicoBD: String

    //Variable para acumular los resultados de centros médicos para montar el spinner
    private var listaCentrosMedicosSpinner: MutableList<String> = mutableListOf()
    private lateinit var adaptadorSpinner: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.per_activity_modificar_medico)
        //Recepción de los extras del Activity anterior con datos de paciente y médico
        val extras = intent.extras
        if(extras!=null){
            procesarExtras(extras)
        }

        //Enlazar las vistas
        lbl_idMedico = findViewById(R.id.PER_titulo_modificarMedico)
        txt_nombreMedico= findViewById(R.id.PER_txt_nombre_modificarMedico)
        txt_apellidosMedico= findViewById(R.id.PER_txt_apellidos_modificarMedico)
        txt_numColegiadoMedico= findViewById(R.id.PER_txt_numColegiado_modificarMedico)
        spinner_especialidadMedico= findViewById(R.id.PER_spinner_especialidad_modificarMedico)
        spinner_centroTrabajoMedico= findViewById(R.id.PER_spinner_centroTrabajo_modificarMedico)
        txt_telefonomedico= findViewById(R.id.PER_txt_telefono_modificarMedico)
        txt_emailMedico= findViewById(R.id.PER_txt_email_modificarMedico)
        btn_aceptar= findViewById(R.id.PA_btn_aceptar_modificarDatosPaciente)

        //Montar spinner
        AdaptadorSpinnerEspecialidades(R.array.PA_spinner_especialidades_medicas)

        //Montar el spinner Centros médicos
        cargarCentrosMedicosParaSpinner()
        AdaptadorSpinnerCentros(listaCentrosMedicosSpinner)

        //Rellenar los campos con la información del médico
        lbl_idMedico.text = getString(R.string.PER_titulo_modificarMedico,idMedicoRecibido)
        txt_nombreMedico.setText(nombreMedicoRecibido)
        txt_apellidosMedico.setText(apellidosMedicoRecibido)
        txt_numColegiadoMedico.setText(numColegiadoMedicoRecibido)
        for(i in 0..spinner_especialidadMedico.adapter.count-1){
            if(spinner_especialidadMedico.adapter.getItem(i).toString() == especialidadMedicoRecibido){
                spinner_especialidadMedico.setSelection(i)
            }
        }
        spinner_especialidadMedico
        for(i in 0..spinner_centroTrabajoMedico.adapter.count-1){
            if(spinner_centroTrabajoMedico.adapter.getItem(i).toString() == nombreCentroMedicoRecibido){
                spinner_centroTrabajoMedico.setSelection(i)
            }
        }
        txt_telefonomedico.setText(telefonoMedicoRecibido)
        txt_emailMedico.setText(emailMedicoRecibido)

        //Gestión del botón Aceptar
        btn_aceptar.setOnClickListener {
            var idMedicoModificado = idMedicoRecibido
            var nombreMedicoModificado = txt_nombreMedico.text.toString()
            var apellidosModificado = txt_apellidosMedico.text.toString()
            var telefonoMedicoModificado = txt_telefonomedico.text.toString()
            var emailMedicoModificado = txt_emailMedico.text.toString()
            var especialidadMedicoModificado = spinner_especialidadMedico.selectedItem.toString()
            var numColegiadoMedicoModificado = txt_numColegiadoMedico.text.toString()
            var esAdminMedicoModificado = esAdminMedicoRecibido
            var idUsuarioKFModificado = idUsuarioRecibido

            //Buscar el id del centro de trabajo seleccionado
            var nombreCentroTrabajoModificado = spinner_centroTrabajoMedico.selectedItem.toString()

            var idCentroMedicoFKModificado = obtenerIDCentroMedicoSeleccionado(nombreCentroTrabajoModificado)

            //control de errores
            var validacionMedUsu = ValidacionMedicosUsuarios()
            var validacionMedUsuBBDD = ValidacionMedicosUsuariosBBDD(this,this)

            if(!validacionMedUsu.esNombreMedicoValido(nombreMedicoModificado)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_nombre_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esApellidosMedicoValidos(apellidosModificado)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_apellidos_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esTelefonoValido(telefonoMedicoModificado)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_telefono_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esEmailValido(emailMedicoModificado)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_email_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esEspecialidadSeleccionada(spinner_especialidadMedico.selectedItemPosition)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_especialidad_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esNumColegiadoValido(numColegiadoMedicoModificado)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_numColegiado_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsuBBDD.esNumColegiadoValidoParaModificacion(numColegiadoMedicoModificado,numColegiadoMedicoRecibido)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.LO_Toast_MedicoExiste,Toast.LENGTH_SHORT).show()
            }
            else if(!validacionMedUsu.esCentroMedicoSeleccionado(spinner_centroTrabajoMedico.selectedItemPosition)){
                Toast.makeText(this@ModificarMedicoActivity,R.string.PER_toastError_centroTrabajo_modificarMedico,Toast.LENGTH_SHORT).show()
            }
            else{
                // Realizar la actualización eb bbdd
                val modificarcionMedico = ModificacionRemotaMedicos()
                // Como ModificacionRemotaMedicos es suspend, se necesita una corrutina
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = modificarcionMedico.modificarMedico(
                        idMedicoModificado,
                        nombreMedicoModificado,
                        apellidosModificado,
                        telefonoMedicoModificado,
                        emailMedicoModificado,
                        especialidadMedicoModificado,
                        numColegiadoMedicoModificado,
                        esAdminMedicoModificado,
                        idCentroMedicoFKModificado,
                        idUsuarioKFModificado
                    )
                    //Para poder usar un toast en la rutina hay que usar este bloque
                    runOnUiThread {
                        if (resultado) {
                            Toast.makeText(
                                this@ModificarMedicoActivity, R.string.PER_ToastExito_modificarMedico, Toast.LENGTH_SHORT).show()
                            enviarIntent(DatosDelMedicoActivity::class.java,"OrigenBtnPerfil",idMedicoRecibido,esAdminMedicoRecibido,idUsuarioRecibido)
                        } else {
                            Toast.makeText(
                                this@ModificarMedicoActivity,
                                R.string.PA_toastErrorModificacion_ModificarPaciente,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }


        }

    }

    fun procesarExtras(extras : Bundle){
        idMedicoRecibido = extras.getString("idMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        nombreMedicoRecibido = extras.getString("nombreMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        apellidosMedicoRecibido = extras.getString("apellidosMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        telefonoMedicoRecibido = extras.getString("telefonoMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        emailMedicoRecibido = extras.getString("emailMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        especialidadMedicoRecibido = extras.getString("especialidadMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        numColegiadoMedicoRecibido = extras.getString("numColegiadoMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        esAdminMedicoRecibido = extras.getString("esAdminMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        nombreCentroMedicoRecibido = extras.getString("nombreCentroMedico") ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuarioFK") ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    fun AdaptadorSpinnerCentros(listaCentrosMedicos: MutableList<String>) {
        if (listaCentrosMedicos.isNotEmpty() &&
            listaCentrosMedicos[0] != getString(R.string.LO_NoExistenCentrosMedicos)
        ) {
            listaCentrosMedicos.add(0, getString(R.string.LO_tituloSpiner))
        }
        adaptadorSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaCentrosMedicos)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Asignar el adaptador al spinner
        spinner_centroTrabajoMedico.adapter = adaptadorSpinner
    }

    //Adaptador spinner especialidades médicas
    private fun AdaptadorSpinnerEspecialidades(StringFrase: Int) {
        val adaptador =
            ArrayAdapter.createFromResource(this, StringFrase, android.R.layout.simple_spinner_item)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_especialidadMedico.setAdapter(adaptador)
    }

    fun cargarCentrosMedicosParaSpinner() {
        //-----Cominucacion con la API-Rest-----------
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = consultaRemotaCentrosMedicos.obtenerListado()
        //verificamos que result no está vacio
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idCentroMedicoBD = jsonObject.getString("idCentroMedico")
                    nombreCentroMedicoBD = jsonObject.getString("nombreCentroMedico")
                    telefonoCentroMedicoBD = jsonObject.getString("telefonoCentroMedico")
                    latitudCentroMedicoBD = jsonObject.getString("latitudCentroMedico")
                    longitudCentroMedicoBD = jsonObject.getString("longitudCentroMedico")
                    direccionCentroMedicoBD = jsonObject.getString("direccionCentroMedico")
                    localidadCentroMedicoBD = jsonObject.getString("localidadCentroMedico")
                    codigoPostalCentroMedicoBD = jsonObject.getString("codigoPostalCentroMedico")
                    provinciaCentroMedicoBD = jsonObject.getString("provinciaCentroMedico")
                    esHospitalCentroMedicoBD = jsonObject.getString("esHospitalCentroMedico")

                    listaCentrosMedicosSpinner.add(nombreCentroMedicoBD)
                }
            } else {
                Log.e("ModificarMedicoActivity", "El JSONObject está vacío")
                listaCentrosMedicosSpinner.clear()
                listaCentrosMedicosSpinner.add(getString(R.string.LO_NoExistenCentrosMedicos))
            }
        } catch (e: JSONException) {
            Log.e("ModificarMedicoActivity", "Error al procesar el JSON", e)
        }
    }

    //Enviar a intent para ida y vuelta
    private fun enviarIntent(
        activityDestino: Class<out Activity>, claveOrigen: String, idMedico: String?,  esAdminMedico: String?,  idUsuario: String?
    ) {
        val intent = Intent(this@ModificarMedicoActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        enviarIntent(DatosDelMedicoActivity::class.java,"OrigenBtnPerfil",idMedicoRecibido,esAdminMedicoRecibido,idUsuarioRecibido)
    }

    fun obtenerIDCentroMedicoSeleccionado(nombreCentro: String?) : String{
        //-----Cominucacion con la API-Rest-----------
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = consultaRemotaCentrosMedicos.obtenerIdCentroMedicoPorNombre(nombreCentro?:"")
        //verificamos que result no está vacio
        var idCentroMedicoFKModificado : String = ""
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                     idCentroMedicoFKModificado = jsonObject.getString("idCentroMedico")
                }
            } else {
                Log.e("ModificarMedicoActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("ModificarMedicoActivity", "Error al procesar el JSON", e)
        }
        return idCentroMedicoFKModificado
    }

    override fun consultarExistenciaUsuario(nombreUsuario: String): Boolean {
        return true
    }

    override fun consultarExistenciaMedico(numColegiadoMedico: String): Boolean {
        var existeMedico: Boolean = false
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaMedicos = ConsultaRemotaMedicos()
        result = consultaRemotaMedicos.obtenerMedicoPorNumCol(numColegiadoMedico)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    var numColegiadoMedicoBD = jsonObject.getString("numColegiadoMedico")
                    if (numColegiadoMedicoBD == numColegiadoMedico) {
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
        return existeMedico
    }
}