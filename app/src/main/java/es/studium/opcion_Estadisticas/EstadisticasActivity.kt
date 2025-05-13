package es.studium.opcion_Estadisticas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import es.studium.diagnoskin_app.R
import org.json.JSONArray
import org.json.JSONObject

class EstadisticasActivity : AppCompatActivity() {
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
    private lateinit var txt_fechaDesde : EditText
    private lateinit var txt_fechaHasta : EditText
    private lateinit var btn_volver : Button

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.es_activity_estadisticas)

        //Enlazar vistas
        lbl_centro = findViewById(R.id.ES_lbl_NombreCentro_Estadisticas)
        lbl_dirección= findViewById(R.id.ES_lbl_direccionCentro_Estadisticas)
        lbl_localidad= findViewById(R.id.ES_lbl_localidadCentro_Estadisticas)
        lblTelefono= findViewById(R.id.ES_lbl_telefonoCentro_Estadisticas)
        lblFijo_tituloDiagnosticos= findViewById(R.id.ES_lbl_tituloDiagnosticos_Estadisticas)
        lblFijo_RangoFecha= findViewById(R.id.ES_lbl_tituloRangoFechas_Estadisticas)
        lblFijo_fechaDesde= findViewById(R.id.ES_lbl_fechaDesde_Estadisticas)
        lblFijo_fechaHasta= findViewById(R.id.ES_lbl_fechaHasta_Estadisticas)
        btn_Buscar= findViewById(R.id.ES_btn_BuscarDiagnosticos_BuscadorInformes)
        lbl_Melanomas= findViewById(R.id.ES_lbl_Melanomas_Estadisticas)
        lbl_Onicomicosis= findViewById(R.id.ES_lbl_Onicomicosis_Estadisticas)
        lbl_Angiomas= findViewById(R.id.ES_lbl_Angiomas_Estadisticas)
        lbl_Dermatofibromas= findViewById(R.id.ES_lbl_Dermatofibromas_Estadisticas)
        txt_fechaDesde = findViewById(R.id.ES_txt_fechaDesde_Estadisticas)
        txt_fechaHasta = findViewById(R.id.ES_txt_fechaHasta_Estadisticas)
        btn_volver = findViewById(R.id.ES_btnVolver_Estadisticas)

    }


    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }
}