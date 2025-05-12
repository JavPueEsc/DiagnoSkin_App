package es.studium.opcion_informes

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R
import org.json.JSONArray
import org.json.JSONObject

class BuscadorInformesActivity : AppCompatActivity() {
    //Declaracion de vistas
    private lateinit var lbl_apellidosNombrePaciente : TextView
    private lateinit var lbl_nuhsaPaciente : TextView
    private lateinit var txt_fechaDesde : EditText
    private lateinit var txt_fechaHasta : EditText
    private lateinit var btn_buscarDiagnosticos : Button
    private lateinit var lblFijo_seleccionDiagnosticos : TextView
    private lateinit var spiner_diagnosticos : AppCompatSpinner
    private lateinit var lblFijo_diagnosticoSeleccionado : TextView
    private lateinit var lbl_idDiagnostico : TextView
    private lateinit var lbl_diagDiagnostico : TextView
    private lateinit var lbl_tipoLesionDiagnostico : TextView
    private lateinit var lbl_fechaDiagnostico : TextView
    private lateinit var btn_visualizar: Button
    private lateinit var btn_volver : ImageView

    //Declaración de extras recibidos
    private var idPacienteRecibido: String? = null
    private var nombrePacienteRecibido: String? = null
    private var apellidosPacienteRecibido: String? = null
    private var sexoPacienteRecibido: String? = null
    private var fechaNacPacienteRecibido: String? = null
    private var nuhsaPacienteRecibido: String? = null
    private var telefonoPacienteRecibido: String? = null
    private var emailPacienteRecibido: String? = null
    private var dniPacienteRecibido: String? = null
    private var direccionPacienteRecibido: String? = null
    private var localidadPacienteRecibido: String? = null
    private var provinciaPacienteRecibido: String? = null
    private var codigoPostalPacienteRecibido: String? = null
    private var esAdminMedicoRecibido: String? = null
    private var idMedicoRecibido: String? = null
    private var idUsuarioRecibido: String? = null

    //variables para la consulta a la bbdd
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    private lateinit var idDiagnosticoBD: String
    private lateinit var diagnosticoDiagnosticoBD: String
    private lateinit var gravedadDiagnosticoBD: String
    private lateinit var fechaDiagnosticoBD: String

    //Variable para acumular los resultados de diagnosticos para montar el spinner
    private var listaDiagnosticosSpinner: MutableList<String> = mutableListOf()
    private lateinit var adaptadorSpinner: ArrayAdapter<String>

    //variables para acceder a la posición del spinner seleccionada
    private lateinit var  idSeleccionado : String
    private lateinit var  diagnosticoSeleccionado : String
    private lateinit var  gravedadSeleccionada : String
    private lateinit var  fechaSeleccionada : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inf_activity_buscador_informes)
        //Recibir extras
        val extras = intent.extras
        if (extras != null) {
                procesarExtras(extras)
        }

        //Enlazar vistas
        lbl_apellidosNombrePaciente = findViewById(R.id.INF_lbl_nombrePaciente_BuscadorInformes)
        lbl_apellidosNombrePaciente.text = getString(R.string.INF_lbl_apellidosNombre_BuscadorInformes,apellidosPacienteRecibido, nombrePacienteRecibido) //<--seteo nombre
        lbl_nuhsaPaciente = findViewById(R.id.INF_lbl_nuhsaPaciente_BuscadorInformes) //<--seteo nuhsa
        lbl_nuhsaPaciente.text = getString(R.string.INF_lbl_nuhsa_BuscadorInformes, nuhsaPacienteRecibido)
        txt_fechaDesde = findViewById(R.id.INF_txt_fechaDesde_BuscadorInformes)
        txt_fechaHasta = findViewById(R.id.INF_txt_fechaHasta_BuscadorInformes)
        btn_buscarDiagnosticos = findViewById(R.id.INF_btn_BuscarDiagnosticos_BuscadorInformes)
        lblFijo_seleccionDiagnosticos = findViewById(R.id.INF_lbl_SeleccionDiag_BuscadorInformes)
        lblFijo_seleccionDiagnosticos.visibility = View.GONE //<-- Oculto de inicio
        spiner_diagnosticos = findViewById(R.id.INF_spinner_seleccionDiag_BuscadorInformes)
        spiner_diagnosticos.visibility = View.GONE //<-- Oculto de inicio
        lblFijo_diagnosticoSeleccionado = findViewById(R.id.INF_lbl_DiagSeleccionado_BuscadorInformes)
        lblFijo_diagnosticoSeleccionado.visibility = View.GONE //<-- Oculto de inicio
        lbl_idDiagnostico = findViewById(R.id.INF_lbl_idDiag_BuscadorInformes)
        lbl_idDiagnostico.visibility = View.GONE //<-- Oculto de inicio
        lbl_diagDiagnostico = findViewById(R.id.INF_lbl_diadDiag_BuscadorInformes)
        lbl_diagDiagnostico.visibility = View.GONE //<-- Oculto de inicio
        lbl_tipoLesionDiagnostico = findViewById(R.id.INF_lbl_tipolesion_BuscadorInformes)
        lbl_tipoLesionDiagnostico.visibility = View.GONE //<-- Oculto de inicio
        lbl_fechaDiagnostico = findViewById(R.id.INF_lbl_fechaDiag_BuscadorInformes)
        lbl_fechaDiagnostico.visibility = View.GONE //<-- Oculto de inicio
        btn_visualizar = findViewById(R.id.INF_btn_visualizarInforme_BuscadorInformes)
        btn_visualizar.visibility = View.GONE //<-- Oculto de inicio
        btn_volver = findViewById(R.id.INF_btnVolver_BuscadorInformes)

        //gestion de txt_fechas: volverlos no seleccionables y no editables
        //Gestión de apertura del calendario cuando se presione sobre el edittext de fechaNac
        txt_fechaDesde.inputType = InputType.TYPE_NULL
        txt_fechaDesde.isFocusable = false
        txt_fechaDesde.isClickable = true
        txt_fechaDesde.setOnClickListener {
            abrirCalendarioFechaActual(txt_fechaDesde)
        }
        txt_fechaHasta.inputType = InputType.TYPE_NULL
        txt_fechaHasta.isFocusable = false
        txt_fechaHasta.isClickable = true
        txt_fechaHasta.setOnClickListener {
            abrirCalendarioFechaActual(txt_fechaHasta)
        }


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
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuario")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    //Metodo para abrir el calendario en la fechaActual
    fun abrirCalendarioFechaActual(cuadroFecha: EditText){
        val calendario = Calendar.getInstance()
        var anyo = calendario.get(Calendar.YEAR)
        var mes = calendario.get(Calendar.MONTH)
        var dia = calendario.get(Calendar.DAY_OF_MONTH)

        var dialogoFecha =
            DatePickerDialog(
                this@BuscadorInformesActivity, R.style.Estilo_ColoresCalendario,
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
}