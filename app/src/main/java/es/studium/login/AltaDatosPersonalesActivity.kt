package es.studium.login

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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


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

    //Declaración objetos consulta
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
            }
            else if (txt_Apellidos.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorApellidos, Toast.LENGTH_SHORT).show()
            }
            else if (txt_Especialidad.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorEspecialidad, Toast.LENGTH_SHORT).show()
            }
            else if (spiner_CentroMedico.selectedItemPosition == 0) {
                Toast.makeText(this, R.string.LO_Toast_ErrorCentro, Toast.LENGTH_SHORT).show()
            }
            else if (txt_telefono.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorTelefono, Toast.LENGTH_SHORT).show()
            }
            else if (txt_email.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorEmail, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Todo ok", Toast.LENGTH_SHORT).show()
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
}