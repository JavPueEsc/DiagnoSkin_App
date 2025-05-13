package es.studium.opcion_Estadisticas

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R

class EstadisticasActivity : AppCompatActivity() {
    //Declaración de las variables de las vistas

    private lateinit var btn_volver : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.es_activity_estadisticas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }
}