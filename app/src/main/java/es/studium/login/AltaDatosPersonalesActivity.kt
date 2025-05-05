package es.studium.login

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.studium.diagnoskin_app.R
import es.studium.operacionesbdcentrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbdmedicos.AltaRemotaMedicos
import es.studium.operacionesbdusuarios.AltaRemotaUsuarios
import es.studium.operacionesbdusuarios.ConsultaRemotaUsuarios
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AltaDatosPersonalesActivity : AppCompatActivity() {
    //Declaracion de las vistas
    private lateinit var txt_Nombre: EditText
    private lateinit var txt_Apellidos: EditText
    private lateinit var txt_Especialidad: EditText
    private lateinit var spiner_CentroMedico: Spinner
    private lateinit var txt_telefono: EditText
    private lateinit var txt_email: EditText
    private lateinit var btn_Aceptar: Button


    //Declaración de datos introducidos
    private lateinit var nombreIntroducido: String
    private lateinit var apellidosIntroducidos: String
    private lateinit var especialidadIntroducida: String
    private lateinit var centroMedicoSeleccionado: String
    private lateinit var telefonoIntroducido: String
    private lateinit var emailIntroducido: String

    //Variable para acumular los resultados de diagnosticos para montar el spinner
    private var listaCentrosMedicosSpinner: MutableList<String> = mutableListOf()
    private lateinit var adaptadorSpinner: ArrayAdapter<String>

    //Declaración objetos consulta tabla médicos
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

    //Declaración objetos consulta usuarios
    private lateinit var idUsuarioBD: String
    private lateinit var nombreUsuarioBD: String
    private lateinit var claveUsuarioBD: String
    private lateinit var fechaAltaUsuarioBD: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_alta_datos_personales)
        //Asociación de variables con Vistas
        txt_Nombre = findViewById(R.id.LO_txt_NombrePersonal)
        txt_Apellidos = findViewById(R.id.LO_txt_Apellidos)
        txt_Especialidad = findViewById(R.id.LO_txt_Especialidad)
        spiner_CentroMedico = findViewById(R.id.LO_spinner_centrosMedicos)
        txt_telefono = findViewById(R.id.LO_txt_Telefono)
        txt_email = findViewById(R.id.LO_txt_Email)
        btn_Aceptar = findViewById(R.id.LO_btn_AceptarDatosPersonales)

        //Montar el spinner
        cargarCentrosMedicosParaSpinner()
        montarSpinnerAdapter(listaCentrosMedicosSpinner)

        //Gestión de la pulsación de una opción del spinner
        spiner_CentroMedico.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSeleccionado = parent.getItemAtPosition(position).toString()

                if (itemSeleccionado != getString(R.string.LO_tituloSpiner) &&
                    itemSeleccionado != getString(R.string.LO_NoExistenCentrosMedicos)
                ) {
                    centroMedicoSeleccionado = itemSeleccionado
                    Log.d("Spinner", "Centro Médico Seleccionado: $itemSeleccionado")

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se usa
            }
        }

        //Gestión botón Aceptar
        btn_Aceptar.setOnClickListener {
            //Control de errores
            if (txt_Nombre.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorNombre, Toast.LENGTH_SHORT).show()
            } else if (txt_Apellidos.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorApellidos, Toast.LENGTH_SHORT).show()
            } else if (txt_Especialidad.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorEspecialidad, Toast.LENGTH_SHORT).show()
            } else if (spiner_CentroMedico.selectedItemPosition == 0) {
                Toast.makeText(this, R.string.LO_Toast_ErrorCentro, Toast.LENGTH_SHORT).show()
            } else if (txt_telefono.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorTelefono, Toast.LENGTH_SHORT).show()
            } else if (txt_email.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorEmail, Toast.LENGTH_SHORT).show()
            } else {

                //Recepción de la información de la Activity anterior
                val extras = intent.extras
                if (extras != null) {
                    val nombreUsuarioRecibido = extras.getString("nombreUsuario")
                        ?: getString(R.string.LO_ErrorExtraNoRecibido)
                    val claveUsuarioRecibida = extras.getString("claveUsuario")
                        ?: getString(R.string.LO_ErrorExtraNoRecibido)
                    val numColegiadoRecibido = extras.getString("numColegiadoMedico")
                        ?: getString(R.string.LO_ErrorExtraNoRecibido)

                    //Inserción del nuevo usuario en la tabla usuarios de la BBDD
                    var altaUsuario = AltaRemotaUsuarios()
                    val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    var fechaAltaUsuario = formatoFecha.format(Date())
                    var altaUsuarioCorrecta: Boolean = altaUsuario.darAltaUsuarioEnBD(
                        nombreUsuarioRecibido,
                        claveUsuarioRecibida,
                        fechaAltaUsuario
                    )
                    //Obtención del idUsuario introducido
                    if (altaUsuarioCorrecta) {
                        var idUsuarioRegistrado = obtenerIdUsuario(nombreUsuarioRecibido)
                        //Inserción de datos médico en tabla médico
                        if (idUsuarioRegistrado != getString(R.string.LO_ErrorObtencionIdUsuario)) {
                            var altaMedico = AltaRemotaMedicos()
                            nombreIntroducido = txt_Nombre.text.toString()
                            apellidosIntroducidos = txt_Apellidos.text.toString()
                            especialidadIntroducida = txt_Especialidad.text.toString()
                            telefonoIntroducido = txt_Especialidad.text.toString()
                            emailIntroducido = txt_email.text.toString()
                            var centroMedicoIntroducido = obtenerIdCentroMedico(centroMedicoSeleccionado)
                            //txt_email.setText(centroMedicoIntroducido)
                            if(centroMedicoIntroducido!= getString(R.string.LO_ErrorObtencionIdCentroMedico)){
                                var altaMedicoCorrecta = altaMedico.darAltaMedicoEnBD(nombreIntroducido,apellidosIntroducidos,telefonoIntroducido,emailIntroducido,especialidadIntroducida,numColegiadoRecibido,"0",centroMedicoIntroducido,idUsuarioRegistrado)
                                if(altaMedicoCorrecta){
                                    Toast.makeText(this,R.string.LO_AltaExito,Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this,AutenticacionActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }

                    }
                }
            }
        }
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
                Log.e("MainActivity", "El JSONObject está vacío")
                listaCentrosMedicosSpinner.clear()
                listaCentrosMedicosSpinner.add(getString(R.string.LO_NoExistenCentrosMedicos))
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
    }

    fun montarSpinnerAdapter(listaCentrosMedicos: MutableList<String>) {
        if (listaCentrosMedicos.isNotEmpty() &&
            listaCentrosMedicos[0] != getString(R.string.LO_NoExistenCentrosMedicos)
        ) {
            listaCentrosMedicos.add(0, getString(R.string.LO_tituloSpiner))
        }
        adaptadorSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaCentrosMedicos)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Asignar el adaptador al spinner
        spiner_CentroMedico.adapter = adaptadorSpinner
    }

    fun obtenerIdUsuario(nombreUsuario: String): String {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaUsuarios = ConsultaRemotaUsuarios()
        result = consultaRemotaUsuarios.obtenerIdUsuarioPorNombre(nombreUsuario)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idUsuarioBD = jsonObject.getString("idUsuario")
                    nombreUsuarioBD = jsonObject.getString("nombreUsuario")
                    claveUsuarioBD = jsonObject.getString("contrasenaUsuario")
                    fechaAltaUsuarioBD = jsonObject.getString("fechaAltaUsuario")

                    if (nombreUsuarioBD == nombreUsuario) {
                        return idUsuarioBD
                        break //<-- salimos del bucle
                    }
                }
            } else {
                Log.e("MainActivity", "El JSONObject de usuario está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
        return getString(R.string.LO_ErrorObtencionIdUsuario)
    }

    fun obtenerIdCentroMedico(nombreCentroMedico: String): String {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = consultaRemotaCentrosMedicos.obtenerIdCentroMedicoPorNombre(nombreCentroMedico)
        //Verificamos que result no está vacío
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

                    if (nombreCentroMedicoBD == nombreCentroMedico) {
                        return idCentroMedicoBD
                        break //<-- salimos del bucle
                    }

                }
            } else {
                Log.e("MainActivity", "El JSONObject de centro medico está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
        return getString(R.string.LO_ErrorObtencionIdCentroMedico)
    }
}