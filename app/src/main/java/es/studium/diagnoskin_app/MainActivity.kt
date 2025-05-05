package es.studium.diagnoskin_app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btn_pacientes : View
    private lateinit var btn_diagnosticos : View
    private lateinit var btn_informes : View
    private lateinit var btn_estadisticas : View
    private lateinit var btn_perfil : View
    private lateinit var btn_administrador : View

    var esAdminPrueba = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_pacientes = findViewById(R.id.MP_btn_Pacientes)
        btn_diagnosticos = findViewById(R.id.MP_btn_Diagnosticos)
        btn_informes = findViewById(R.id.MP_btn_Informes)
        btn_estadisticas = findViewById(R.id.MP_btn_Estadisticas)
        btn_perfil = findViewById(R.id.MP_btn_Perfil)
        btn_administrador = findViewById(R.id.MP_btn_Admin)

        //Recibimos los extras del intent del Loging <---------------------------

        //Consulta a la base de datos de medicos por idUsuarioFK <---------------

        //Se oculta el botón de Administrador si el usuario no lo es.
        if(esAdminPrueba=="0"){
            btn_administrador.visibility = View.GONE
        }

        //1. Gestión del botón Pacientes
        btn_pacientes.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
        //2. Gestión del botón Diagnósticos
        btn_diagnosticos.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
        //3. Gestión del botón informes
        btn_informes.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
        //4. Gestión del botón estadísticas
        btn_estadisticas.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
        //5. Gestión del botón perfil
        btn_informes.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
        //6. Gestión del botón Administrador
        btn_administrador.setOnClickListener {
            Toast.makeText(this,"Funciona",Toast.LENGTH_SHORT).show()
        }
    }
}