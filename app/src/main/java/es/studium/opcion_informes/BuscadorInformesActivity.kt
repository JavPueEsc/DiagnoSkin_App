package es.studium.opcion_informes

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import es.studium.opcion_diagnosticos.PrincipalPacientes2Activity
import es.studium.operacionesdb_diagnosticos.ConsultaRemotaDiagnosticos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class BuscadorInformesActivity : AppCompatActivity() {
    //Declaracion de vistas
    private lateinit var lbl_apellidosNombrePaciente : TextView
    private lateinit var lbl_nuhsaPaciente : TextView
    private lateinit var txt_fechaDesde : EditText
    private lateinit var txt_fechaHasta : EditText
    private lateinit var btn_buscarDiagnosticos : Button
    private lateinit var lblFijo_seleccionDiagnosticos : TextView
    private lateinit var spinner_diagnosticos : AppCompatSpinner
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
    private lateinit var idMedicoFKBD : String
    private lateinit var idPacienteFKBD : String

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
        spinner_diagnosticos = findViewById(R.id.INF_spinner_seleccionDiag_BuscadorInformes)
        spinner_diagnosticos.visibility = View.GONE //<-- Oculto de inicio
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

        //gestión btn volver
        btn_volver.setOnClickListener{
            enviarIntentVuelta(PrincipalPacientes2Activity::class.java,"OrigenBtnInformes", esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
        }

        //Gestión btn buscar diagnosticos
        btn_buscarDiagnosticos.setOnClickListener {
            //Control de errores:fecha desde no puede ser superior a fecha hasta y no pueden quedar vacíos
            //Convertimos las fechas a Date para poder compararlas
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaDesdeString = txt_fechaDesde.text.toString()
            val fechaHastaString = txt_fechaHasta.text.toString()

            if (fechaDesdeString.isEmpty()) {
                Toast.makeText(this, R.string.INF_toastErrorFechaDesdeVacia_BuscadorInformes, Toast.LENGTH_SHORT).show()
            } else if (fechaHastaString.isEmpty()) {
                Toast.makeText(this, R.string.INF_toastErrorFechaHastaVacia_BuscadorInformes, Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val fechaDesde = formatoFecha.parse(fechaDesdeString)
                    var fechaHasta = formatoFecha.parse(fechaHastaString)

                    if (fechaDesde != null && fechaHasta != null && fechaDesde.after(fechaHasta)) {
                        Toast.makeText(this, R.string.INF_toastErrorFechaDesdeMayor_BuscadorInformes, Toast.LENGTH_SHORT).show()
                    } else {
                        //Toast.makeText(this, "Rango de fechas CORRECTO", Toast.LENGTH_SHORT).show()

                        //Limpiar el array por si hay resultados anteriores evitamos que se añadan
                        listaDiagnosticosSpinner.clear()
                        //Solicitamos los datos a la base de datos
                        cargarDatosEntreFechasSeleccionadas(
                            fechaEuropeaAMysql(fechaDesdeString),
                            fechaEuropeaAMysql(fechaHastaString),
                            idPacienteRecibido
                        )
                        montarSpinnerAdapter(listaDiagnosticosSpinner)
                        //Mostrar el spinner
                        lblFijo_seleccionDiagnosticos.visibility = View.VISIBLE
                        spinner_diagnosticos.visibility = View.VISIBLE

                        //Gestión de los elementos seleccionados en el spinner
                        spinner_diagnosticos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val itemSeleccionado = parent.getItemAtPosition(position).toString()

                                if (itemSeleccionado != getString(R.string.INF_spinner_NoExistenDiagnosticos) &&
                                    itemSeleccionado != getString(R.string.INF_spinner_Seleccion)
                                ) {
                                    lblFijo_diagnosticoSeleccionado.visibility = View.VISIBLE
                                    lbl_idDiagnostico.visibility = View.VISIBLE
                                    lbl_fechaDiagnostico.visibility = View.VISIBLE
                                    lbl_diagDiagnostico.visibility = View.VISIBLE
                                    lbl_tipoLesionDiagnostico.visibility = View.VISIBLE
                                    btn_visualizar.visibility = View.VISIBLE

                                    val elementos = itemSeleccionado.split("-")
                                    idSeleccionado = elementos[0].replace("#", "")
                                    diagnosticoSeleccionado = elementos[1]
                                    gravedadSeleccionada = elementos[2].replace("Lesión ", "")
                                    fechaSeleccionada = elementos[3].replace("(", "").replace(")","")

                                    lbl_idDiagnostico.text = getString(R.string.INF_lbl_idDiag_BuscadorInformes, idSeleccionado)
                                    lbl_fechaDiagnostico.text = getString(R.string.INF_lbl_fechaDiagnostico_BuscadorInformes,fechaSeleccionada)
                                    lbl_diagDiagnostico.text = getString(R.string.INF_lbl_diagDiag_BuscadorInformes,diagnosticoSeleccionado)
                                    lbl_tipoLesionDiagnostico.text = getString(R.string.INF_lbl_tipoLesion_BuscadorInformes,gravedadSeleccionada)
                                } else {
                                    lbl_idDiagnostico.text = ""
                                    lbl_fechaDiagnostico.text = ""
                                    lbl_diagDiagnostico.text = ""
                                    lbl_tipoLesionDiagnostico.text = ""
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // Opcional: manejar si se deselecciona algo
                            }
                        }

                    }
                } catch (e: ParseException) {
                    Toast.makeText(this, "Error en el formato de fechas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Gestión del botón visualizar
        btn_visualizar.setOnClickListener {
            //mandar los extras recibudis + idDiagnosticoSeleccionado.
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

    fun cargarDatosEntreFechasSeleccionadas(fechaDesde: String, fechaHasta: String, idPaciente : String?) {
        //-----Cominucacion con la API-Rest-----------
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var accesoRemotoDiagnosticos = ConsultaRemotaDiagnosticos()
        result = accesoRemotoDiagnosticos.obtenerListadoEntreFechas(fechaDesde, fechaHasta)
        //verificamos que result no está vacio
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idDiagnosticoBD = jsonObject.getString("idDiagnostico")
                    diagnosticoDiagnosticoBD = jsonObject.getString("diagnosticoDiagnostico")
                    gravedadDiagnosticoBD = jsonObject.getString("gravedadDiagnostico")
                    fechaDiagnosticoBD = jsonObject.getString("fechaDiagnostico")
                    idMedicoFKBD = jsonObject.getString("idMedicoFK")
                    idPacienteFKBD = jsonObject.getString("idPacienteFK")
                    if(idPacienteFKBD == idPaciente){
                        listaDiagnosticosSpinner.add(getString(R.string.INF_linea_spinner,idDiagnosticoBD,diagnosticoDiagnosticoBD,gravedadDiagnosticoBD,fechaMysqlAEuropea(fechaDiagnosticoBD)))
                    }
                }
            } else {
                Log.e("BuscadorInformesActivity", "El JSONObject está vacío")
                //listaDiagnosticosSpinner.add("No existen diagnósticos disponibles")
            }
        } catch (e: JSONException) {
            Log.e("BuscadorInformesActivity", "Error al procesar el JSON", e)
        }
    }

    //Metodo para pasar fechas Europeas a MySQL
    fun fechaEuropeaAMysql(fecha: String): String {
        lateinit var fechaTransformada: String
        var elementosFecha = fecha.split("/")
        if (elementosFecha.size == 3) {
            fechaTransformada = "${elementosFecha[2]}-${elementosFecha[1]}-${elementosFecha[0]}"
        } else {
            fechaTransformada = "Error al formatear fechas"
        }
        return fechaTransformada
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

    fun montarSpinnerAdapter(listaDiagnosticos: MutableList<String>) {
        if(listaDiagnosticos.isEmpty()){
            listaDiagnosticos.add(0, getString(R.string.INF_spinner_NoExistenDiagnosticos))
        }
        else if (listaDiagnosticos[0] != getString(R.string.INF_spinner_NoExistenDiagnosticos)) {
            listaDiagnosticos.add(0,getString(R.string.INF_spinner_Seleccion))
        }

        adaptadorSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaDiagnosticos)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Asignar el adaptador al spinner
        spinner_diagnosticos.adapter = adaptadorSpinner
    }

    //Enviar intent de vuelta
    private fun enviarIntentVuelta(
        activityDestino: Class<out Activity>, claveOrigen: String, esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@BuscadorInformesActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

}