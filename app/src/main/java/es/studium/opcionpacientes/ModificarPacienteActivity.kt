package es.studium.opcionpacientes

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import es.studium.diagnoskin_app.R
import es.studium.operacionesbd_pacientes.ModificacionRemotaPacientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ModificarPacienteActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_activity_modificar_paciente)
        //Recepción de los extras del Activity anterior con datos de paciente y médico
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
        lbl_idPaciente = findViewById(R.id.PA_lbl_idPaciente_modificarDatosPaciente)
        txt_nombrePaciente = findViewById(R.id.PA_txt_nombrePaciente_modificarDatosPaciente)
        txt_apellidosPaciente = findViewById(R.id.PA_txt_apellidosPaciente_modificarDatosPaciente)
        spinner_sexoPaciente = findViewById(R.id.PA_spinner_sexoPaciente_modificarDatosPaciente)
        txt_fechaNacPaciente = findViewById(R.id.PA_txt_fechaNacPaciente_modificarDatosPaciente)
        txt_nuhsaPaciente = findViewById(R.id.PA_txt_nuhsaPaciente_modificarDatosPaciente)
        txt_telefonoPaciente = findViewById(R.id.PA_txt_telefonoPaciente_modificarDatosPaciente)
        txt_emailPaciente = findViewById(R.id.PA_txt_emailPaciente_modificarDatosPaciente)
        txt_dniPaciente = findViewById(R.id.PA_txt_dniPaciente_modificarDatosPaciente)
        txt_direccionPaciente = findViewById(R.id.PA_txt_direccionPaciente_modificarDatosPaciente)
        txt_localidadPaciente = findViewById(R.id.PA_txt_localidadPaciente_modificarDatosPaciente)
        txt_provinciaPaciente = findViewById(R.id.PA_txt_provinciaPaciente_modificarDatosPaciente)
        txt_codigoPostalPaciente =
            findViewById(R.id.PA_txt_codigoPostalPaciente_modificarDatosPaciente)
        btn_aceptar = findViewById(R.id.PA_btn_aceptar_modificarDatosPaciente)
        btn_volver = findViewById(R.id.btnVolver_ModificarPacienteActivity)

        //Gestión de apertura del calendario cuando se presione sobre el edittext de fechaNac
        txt_fechaNacPaciente.inputType = InputType.TYPE_NULL
        txt_fechaNacPaciente.isFocusable = false
        txt_fechaNacPaciente.isClickable = true
        txt_fechaNacPaciente.setOnClickListener {
            abrirCalendario(txt_fechaNacPaciente, txt_fechaNacPaciente.text.toString())
        }

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            enviarIntent(
                "ModificarPacienteActivity", idPacienteRecibido, nombrePacienteRecibido, apellidosPacienteRecibido,
                sexoPacienteRecibido, fechaNacPacienteRecibido, nuhsaPacienteRecibido, telefonoPacienteRecibido, emailPacienteRecibido,
                dniPacienteRecibido, direccionPacienteRecibido, localidadPacienteRecibido, provinciaPacienteRecibido, codigoPostalPacienteRecibido,
                esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido
            )
        }

        //Montar spinner
        AdaptadorSpinnerSexo(R.array.PA_spinner_sexoPaciente_modificarPaciente)

        // Establecer los datos del paciente en las los edittext (id en textview con placeholder)
        lbl_idPaciente.text =
            getString(R.string.PA_lbl_idPaciente_modificarPaciente, idPacienteRecibido)
        txt_nombrePaciente.setText(nombrePacienteRecibido)
        txt_apellidosPaciente.setText(apellidosPacienteRecibido)
        if (sexoPacienteRecibido == "Hombre") {
            spinner_sexoPaciente.setSelection(1)
        } else {
            spinner_sexoPaciente.setSelection(2)
        }
        txt_fechaNacPaciente.setText(
            fechaMysqlAEuropea(
                fechaNacPacienteRecibido ?: getString(R.string.PA_btn_errorFechaNoDisponible)
            )
        )
        txt_nuhsaPaciente.setText(nuhsaPacienteRecibido)
        txt_telefonoPaciente.setText(telefonoPacienteRecibido)
        txt_emailPaciente.setText(emailPacienteRecibido)
        txt_dniPaciente.setText(dniPacienteRecibido)
        txt_direccionPaciente.setText(direccionPacienteRecibido)
        txt_localidadPaciente.setText(localidadPacienteRecibido)
        txt_provinciaPaciente.setText(provinciaPacienteRecibido)
        txt_codigoPostalPaciente.setText(codigoPostalPacienteRecibido)

        //Gestión del botón Aceptar
        btn_aceptar.setOnClickListener {
            var idPacienteModificado = idPacienteRecibido
            var nombrePacienteModificado = txt_nombrePaciente.text.toString()
            var apellidosPacienteModificado = txt_apellidosPaciente.text.toString()
            var sexoPacienteModificado = spinner_sexoPaciente.selectedItem.toString()
            var fechaNacPacienteModificado =
                fechaEuropeaAMysql(txt_fechaNacPaciente.text.toString())
            var nuhsaPacienteModificado = txt_nuhsaPaciente.text.toString()
            var telefonoPacienteModificado = txt_telefonoPaciente.text.toString()
            var emailPacienteModificado = txt_emailPaciente.text.toString()
            var dniPacienteModificado = txt_dniPaciente.text.toString()
            var direccionPacienteModificado = txt_direccionPaciente.text.toString()
            var localidadPacienteModificado = txt_localidadPaciente.text.toString()
            var provinciaPacienteModificado = txt_provinciaPaciente.text.toString()
            var codigoPostalPacienteModificado = txt_codigoPostalPaciente.text.toString()

            //control de errores
            if (nombrePacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_NombrePaciente, Toast.LENGTH_SHORT).show()
            } else if (apellidosPacienteModificado.toString().isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_apellidosPaciente, Toast.LENGTH_SHORT).show()
            } else if (sexoPacienteModificado == getString(R.string.PA_itemCeroSpinner)) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_sexoPaciente, Toast.LENGTH_SHORT).show()
            } else if (nuhsaPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_nuhsaPaciente, Toast.LENGTH_SHORT).show()
            } else if (telefonoPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_telefonoPaciente, Toast.LENGTH_SHORT).show()
            }  else if (direccionPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_direccionPaciente, Toast.LENGTH_SHORT).show()
            } else if (localidadPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_localidadPaciente, Toast.LENGTH_SHORT).show()
            } else if (provinciaPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_provinciaPaciente, Toast.LENGTH_SHORT).show()
            } else if (codigoPostalPacienteModificado.isEmpty()) {
                Toast.makeText(this, R.string.PA_toastErrorVacio_codigoPostalPaciente, Toast.LENGTH_SHORT).show()
            } else if (codigoPostalPacienteModificado.length != 5) {
                Toast.makeText(this, R.string.PA_toastErrorDistintoCinco_codigoPostalPaciente, Toast.LENGTH_SHORT).show()
            } else {
                // Realizar la actualización eb bbdd
                val modificarcionPaciente = ModificacionRemotaPacientes()
                // Como ModificacionRemotaPacientes es suspend, se necesita una corrutina
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = modificarcionPaciente.modificarPaciente(
                        idPacienteModificado!!,
                        nombrePacienteModificado,
                        apellidosPacienteModificado,
                        sexoPacienteModificado,
                        fechaNacPacienteModificado,
                        nuhsaPacienteModificado,
                        telefonoPacienteModificado,
                        emailPacienteModificado,
                        dniPacienteModificado,
                        direccionPacienteModificado,
                        localidadPacienteModificado,
                        provinciaPacienteModificado,
                        codigoPostalPacienteModificado
                    )
                    //Para poder usar un toast en la rutina hay que usar este bloque
                    runOnUiThread {
                        if (resultado) {
                            Toast.makeText(
                                this@ModificarPacienteActivity, R.string.PA_toastExitoModificacion_ModificarPaciente, Toast.LENGTH_SHORT).show()
                            enviarIntent(
                                "ModificarPacienteActivity", idPacienteModificado, nombrePacienteModificado, apellidosPacienteModificado,
                                sexoPacienteModificado, fechaNacPacienteModificado, nuhsaPacienteModificado, telefonoPacienteModificado, emailPacienteModificado,
                                dniPacienteModificado, direccionPacienteModificado, localidadPacienteModificado, provinciaPacienteModificado, codigoPostalPacienteModificado,
                                esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido
                            )
                        } else {
                            Toast.makeText(
                                this@ModificarPacienteActivity,
                                R.string.PA_toastErrorModificacion_ModificarPaciente,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
        }

    }

    private fun AdaptadorSpinnerSexo(StringFrase: Int) {
        val adaptador =
            ArrayAdapter.createFromResource(this, StringFrase, android.R.layout.simple_spinner_item)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_sexoPaciente.setAdapter(adaptador)
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

    //Metodo para abrir el calendario
    fun abrirCalendario(cuadroFecha: EditText, fechaEstablecida: String) {
        var elementosFecha = fechaEstablecida.split("/")
        var anyo = elementosFecha.get(2).toInt()
        var mes = elementosFecha.get(1).toInt()
        var dia = elementosFecha.get(0).toInt()

        var dialogoFecha =
            DatePickerDialog(
                this@ModificarPacienteActivity, R.style.Estilo_ColoresCalendario,
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

    //Metodo para enviar intent de vuelta a DatosDelPacienteActivity
    fun enviarIntent(
        origenModificarPacienteActivity: String, idPaciente: String?, nombrePaciente: String?, apellidosPaciente: String?, sexoPaciente: String?, fechaNacPaciente: String?,
        nuhsaPaciente: String?, telefonoPaciente: String?, emailPaciente: String?, dniPaciente: String?, direccionPaciente: String?, localidadPaciente: String?,
        provinciaPaciente: String?, codigoPostalPaciente: String?, esAdminMedico: String?,idMedico: String?, idUsuario : String?
    ) {
        val intent = Intent(this, DatosDelPacienteActivity::class.java)
        intent.putExtra("origenModificarPacienteActivity", origenModificarPacienteActivity)
        intent.putExtra("idPaciente", idPaciente)
        intent.putExtra("nombrePaciente", nombrePaciente)
        intent.putExtra("apellidosPaciente", apellidosPaciente)
        intent.putExtra("sexoPaciente", sexoPaciente)
        intent.putExtra("fechaNacPaciente", fechaNacPaciente)
        intent.putExtra("nuhsaPaciente", nuhsaPaciente)
        intent.putExtra("telefonoPaciente", telefonoPaciente)
        intent.putExtra("emailPaciente", emailPaciente)
        intent.putExtra("dniPaciente", dniPaciente)
        intent.putExtra("direccionPaciente", direccionPaciente)
        intent.putExtra("localidadPaciente", localidadPaciente)
        intent.putExtra("provinciaPaciente", provinciaPaciente)
        intent.putExtra("codigoPostalPaciente", codigoPostalPaciente)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)

        startActivity(intent)
    }

}