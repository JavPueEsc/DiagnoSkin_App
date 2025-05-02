package es.studium.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.operacionesbdusuarios.ConsultaRemotaUsuarios
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest

class AutenticacionActivity : AppCompatActivity() {
    //Declaracion de las vistas
    private lateinit var txt_usuario: EditText
    private lateinit var txt_clave: EditText
    private lateinit var swt_MostrarClave: Switch
    private lateinit var btn_cuadroHuella: View
    private lateinit var btn_IniciarSesion: Button
    private lateinit var btn_CrearNuevaCuenta: TextView

    //Declaración datos introducidos
    private lateinit var usuarioIntroducido: String
    private lateinit var claveIntroducida: String

    //Declaración bjetos consulta
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject
    lateinit var idUsuarioBD: String
    lateinit var nombreUsuarioBD: String
    lateinit var claveUsuarioBD: String
    lateinit var fechaAltaUsuarioBD: String
    var credencialesCorrectas: Boolean = false

    //Declaración de las constantes necesarias para las shared Preferences
    val MyPREFERENCES = "credenciales_Acceso"
    val USUARIO_KEY = "usuario_guardado"
    val PASS_KEY = "password_guardada"
    private lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_autenticacion)
        //Asociación de variables con Vistas
        txt_usuario = findViewById(R.id.LO_txt_Usuario)
        txt_clave = findViewById(R.id.LO_txt_Clave)
        swt_MostrarClave = findViewById(R.id.LO_btn_MostrarClave)
        btn_cuadroHuella = findViewById(R.id.LO_rectangulo_huella)
        btn_IniciarSesion = findViewById(R.id.LO_btnLogin)
        btn_CrearNuevaCuenta = findViewById(R.id.LO_lbl_CrearNuevaCuenta)

        //Gestión del botón de iniciar sesión
        btn_IniciarSesion.setOnClickListener {
            usuarioIntroducido = txt_usuario.text.toString()
            claveIntroducida = txt_clave.text.toString()

            //Comprobación de las credenciales introducidas
            comprobarCredenciales(usuarioIntroducido, claveIntroducida)

            //Contro de errores
            if (txt_usuario.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorCampoUsuarioVacio, Toast.LENGTH_SHORT)
                    .show()
            } else if (txt_clave.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.LO_Toast_ErrorCampoClaveVacio, Toast.LENGTH_SHORT)
                    .show()
            } else if (credencialesCorrectas) {
                Toast.makeText(this, R.string.LO_Toast_CredencialesCorrectas, Toast.LENGTH_SHORT)
                    .show()
                credencialesCorrectas = false //Reseteo de booleano
                //Comprobación de si hay Shared preferences guardadas
                shared = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
                val isShared = shared.getString(USUARIO_KEY, "")

                //Hay S-P guardadas. el usuario quiere entrar con usu y pass. No se le pregunta siquiere guardar
                //Dos Opciones;
                //1. Quiere entrar el mismo usuario que las dejó guardadas
                //2. Quiere entrar un usuario nuevo
                if (!isShared.isNullOrEmpty()) {

                    //1. Mismo usuario
                    if (usuarioIntroducido == shared.getString(USUARIO_KEY, "")) {
                        enviarIntent()
                    }
                    //2. Distinto usuario
                    else {
                        mostrarDialogoSobrescribirCredenciales(usuarioIntroducido, claveIntroducida)
                    }

                } else {
                    mostrarDialogoGuardarCredenciales(usuarioIntroducido, claveIntroducida)
                }
            } else {
                Toast.makeText(this, R.string.LO_Toast_CredencialesIncorrectas, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //Gestión del botón Crear nueva cuenta
        btn_CrearNuevaCuenta.setOnClickListener {
            val intentAlta = Intent(this, AltaDatosDeAccesoActivity::class.java)
            startActivity(intentAlta)
        }

        //Gestión del botón de Acceso con Biometría
        btn_cuadroHuella.setOnClickListener {
            //Dos opciones:
            //1. No existen credenciales guardadas
            //2. Existen credenciales guardadas

            //1. Existen credenciales guardadas - Entrada por huella habilitada
            shared = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
            val isShared = shared.getString(USUARIO_KEY, "")
            if (!isShared.isNullOrEmpty()) {
                //1. Comprobar que el dispositivo tenga el hardware adecuado
                val biometricManager = BiometricManager.from(this)

                when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                    //1. El dispositivo cuenta con el hardware adecuado.
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        showBiometricPrompt()
                        //aquí iria el acceso a la aplicación.
                    }
                    //2. El dispositivo no cuenta con el hardware adecuado.
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        showToast("No hay hardware biométrico.")
                    }
                    //3. El hardware biométrico no está disponible
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        showToast("Hardware biométrico no disponible.")
                    }
                    //4. El dispositivo cuenta con hardware biométrico pero no hay huella registrada
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        showToast("No hay biometría registrada.")
                    }
                }
            }
            else{
                Toast.makeText(this,"No es posible el acceso biométrico. Debe guardar sus credenciales",Toast.LENGTH_SHORT).show()
            }
        }

        //Gestíón Mostrar/ocultar contraseña
        swt_MostrarClave.setOnClickListener{
            if (swt_MostrarClave.isChecked) {
                txt_clave.inputType = InputType.TYPE_CLASS_TEXT
                txt_clave.setSelection(txt_clave.text.length)
            } else {
                //Hace que el edit text admita texto (1) y por otro lado que ese texto se convierta en contraseña (2)
                txt_clave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                txt_clave.setSelection(txt_clave.text.length)
            }
        }
    }

    //Comprobar credenciales
    fun comprobarCredenciales(usuarioIntroducido: String, claveIntroducida: String) {
        //Comunicación con la API para realizar la consulta
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var consultaRemotaUsuarios = ConsultaRemotaUsuarios()
        result = consultaRemotaUsuarios.obtenerListado()
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idUsuarioBD = jsonObject.getString("idUsuario")
                    nombreUsuarioBD = jsonObject.getString("nombreUsuario")
                    claveUsuarioBD = jsonObject.getString("contrasenaUsuario")
                    fechaAltaUsuarioBD = jsonObject.getString("fechaAltaUsuario")

                    if ((nombreUsuarioBD == usuarioIntroducido) and (claveUsuarioBD == convertirASHA256(
                            claveIntroducida
                        ))
                    ) {
                        credencialesCorrectas = true
                        break //<-- salimos del bucle
                    }
                }
                Log.d("DEBUG", "HASH ENVIADO: ${convertirASHA256(claveIntroducida)}")
                Log.d("DEBUG", "HASH BD: $claveUsuarioBD")
            } else {
                Log.e("MainActivity", "El JSONObject está vacío")
                credencialesCorrectas = false
            }
        } catch (e: JSONException) {
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }
    }

    //Convertir lo que escribe el usuario a SHA2
    fun convertirASHA256(input: String): String {
        // Obtener la instancia del algoritmo SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        // Obtener el hash de la entrada
        val hashBytes = digest.digest(input.toByteArray())
        // Convertir los bytes a una cadena hexadecimal
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Muestra un diálogo de confirmación para guardar las credenciales tras el primer login exitoso
    private fun mostrarDialogoGuardarCredenciales(usuario: String, clave: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.LO_Dialogo_tituloGuardarCredenciales)
            .setMessage(R.string.LO_Dialogo_mensajeGuardarCredenciales)
            .setPositiveButton(R.string.LO_Dialogo_GuardarCredencialesOpcionSi) { _, _ ->
                // Si el usuario acepta, guardamos las credenciales de forma segura
                guardarCredenciales(this, usuario, clave)
                Toast.makeText(
                    this,
                    R.string.LO_Toast_Dialogo_GuardarCredencialesExito,
                    Toast.LENGTH_SHORT
                ).show()
                enviarIntent()
            }
            .setNegativeButton(R.string.LO_Dialogo_GuardarCredencialesOpcionNo) { _, _ ->
                enviarIntent()
            }
            .show()

    }

    // Muestra un diálogo para confirmar si se quieren sobrescribir las credenciales ya existentes
    private fun mostrarDialogoSobrescribirCredenciales(usuario: String, clave: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.LO_Dialogo_tituloSobrescribirCredenciales)
            .setMessage(R.string.LO_Dialogo_mensajeSobrescribirCredenciales)
            .setPositiveButton(R.string.LO_Dialogo_SobrescribirCredencialesOpcionSi) { _, _ ->
                guardarCredenciales(this, usuario, clave)
                Toast.makeText(
                    this,
                    R.string.LO_Toast_Dialogo_SobrescribirCredencialesExito,
                    Toast.LENGTH_SHORT
                )
                    .show()
                enviarIntent()
            }
            .setNegativeButton(R.string.LO_Dialogo_SobrescribirCredencialesOpcionNo) { _, _ ->
                enviarIntent()
            }
            .show()
    }

    fun guardarCredenciales(context: Context, usuario: String, password: String) {
        val prefs = context.getSharedPreferences("credenciales_Acceso", Context.MODE_PRIVATE)

        prefs.edit()
            .putString("usuario_guardado", usuario)
            .putString("password_guardada", password)
            .apply()
    }

    fun enviarIntent() {
        val bundle = Bundle()
        bundle.putString("idUsuario", idUsuarioBD)
        bundle.putString("nombreUsuario", nombreUsuarioBD)
        bundle.putString("claveUsuario", claveUsuarioBD)
        bundle.putString("fechaAltaUsuario", fechaAltaUsuarioBD)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    //Métodos para mostrar el prompt de huella digital
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor((this))
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val textoHuellaExito = getString(R.string.LO_Toast_HuellaExito)
                    showToast(textoHuellaExito)
                    //Si la autenticación es exitosa, metemos los datos desde las
                    //Shared preferences y simulamos un click del btn login
                    shared = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
                    val isShared = shared.getString(USUARIO_KEY, "")
                    if (!isShared.isNullOrEmpty()){
                        txt_usuario.setText(shared.getString(USUARIO_KEY,""))
                        txt_clave.setText(shared.getString(PASS_KEY,""))
                        btn_IniciarSesion.performClick()
                    }

                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val textoHuellaError = getString(R.string.LO_Toast_HuellaError)
                    showToast(textoHuellaError)
                    showToast("$textoHuellaError $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val textoHuellaFallo = getString(R.string.LO_Toast_HuellaFallo)
                    showToast(textoHuellaFallo)
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.LO_DialogoBiometria_titulo))
            .setSubtitle(getString(R.string.LO_DialogoBiometria_subtitulo))
            .setDescription(getString(R.string.LO_DialogoBiometria_mensaje))
            //.setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}