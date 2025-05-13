package es.studium.opcion_Estadisticas

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.ModeloDiagnostico
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesdb_diagnosticos.ConsultaRemotaDiagnosticos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class EstadisticasActivity : FragmentActivity(), OnMapReadyCallback {
    //Declaración de las variables de las vistas
    private lateinit var lbl_centro: TextView
    private lateinit var lbl_dirección: TextView
    private lateinit var lbl_localidad: TextView
    private lateinit var lblTelefono: TextView
    private lateinit var lblFijo_tituloDiagnosticos: TextView
    private lateinit var lblFijo_RangoFecha: TextView
    private lateinit var lblFijo_fechaDesde: TextView
    private lateinit var lblFijo_fechaHasta: TextView
    private lateinit var btn_Buscar : Button
    private lateinit var lbl_Melanomas: TextView
    private lateinit var lbl_Onicomicosis: TextView
    private lateinit var lbl_Angiomas: TextView
    private lateinit var lbl_Dermatofibromas: TextView
    private lateinit var lbl_nevus: TextView
    private lateinit var txt_fechaDesde : EditText
    private lateinit var txt_fechaHasta : EditText
    private lateinit var lblFijo_asteriscos : TextView
    private lateinit var btn_agrandarPantalla : ImageView
    private lateinit var btn_volver : ImageView

    //Variables para extraer los extras recibidos
    private lateinit var esMedicoAdminRecibido : String
    private lateinit var idMedicoRecibido : String
    private lateinit var idUsuarioRecibido : String

    //Variables para consulta base de datos
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    //consulta centro medico
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
    //Consulta diagnosticos
    private lateinit var patologiaConsultada : String
    private var idCentroSeleccionado : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.es_activity_estadisticas)

        //Recibir EXTRA con los datos del usuario médico
        val extras = intent.extras
        if (extras != null) {
                procesarExtras(extras)
        }

        // Obtenemos el mapa de forma asíncrona (notificará cuando esté listo)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Enlazar vistas
        lbl_centro = findViewById(R.id.ES_lbl_NombreCentro_Estadisticas)
        lbl_centro.visibility = View.GONE
        lbl_dirección= findViewById(R.id.ES_lbl_direccionCentro_Estadisticas)
        lbl_dirección.visibility = View.GONE
        lbl_localidad= findViewById(R.id.ES_lbl_localidadCentro_Estadisticas)
        lbl_localidad.visibility = View.GONE
        lblTelefono= findViewById(R.id.ES_lbl_telefonoCentro_Estadisticas)
        lblTelefono.visibility = View.GONE
        lblFijo_tituloDiagnosticos= findViewById(R.id.ES_lbl_tituloDiagnosticos_Estadisticas)
        lblFijo_tituloDiagnosticos.visibility = View.GONE
        lblFijo_RangoFecha= findViewById(R.id.ES_lbl_tituloRangoFechas_Estadisticas)
        lblFijo_RangoFecha.visibility = View.GONE
        lblFijo_fechaDesde= findViewById(R.id.ES_lbl_fechaDesde_Estadisticas)
        lblFijo_fechaDesde.visibility = View.GONE
        lblFijo_fechaHasta= findViewById(R.id.ES_lbl_fechaHasta_Estadisticas)
        lblFijo_fechaHasta.visibility = View.GONE
        btn_Buscar= findViewById(R.id.ES_btn_BuscarDiagnosticos_BuscadorInformes)
        btn_Buscar.visibility = View.GONE
        lbl_Melanomas= findViewById(R.id.ES_lbl_Melanomas_Estadisticas)
        lbl_Melanomas.visibility = View.GONE
        lbl_Onicomicosis= findViewById(R.id.ES_lbl_Onicomicosis_Estadisticas)
        lbl_Onicomicosis.visibility = View.GONE
        lbl_Angiomas= findViewById(R.id.ES_lbl_Angiomas_Estadisticas)
        lbl_Angiomas.visibility = View.GONE
        lbl_Dermatofibromas= findViewById(R.id.ES_lbl_Dermatofibromas_Estadisticas)
        lbl_Dermatofibromas.visibility = View.GONE
        lbl_nevus = findViewById(R.id.ES_lbl_Nevus_Estadisticas)
        lbl_nevus.visibility = View.GONE
        txt_fechaDesde = findViewById(R.id.ES_txt_fechaDesde_Estadisticas)
        txt_fechaDesde.visibility = View.GONE
        txt_fechaHasta = findViewById(R.id.ES_txt_fechaHasta_Estadisticas)
        txt_fechaHasta.visibility = View.GONE
        lblFijo_asteriscos = findViewById(R.id.ES_camposObligatorios_Estadisticas)
        lblFijo_asteriscos.visibility = View.GONE
        btn_volver = findViewById(R.id.btnVolver_Estadisticas)
        btn_volver.visibility = View.VISIBLE
        btn_agrandarPantalla = findViewById(R.id.btnAgrandar_Estadisticas)

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
        //Gestión del botón volver
        btn_volver.setOnClickListener {
            val intent = Intent(this@EstadisticasActivity,MainActivity::class.java)
            intent.putExtra("idUsuario",idUsuarioRecibido)
            startActivity(intent)
        }
        //Gestión del botón agrandar pantalla 8reinicia el Activity sin perder los extras recibidos)

        btn_agrandarPantalla.setOnClickListener {
            //Modificacion del tamaño del mapa
            val marco = findViewById<View>(R.id.ES_marcoMapa_Estadisticas)
            val paddingDp = 24
            val escala = resources.displayMetrics.density
            val paddingPx = (paddingDp * escala).toInt()

            val params = marco.layoutParams as ConstraintLayout.LayoutParams
            params.height = 0 // "match constraints" en ConstraintLayout
            params.bottomMargin = paddingPx
            params.topToBottom = R.id.ES_lbl_tituloSeleccionCentro_Estadisticas  // cambia por el ID real
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            marco.layoutParams = params
            marco.setPadding(
                marco.paddingLeft,
                marco.paddingTop,
                marco.paddingRight,
                marco.paddingBottom
            )

            //Se ocultan las demás vistas
            lbl_centro.visibility = View.GONE
            lbl_dirección.visibility = View.GONE
            lbl_localidad.visibility = View.GONE
            lblTelefono.visibility = View.GONE
            lblFijo_tituloDiagnosticos.visibility = View.GONE
            lblFijo_RangoFecha.visibility = View.GONE
            lblFijo_fechaDesde.visibility = View.GONE
            lblFijo_fechaHasta.visibility = View.GONE
            btn_Buscar.visibility = View.GONE
            lbl_Melanomas.visibility = View.GONE
            lbl_Onicomicosis.visibility = View.GONE
            lbl_Angiomas.visibility = View.GONE
            lbl_Dermatofibromas.visibility = View.GONE
            lbl_nevus.visibility = View.GONE
            txt_fechaDesde.visibility = View.GONE
            txt_fechaHasta.visibility = View.GONE
            lblFijo_asteriscos.visibility = View.GONE
        }

        //Gestión del botón Buscar estadisticas
        btn_Buscar.setOnClickListener {
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
                        lbl_Melanomas.text = getString(R.string.ES_lbl_Melanomas_Estadisticas,consultarNumeroDeDiagnosticos(idCentroSeleccionado,"Melanoma"))
                        lbl_Melanomas.visibility = View.VISIBLE
                        lbl_Angiomas.text = getString(R.string.ES_lbl_Angiomas_Estadisticas,consultarNumeroDeDiagnosticos(idCentroSeleccionado,"Angioma"))
                        lbl_Angiomas.visibility = View.VISIBLE
                        lbl_nevus.text = getString(R.string.ES_lbl_Nevus_Estadisticas,consultarNumeroDeDiagnosticos(idCentroSeleccionado,"Nevus"))
                        lbl_nevus.visibility = View.VISIBLE
                        lbl_Onicomicosis.text = getString(R.string.ES_lbl_Onicomicosis_Estadisticas,consultarNumeroDeDiagnosticos(idCentroSeleccionado,"Onicomicosis"))
                        lbl_Onicomicosis.visibility = View.VISIBLE
                        lbl_Dermatofibromas.text = getString(R.string.ES_lbl_Dermatofibromas_Estadisticas,consultarNumeroDeDiagnosticos(idCentroSeleccionado,"Dermatofibroma"))
                        lbl_Dermatofibromas.visibility = View.VISIBLE
                    }
                } catch (e: ParseException) {
                    Toast.makeText(this, R.string.INF_toastErrorFormatoFechas_BuscadorInformes, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        //COMUNICACIÓN CON LA BASE DE DATOS <-----

        var ubicacionCentroJerez = LatLng(36.68539, -6.12780)
        val zoomLevel = 15f

        var mapa = googleMap
        // Se establece el tipo de mapa como normal (vista estándar de calles y terrenos)
        mapa.mapType = GoogleMap.MAP_TYPE_NORMAL
        // Se habilitan los controles de zoom para permitir al usuario acercar y alejar el mapa.
        mapa.uiSettings.isZoomControlsEnabled = true

        EstablecerMarcadores(mapa)

        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentroJerez, zoomLevel))

        //Añadir Listener al mapa para capturar la pulsación sobre el marcador
        mapa.setOnMarkerClickListener { marker ->
            val idCentro = marker.tag as? String
            idCentroSeleccionado = idCentro
            if(idCentro!=null){
                //Redimensionar el mapa
                val alturaDp = 250
                val escala = resources.displayMetrics.density
                val alturaPx = (alturaDp * escala).toInt()
                val marco = findViewById<View>(R.id.ES_marcoMapa_Estadisticas)
                val params = marco.layoutParams
                params.height = alturaPx
                marco.layoutParams = params

                CapturarDatosDelMarcador(mapa,idCentro)
                lbl_centro.text=getString(R.string.ES_lbl_NombreCentro_Estadisticas, nombreCentroMedicoBD)
                lbl_dirección.text=getString(R.string.ES_lbl_direccionCentro_Estadisticas, direccionCentroMedicoBD)
                lbl_localidad.text=getString(R.string.ES_lbl_localidadCentro_Estadisticas,localidadCentroMedicoBD,provinciaCentroMedicoBD,codigoPostalCentroMedicoBD)
                lblTelefono.text=getString(R.string.ES_lbl_telefonoCentro_Estadisticas,telefonoCentroMedicoBD)
                lbl_centro.visibility = View.VISIBLE
                lbl_dirección.visibility = View.VISIBLE
                lbl_localidad.visibility = View.VISIBLE
                lblTelefono.visibility = View.VISIBLE
                lbl_centro.visibility = View.VISIBLE
                lbl_dirección.visibility = View.VISIBLE
                lbl_localidad.visibility = View.VISIBLE
                lblTelefono.visibility = View.VISIBLE
                lblFijo_tituloDiagnosticos.visibility = View.VISIBLE
                lblFijo_RangoFecha.visibility = View.VISIBLE
                lblFijo_fechaDesde.visibility = View.VISIBLE
                lblFijo_fechaHasta.visibility = View.VISIBLE
                txt_fechaDesde.visibility = View.VISIBLE
                txt_fechaHasta.visibility = View.VISIBLE
                btn_Buscar.visibility = View.VISIBLE
                lblFijo_asteriscos.visibility = View.VISIBLE
            }
            else{
                Log.e("Error con idCentro", "Nos e ha posido acceder al id del dentro")
            }
            Log.d("MarkerClick", "ID Centro Médico: $idCentro")
            false
        }
    }

    fun EstablecerMarcadores(mapa: GoogleMap) {
        //-----Cominucacion con la API-Rest-----------
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var accesoRemotoCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = accesoRemotoCentrosMedicos.obtenerListado()
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

                    Log.d("EstadisticasActivity", "Número de centros: ${result.length()}")
                    Log.d("EstadisticasActivity", "LatLng: $latitudCentroMedicoBD, $longitudCentroMedicoBD")


                    var ubicacion = LatLng(
                        latitudCentroMedicoBD.toDouble(),
                        longitudCentroMedicoBD.toDouble()
                    )

                    if (esHospitalCentroMedicoBD == "0") {
                        val marker = mapa.addMarker(
                            MarkerOptions()
                                .position(ubicacion)
                                .title(nombreCentroMedicoBD)
                                .snippet(getString(R.string.ES_csalud_Estadisticas))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntero_cs))
                                .anchor(0.5f,1.0f)
                        )
                        marker?.tag = idCentroMedicoBD
                    } else {
                        val marker = mapa.addMarker(
                            MarkerOptions()
                                .position(ubicacion)
                                .title(nombreCentroMedicoBD)
                                .snippet(getString(R.string.ES_hospital_Estadisticas))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntero_hos))
                                .anchor(0.5f,1.0f)
                        )
                        marker?.tag = idCentroMedicoBD
                    }
                }
            } else {
                Log.e("MainActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
    }

    fun CapturarDatosDelMarcador(mapa: GoogleMap, idCentro : String) {
        //-----Cominucacion con la API-Rest-----------
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var accesoRemotoCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = accesoRemotoCentrosMedicos.obtenerCentroMedicoPorId(idCentro)
        //verificamos que result no está vacio
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idCentroMedicoBD = jsonObject.getString("idCentroMedico")
                    nombreCentroMedicoBD = jsonObject.getString("nombreCentroMedico")
                    telefonoCentroMedicoBD = jsonObject.getString("telefonoCentroMedico")
                    latitudCentroMedicoBD = jsonObject.getString("latitudCentromedico")
                    longitudCentroMedicoBD = jsonObject.getString("longitudCentroMedico")
                    direccionCentroMedicoBD = jsonObject.getString("direccionCentroMedico")
                    localidadCentroMedicoBD = jsonObject.getString("localidadCentroMedico")
                    codigoPostalCentroMedicoBD = jsonObject.getString("codigoPostalCentroMedico")
                    provinciaCentroMedicoBD = jsonObject.getString("provinciaCentroMedico")
                    esHospitalCentroMedicoBD = jsonObject.getString("esHospitalCentroMedico")

                    //Log.d("MainActivity", "Número de centros: ${result.length()}")
                    //Log.d("MainActivity", "LatLng: $latitudCentroMedicoBD, $longitudCentroMedicoBD")

                }
            } else {
                Log.e("EstadisticasActivity", "El JSONObject está vacío")
            }
        } catch (e: JSONException) {
            Log.e("EstadisticasActivity", "Error al procesar el JSON", e)
        }
    }

    //Metodo para abrir el calendario en la fechaActual
    fun abrirCalendarioFechaActual(cuadroFecha: EditText){
        val calendario = Calendar.getInstance()
        var anyo = calendario.get(Calendar.YEAR)
        var mes = calendario.get(Calendar.MONTH)
        var dia = calendario.get(Calendar.DAY_OF_MONTH)

        var dialogoFecha =
            DatePickerDialog(
                this@EstadisticasActivity, R.style.Estilo_ColoresCalendario,
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

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }

    //Médodo para recibir los extras
    private fun procesarExtras(extras: Bundle) {
        esMedicoAdminRecibido = extras.getString("esAdminMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioRecibido = extras.getString("idUsuario")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }

    fun consultarNumeroDeDiagnosticos(idCentroMedico : String?, patologia: String) : String{
        //-----Cominucacion con la API-Rest-----------
        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var numeroDeDiagnosticos =""
        var consultaRemotaDiagnosticos = ConsultaRemotaDiagnosticos()
        result = consultaRemotaDiagnosticos.obtenerNumDiagPorPatologia(idCentroMedico,patologia)

        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    numeroDeDiagnosticos = jsonObject.getString("diagnosticos")
                }
            }
            else{
                Log.e("EstadisticasActivity_consultaDiahnosticos", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("EstadisticasActivity_consultaDiagnosticos", "Error al procesar el JSON", e)
        }
        return numeroDeDiagnosticos
    }
}