package es.studium.opcionpacientes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R

class DatosDelPacienteActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var lbl_idPaciente: TextView
    private lateinit var lbl_nombrePaciente: TextView
    private lateinit var lbl_apellidosPaciente: TextView
    private lateinit var lbl_sexoPaciente: TextView
    private lateinit var lbl_fechaNacPaciente: TextView
    private lateinit var lbl_nuhsaPaciente: TextView
    private lateinit var lbl_telefonoPaciente: TextView
    private lateinit var lbl_emailPaciente: TextView
    private lateinit var lbl_dniPaciente: TextView
    private lateinit var lbl_direccionPaciente: TextView
    private lateinit var lbl_localidadPaciente: TextView
    private lateinit var lbl_provinciaPaciente: TextView
    private lateinit var lbl_codigoPostalPaciente: TextView
    private lateinit var btn_modificar: Button
    private lateinit var btn_verDiagnosticos: Button
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
        setContentView(R.layout.pa_activity_datos_del_paciente)
        //Recepción de los extras del Activity anterior con datos de paciente y médico
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("origenPrincipalPacienteActivity")) {
                procesarExtras(extras)
            } else {
                procesarExtras(extras)
            }
        }

        //Enlazar variables con vistas
        lbl_idPaciente = findViewById(R.id.PA_lbl_idPaciente_datosPaciente)
        lbl_nombrePaciente = findViewById(R.id.PA_lbl_nombrePaciente_datosPaciente)
        lbl_apellidosPaciente = findViewById(R.id.PA_lbl_apellidosPaciente_datosPaciente)
        lbl_sexoPaciente = findViewById(R.id.PA_lbl_sexoPaciente_datosPaciente)
        lbl_fechaNacPaciente = findViewById(R.id.PA_lbl_fechaNacPaciente_datosPaciente)
        lbl_nuhsaPaciente = findViewById(R.id.PA_lbl_nuhsaPaciente_datosPaciente)
        lbl_telefonoPaciente = findViewById(R.id.PA_lbl_telefonoPaciente_datosPaciente)
        lbl_emailPaciente = findViewById(R.id.PA_lbl_emailPaciente_datosPaciente)
        lbl_dniPaciente = findViewById(R.id.PA_lbl_dniPaciente_datosPaciente)
        lbl_direccionPaciente = findViewById(R.id.PA_lbl_direccionPaciente_datosPaciente)
        lbl_localidadPaciente = findViewById(R.id.PA_lbl_localidadPaciente_datosPaciente)
        lbl_provinciaPaciente = findViewById(R.id.PA_lbl_provinciaPaciente_datosPaciente)
        lbl_codigoPostalPaciente = findViewById(R.id.PA_lbl_codigoPostalPaciente_datosPaciente)
        btn_modificar = findViewById(R.id.PA_btn_Modificar_datosPaciente)
        btn_verDiagnosticos = findViewById(R.id.PA_btn_verDiagnosticos_datosPaciente)
        btn_volver = findViewById(R.id.btnVolver_DatosDelPacienteActivity)

        //Gestion del Botón volver
        btn_volver.setOnClickListener {
            enviarIntentAPrincipal(esAdminMedicoRecibido)
        }

        // Establecer los datos del paciente en las vistas usando strings con placeholders
        lbl_idPaciente.text =
            getString(R.string.PA_lbl_idPaciente_datosDelPaciente, idPacienteRecibido)
        lbl_nombrePaciente.text =
            getString(R.string.PA_lbl_nombrePaciente_datosDelPaciente, nombrePacienteRecibido)
        lbl_apellidosPaciente.text =
            getString(R.string.PA_lbl_apellidosPaciente_datosDelPaciente, apellidosPacienteRecibido)
        lbl_sexoPaciente.text =
            getString(R.string.PA_lbl_sexoPaciente_datosDelPaciente, sexoPacienteRecibido)
        lbl_fechaNacPaciente.text = getString(
            R.string.PA_lbl_fechaNacPaciente_datosDelPaciente,
            fechaMysqlAEuropea(
                fechaNacPacienteRecibido ?: getString(R.string.PA_btn_errorFechaNoDisponible)
            )
        )
        lbl_nuhsaPaciente.text =
            getString(R.string.PA_lbl_nuhsaPaciente_datosDelPaciente, nuhsaPacienteRecibido)
        lbl_telefonoPaciente.text =
            getString(R.string.PA_lbl_telefonoPaciente_datosDelPaciente, telefonoPacienteRecibido)
        lbl_emailPaciente.text =
            getString(R.string.PA_lbl_emailPaciente_datosDelPaciente, emailPacienteRecibido)
        lbl_dniPaciente.text =
            getString(R.string.PA_lbl_dniPaciente_datosDelPaciente, dniPacienteRecibido)
        lbl_direccionPaciente.text =
            getString(R.string.PA_lbl_direccionPaciente_datosDelPaciente, direccionPacienteRecibido)
        lbl_localidadPaciente.text =
            getString(R.string.PA_lbl_localidadPaciente_datosDelPaciente, localidadPacienteRecibido)
        lbl_provinciaPaciente.text =
            getString(R.string.PA_lbl_provinciaPaciente_datosDelPaciente, provinciaPacienteRecibido)
        lbl_codigoPostalPaciente.text = getString(
            R.string.PA_lbl_codigoPostalPaciente_datosDelPaciente,
            codigoPostalPacienteRecibido
        )

        btn_modificar.setOnClickListener {
            var intentModificar = Intent(this@DatosDelPacienteActivity,ModificarPacienteActivity::class.java)
            intentModificar.putExtra("idPaciente", idPacienteRecibido)
            intentModificar.putExtra("nombrePaciente", nombrePacienteRecibido)
            intentModificar.putExtra("apellidosPaciente", apellidosPacienteRecibido)
            intentModificar.putExtra("sexoPaciente", sexoPacienteRecibido)
            intentModificar.putExtra("fechaNacPaciente", fechaNacPacienteRecibido)
            intentModificar.putExtra("nuhsaPaciente", nuhsaPacienteRecibido)
            intentModificar.putExtra("telefonoPaciente", telefonoPacienteRecibido)
            intentModificar.putExtra("emailPaciente", emailPacienteRecibido)
            intentModificar.putExtra("dniPaciente", dniPacienteRecibido)
            intentModificar.putExtra("direccionPaciente", direccionPacienteRecibido)
            intentModificar.putExtra("localidadPaciente", localidadPacienteRecibido)
            intentModificar.putExtra("provinciaPaciente", provinciaPacienteRecibido)
            intentModificar.putExtra("codigoPostalPaciente", codigoPostalPacienteRecibido)
            intentModificar.putExtra("esAdminMedico", esAdminMedicoRecibido)

            startActivity(intentModificar)
        }
        btn_verDiagnosticos.setOnClickListener {
            Toast.makeText(this, "Presion ver diagnosticos", Toast.LENGTH_SHORT).show()
        }
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

    //Médodo para recibir los extras independientemente del Activity del que provengan
    private fun procesarExtras(extras: Bundle) {
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

    //Enviar intent de vuelta
    fun enviarIntentAPrincipal(esAdminMedico:String?){
        val intent = Intent(this@DatosDelPacienteActivity,PrincipalPacientesActivity::class.java)
        intent.putExtra("esAdminMedico", esAdminMedicoRecibido)
        startActivity(intent)
    }
}