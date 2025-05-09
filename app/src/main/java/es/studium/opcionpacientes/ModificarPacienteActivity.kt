package es.studium.opcionpacientes

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import es.studium.diagnoskin_app.R


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
    private lateinit var btn_volver : ImageView

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
        txt_codigoPostalPaciente = findViewById(R.id.PA_txt_codigoPostalPaciente_modificarDatosPaciente)
        btn_aceptar = findViewById(R.id.PA_btn_aceptar_modificarDatosPaciente)
        btn_volver = findViewById(R.id.btnVolver_ModificarPacienteActivity)

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            finish()
        }

        //Montar spinner
        AdaptadorSpinnerSexo(R.array.PA_spinner_sexoPaciente_modificarPaciente)

        // Establecer los datos del paciente en las los edittext (id en textview con placeholder)
        lbl_idPaciente.text =
            getString(R.string.PA_lbl_idPaciente_modificarPaciente, idPacienteRecibido)
        txt_nombrePaciente.setText(nombrePacienteRecibido)
        txt_apellidosPaciente.setText(apellidosPacienteRecibido)
        if(sexoPacienteRecibido=="Hombre"){
            spinner_sexoPaciente.setSelection(1)
        }
        else{
            spinner_sexoPaciente.setSelection(2)
        }
        txt_fechaNacPaciente.setText(fechaMysqlAEuropea(fechaNacPacienteRecibido ?: getString(R.string.PA_btn_errorFechaNoDisponible)))
        txt_nuhsaPaciente.setText(nuhsaPacienteRecibido)
        txt_telefonoPaciente.setText(telefonoPacienteRecibido)
        txt_emailPaciente.setText(emailPacienteRecibido)
        txt_dniPaciente.setText(dniPacienteRecibido)
        txt_direccionPaciente.setText(direccionPacienteRecibido)
        txt_localidadPaciente.setText(localidadPacienteRecibido)
        txt_provinciaPaciente.setText(provinciaPacienteRecibido)
        txt_codigoPostalPaciente.setText(codigoPostalPacienteRecibido)

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
            fechaTransformada = "Error al formatear fechas"
        }
        return fechaTransformada
    }

}