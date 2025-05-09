package es.studium.opcionpacientes

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R
import es.studium.operacionesbd_pacientes.AltaRemotaPacientes
import es.studium.operacionesbd_pacientes.ModificacionRemotaPacientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AltaPacienteActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_idPaciente: TextView
    private lateinit var txt_nombrePaciente: EditText
    private lateinit var txt_apellidosPaciente: EditText
    private lateinit var spinner_sexoPaciente: AppCompatSpinner
    private lateinit var txt_fechaNacPaciente: EditText
    private lateinit var txt_nuhsaPaciente: EditText
    private lateinit var txt_telefonoPaciente: EditText
    private lateinit var txt_emailPaciente: EditText
    private lateinit var txt_dniPaciente: EditText
    private lateinit var txt_direccionPaciente: EditText
    private lateinit var txt_localidadPaciente: EditText
    private lateinit var txt_provinciaPaciente: EditText
    private lateinit var txt_codigoPostalPaciente: EditText
    private lateinit var btn_aceptar: Button
    private lateinit var btn_volver: ImageView

    //Declaración de extras recibidos
    private var esAdminMedicoRecibido: String? = ""
    private var idMedicoRecibido: String? = ""
    private var idUsuarioRecibido: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_activity_alta_paciente)

        //Recepción de los extras del Activity anterior con datos de usuario y médico
        val extras = intent.extras
        if (extras != null) {
            esAdminMedicoRecibido = extras.getString("esAdminMedico")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
            idMedicoRecibido = extras.getString("idMedico")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
            idUsuarioRecibido = extras.getString("idUsuario")
                ?: getString(R.string.LO_ErrorExtraNoRecibido)
        }

        //Enlazar variables con vistas
        lbl_idPaciente = findViewById(R.id.PA_lbl_idPaciente_AltaDatosPaciente)
        txt_nombrePaciente = findViewById(R.id.PA_txt_nombrePaciente_AltaDatosPaciente)
        txt_apellidosPaciente = findViewById(R.id.PA_txt_apellidosPaciente_AltaDatosPaciente)
        spinner_sexoPaciente = findViewById(R.id.PA_spinner_sexoPaciente_AltaDatosPaciente)
        txt_fechaNacPaciente = findViewById(R.id.PA_txt_fechaNacPaciente_AltaDatosPaciente)
        txt_fechaNacPaciente.setText(obtenerFechaActual()) //<-- Establecer la fecha actual por defecto
        txt_nuhsaPaciente = findViewById(R.id.PA_txt_nuhsaPaciente_AltaDatosPaciente)
        txt_telefonoPaciente = findViewById(R.id.PA_txt_telefonoPaciente_AltaDatosPaciente)
        txt_emailPaciente = findViewById(R.id.PA_txt_emailPaciente_AltaDatosPaciente)
        txt_dniPaciente = findViewById(R.id.PA_txt_dniPaciente_AltaDatosPaciente)
        txt_direccionPaciente = findViewById(R.id.PA_txt_direccionPaciente_AltaDatosPaciente)
        txt_localidadPaciente = findViewById(R.id.PA_txt_localidadPaciente_AltaDatosPaciente)
        txt_provinciaPaciente = findViewById(R.id.PA_txt_provinciaPaciente_AltaDatosPaciente)
        txt_codigoPostalPaciente =
            findViewById(R.id.PA_txt_codigoPostalPaciente_AltaDatosPaciente)
        btn_aceptar = findViewById(R.id.PA_btn_aceptar_AltaDatosPaciente)
        btn_volver = findViewById(R.id.btnVolver_AltaPacienteActivity)

        //Gestión de apertura del calendario cuando se presione sobre el edittext de fechaNac
        txt_fechaNacPaciente.inputType = InputType.TYPE_NULL
        txt_fechaNacPaciente.isFocusable = false
        txt_fechaNacPaciente.isClickable = true
        txt_fechaNacPaciente.setOnClickListener {
            abrirCalendarioFechaActual(txt_fechaNacPaciente)
        }

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            enviarIntentVolver(esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
        }

        //Montar spinner
        AdaptadorSpinnerSexo(R.array.PA_spinner_sexoPaciente_modificarPaciente)

        //Gestión del botón Aceptar
        btn_aceptar.setOnClickListener {

            var nombrePacienteIntroducido = txt_nombrePaciente.text.toString()
            var apellidosPacienteIntroducido = txt_apellidosPaciente.text.toString()
            var sexoPacienteIntroducido = spinner_sexoPaciente.selectedItem.toString()
            var fechaNacPacienteIntroducido =
                fechaEuropeaAMysql(txt_fechaNacPaciente.text.toString())
            var nuhsaPacienteIntroducido = txt_nuhsaPaciente.text.toString()
            var telefonoPacienteIntroducido = txt_telefonoPaciente.text.toString()
            var emailPacienteIntroducido = txt_emailPaciente.text.toString()
            var dniPacienteIntroducido = txt_dniPaciente.text.toString()
            var direccionPacienteIntroducido = txt_direccionPaciente.text.toString()
            var localidadPacienteIntroducido = txt_localidadPaciente.text.toString()
            var provinciaPacienteIntroducido = txt_provinciaPaciente.text.toString()
            var codigoPostalPacienteIntroducido = txt_codigoPostalPaciente.text.toString()

            //control de errores
            if (nombrePacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_NombrePaciente, Toast.LENGTH_SHORT).show()
            } else if (apellidosPacienteIntroducido.toString().isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_apellidosPaciente, Toast.LENGTH_SHORT).show()
            } else if (sexoPacienteIntroducido == getString(R.string.PA_itemCeroSpinner)) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_sexoPaciente, Toast.LENGTH_SHORT).show()
            } else if (nuhsaPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_nuhsaPaciente, Toast.LENGTH_SHORT).show()
            } else if (telefonoPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_telefonoPaciente, Toast.LENGTH_SHORT).show()
            }  else if (direccionPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_direccionPaciente, Toast.LENGTH_SHORT).show()
            } else if (localidadPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_localidadPaciente, Toast.LENGTH_SHORT).show()
            } else if (provinciaPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_provinciaPaciente, Toast.LENGTH_SHORT).show()
            } else if (codigoPostalPacienteIntroducido.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_codigoPostalPaciente, Toast.LENGTH_SHORT).show()
            } else if (codigoPostalPacienteIntroducido.length != 5) {
                Toast.makeText(this, R.string.PA_toastErrorDistintoCinco_codigoPostalPaciente, Toast.LENGTH_SHORT).show()
            } else {
                // Realizar la actualización eb bbdd
                val altaPaciente = AltaRemotaPacientes()
                // Como ModificacionRemotaPacientes es suspend, se necesita una corrutina
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = altaPaciente.altaPaciente(
                        nombrePacienteIntroducido,
                        apellidosPacienteIntroducido,
                        sexoPacienteIntroducido,
                        fechaNacPacienteIntroducido,
                        nuhsaPacienteIntroducido,
                        telefonoPacienteIntroducido,
                        emailPacienteIntroducido,
                        dniPacienteIntroducido,
                        direccionPacienteIntroducido,
                        localidadPacienteIntroducido,
                        provinciaPacienteIntroducido,
                        codigoPostalPacienteIntroducido
                    )
                    //Para poder usar un toast en la rutina hay que usar este bloque
                    runOnUiThread {
                        if (resultado) {
                            Toast.makeText(
                                this@AltaPacienteActivity, R.string.PA_toastExitoAlta_AltaPaciente, Toast.LENGTH_SHORT).show()
                            enviarIntentVolver(esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
                        } else {
                            Toast.makeText(
                                this@AltaPacienteActivity, R.string.PA_toastErrorAlta_AltaPaciente, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    //Metodo para montar el spinner
    private fun AdaptadorSpinnerSexo(StringFrase: Int) {
        val adaptador =
            ArrayAdapter.createFromResource(this, StringFrase, android.R.layout.simple_spinner_item)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_sexoPaciente.setAdapter(adaptador)
    }

    //Metodo para pasar fechas Europeas a MySQL
    fun fechaEuropeaAMysql(fecha: String): String {
        lateinit var fechaTransformada: String
        var elementosFecha = fecha.split("/")
        if (elementosFecha.size == 3) {
            fechaTransformada = "${elementosFecha[2]}-${elementosFecha[1]}-${elementosFecha[0]}"
        } else {
            fechaTransformada = getString(R.string.PA_error_ModificarFecha)
        }
        return fechaTransformada
    }

    fun obtenerFechaActual() : String{
        val calendario = Calendar.getInstance()
        var anyo = calendario.get(Calendar.YEAR)
        var mes = calendario.get(Calendar.MONTH)
        var dia = calendario.get(Calendar.DAY_OF_MONTH)
        var mesCorrecto = mes + 1
        var fecha = "%02d/%02d/%04d".format(dia, mesCorrecto, anyo)
        return fecha
    }

    //Metodo para abrir el calendario en la fechaActual
    fun abrirCalendarioFechaActual(cuadroFecha: EditText){
        val calendario = Calendar.getInstance()
        var anyo = calendario.get(Calendar.YEAR)
        var mes = calendario.get(Calendar.MONTH)
        var dia = calendario.get(Calendar.DAY_OF_MONTH)

        var dialogoFecha =
            DatePickerDialog(
                this@AltaPacienteActivity, R.style.Estilo_ColoresCalendario,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(datePicker: DatePicker, anyo: Int, mes: Int, dia: Int) {
                        var mesCorrecto = mes + 1
                        var fecha = "%02d/%02d/%04d".format(dia, mesCorrecto, anyo)
                        cuadroFecha.setText(fecha)
                    }
                }, anyo, mes, dia
            )
        dialogoFecha.show()
    }

    //Enviar intent de vuelta a PrincipalPacientesActivity
    fun enviarIntentVolver(esAdminMedico: String?,idMedico: String?, idUsuario : String?
    ) {
        val intent = Intent(this, PrincipalPacientesActivity::class.java)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)

        startActivity(intent)
    }
}