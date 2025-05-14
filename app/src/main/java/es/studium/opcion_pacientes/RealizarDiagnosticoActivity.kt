package es.studium.opcion_pacientes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import es.studium.diagnoskin_app.MainActivity
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.ModeloIA
import es.studium.opcion_diagnosticos.PrincipalPacientes2Activity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RealizarDiagnosticoActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var camara : androidx.camera.view.PreviewView
    private lateinit var img_foto : ImageView
    private lateinit var btn_tomarFoto : Button
    private lateinit var btn_cargarFoto : Button
    private lateinit var btn_diagnosticar : Button
    private lateinit var btn_volver : ImageView

    //Variables para usar la cámara
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    //Variables para realizar el diagnóstico
    private lateinit var modeloIA: ModeloIA
    private lateinit var prediccion : String
    private lateinit var tipoLesion : String

    //Variable para permitir el poder cargar imágenes
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val photoView = findViewById<ImageView>(R.id.PA_XDIAG_fotoDiagnostico_RealizarDiagnosticos)
            photoView.setImageURI(it)
        }
    }
    //Variable para extra recibido
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
        setContentView(R.layout.pa_xdiag_activity_realizar_diagnostico)
        //Recibir EXTRA con los datos del usuario médico y paciente
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("origenPrincipalDiagnosticosActivity")) {
                procesarExtras(extras)
            } else if (extras.containsKey("PrincipalPacientes2Activity")){
                procesarExtras(extras)
            }
        }
        //Enlazar las vistas
        camara = findViewById(R.id.PA_XDIAG_camara_RealizarDiagnosticos)
        img_foto = findViewById(R.id.PA_XDIAG_fotoDiagnostico_RealizarDiagnosticos)
        btn_tomarFoto = findViewById(R.id.PA_XDIAG_btn_tomarFoto_RealizarDiagnostico)
        btn_cargarFoto = findViewById(R.id.PA_XDIAG_btn_cargarFoto_RealizarDiagnostico)
        btn_diagnosticar = findViewById(R.id.PA_XDIAG_btn_predecir_RealizarDiagnostico)
        btn_volver = findViewById(R.id.btnVolver_RealizarDiagnosticos)
        btn_volver.visibility = View.GONE

        //Inicializar el Modelo predictor
        modeloIA = ModeloIA(this)

        //Solicitar los permisos para poder usar la cámara
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //Gestión del boton volver
        btn_volver.setOnClickListener{
            if (extras != null) {
                if (extras.containsKey("origenPrincipalDiagnosticosActivity")) {
                    enviarIntentVuelta(PrincipalDiagnosticosActivity::class.java,"origenPrincipalDiagnosticosActivity",idPacienteRecibido,nombrePacienteRecibido,apellidosPacienteRecibido,
                        sexoPacienteRecibido,fechaNacPacienteRecibido,nuhsaPacienteRecibido,telefonoPacienteRecibido,
                        emailPacienteRecibido,dniPacienteRecibido,direccionPacienteRecibido,localidadPacienteRecibido,provinciaPacienteRecibido,codigoPostalPacienteRecibido,
                        esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
                } else if (extras.containsKey("PrincipalPacientes2Activity")) {
                    enviarIntentVuelta(PrincipalPacientes2Activity::class.java,"OrigenBtnDiagnosticos",idPacienteRecibido,nombrePacienteRecibido,apellidosPacienteRecibido,
                        sexoPacienteRecibido,fechaNacPacienteRecibido,nuhsaPacienteRecibido,telefonoPacienteRecibido,
                        emailPacienteRecibido,dniPacienteRecibido,direccionPacienteRecibido,localidadPacienteRecibido,provinciaPacienteRecibido,codigoPostalPacienteRecibido,
                        esAdminMedicoRecibido,idMedicoRecibido,idUsuarioRecibido)
                }
            }

        }
        //Gestión del botón tomar foto
        btn_tomarFoto.setOnClickListener {
            takePhoto()
        }
        //Gestión del botón cargar una foto
        btn_cargarFoto.setOnClickListener {

            pickImageLauncher.launch("image/*")
        }
        //Gestión del botón predecir
        btn_diagnosticar.setOnClickListener {
            try {
                // Obtener la URI de la imagen cargada en el ImageView
                val imageUri = getImageUriFromImageView()
                if (imageUri != null) {
                    try {
                        // Realizar la predicción utilizando ModelPredictor
                        val (predictedClass, confidence) = modeloIA.predict(imageUri)
                        val message =
                            "Predicción: $predictedClass (Confianza: %.2f%%)".format(confidence)
                        prediccion = predictedClass
                        // Mostrar el resultado en un Toast
                        if (confidence < 60) {
                            Toast.makeText(this, R.string.PA_XDIAG_ImagenNoValida_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, R.string.PA_XDIAG_Exito_RealizarDiagnostico, Toast.LENGTH_SHORT).show()

                            var fechaActual = LocalDate.now().toString()
                            var uriImagen = imageUri.toString()
                            if (prediccion == getString(R.string.ModeloIA_Melanoma)) {
                                tipoLesion = getString(R.string.PA_XDIA_Maligna)
                            } else {
                                tipoLesion = getString(R.string.PA_XDIA_Benigna)
                            }
                            if (extras != null) {
                                if (extras.containsKey("origenPrincipalDiagnosticosActivity")) {
                                    enviarIntentSiguiente(
                                        ResumenDiagnosticoActivity::class.java, "origenPrincipalDiagnosticosActivity", idPacienteRecibido, nombrePacienteRecibido, apellidosPacienteRecibido,
                                        sexoPacienteRecibido, fechaNacPacienteRecibido, nuhsaPacienteRecibido, telefonoPacienteRecibido, emailPacienteRecibido, dniPacienteRecibido, direccionPacienteRecibido,
                                        localidadPacienteRecibido, provinciaPacienteRecibido, codigoPostalPacienteRecibido, esAdminMedicoRecibido, idMedicoRecibido, idUsuarioRecibido, fechaActual,
                                        prediccion, tipoLesion, uriImagen
                                    )
                                } else {
                                    enviarIntentSiguiente(
                                        ResumenDiagnosticoActivity::class.java, "PrincipalPacientes2Activity", idPacienteRecibido, nombrePacienteRecibido, apellidosPacienteRecibido,
                                        sexoPacienteRecibido, fechaNacPacienteRecibido, nuhsaPacienteRecibido, telefonoPacienteRecibido, emailPacienteRecibido, dniPacienteRecibido, direccionPacienteRecibido,
                                        localidadPacienteRecibido, provinciaPacienteRecibido, codigoPostalPacienteRecibido, esAdminMedicoRecibido, idMedicoRecibido, idUsuarioRecibido, fechaActual,
                                        prediccion, tipoLesion, uriImagen
                                    )
                                }
                            }

                        }
                    } catch (e: Exception) {
                        // En caso de error al procesar la imagen
                        Toast.makeText(this, R.string.PA_XDIAG_ErrorProcesarImagen_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error al procesar la imagen", e)
                    }
                } else {
                    Toast.makeText(
                        this, R.string.PA_XDIAG_ErrorImagenVacia_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: ClassCastException) {
                Toast.makeText(this,  R.string.PA_XDIAG_ErrorImagenVacia_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error al obtener la URI de la imagen", e)
            }

        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //Predicción (3)
    private fun getImageUriFromImageView(): Uri? {
        val photoView = findViewById<ImageView>(R.id.PA_XDIAG_fotoDiagnostico_RealizarDiagnosticos)
        val bitmap = (photoView.drawable as BitmapDrawable).bitmap

        val file = File(externalCacheDir, "image.jpg") // Cambia el nombre si es necesario
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            return FileProvider.getUriForFile(this, "es.studium.modelos_y_utiles.fileprovider", file)
        } catch (e: Exception) {
            Log.e("RealizarDiagnosticoActivity", "Error al guardar la imagen", e)
            return null
        }
    }
    //Predicción (3)
    private fun getImageUri(bitmap: Bitmap): Uri? {
        val file = File(applicationContext.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return FileProvider.getUriForFile(
                applicationContext,
                "com.example.myapp.fileprovider", // Asegúrate de que tu FileProvider esté configurado en el manifiesto
                file
            )
        } catch (e: IOException) {
            Log.e("getImageUri", "Error al guardar la imagen en el archivo temporal", e)
            return null
        }
    }


    // Función para redimensionar la imagen (4)
    private fun resizeImage(image: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(image, 224, 224, true)
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Ruta fija donde se guardará la imagen (si existe, se sobrescribe)
        val photoFile = File(
            getExternalFilesDir(null),
            "captura.jpg" // siempre se sobrescribe este archivo
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    // Mostrar la imagen en el ImageView
                    img_foto.setImageURI(savedUri)
                }
            }
        )
    }


    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(camara.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    //Enviar intent de vuelta (a PrincipalDiagnosticosActivity)
    private fun enviarIntentVuelta(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?
    ) {
        val intent = Intent(this@RealizarDiagnosticoActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("idPaciente", idPaciente)
        intent.putExtra("nombrePaciente", nombre)
        intent.putExtra("apellidosPaciente", apellidos)
        intent.putExtra("sexoPaciente", sexo)
        intent.putExtra("fechaNacPaciente", fechaNac)
        intent.putExtra("nuhsaPaciente", nuhsa)
        intent.putExtra("telefonoPaciente", telefono)
        intent.putExtra("emailPaciente", email)
        intent.putExtra("dniPaciente", dni)
        intent.putExtra("direccionPaciente", direccion)
        intent.putExtra("localidadPaciente", localidad)
        intent.putExtra("provinciaPaciente", provincia)
        intent.putExtra("codigoPostalPaciente", codigoPostal)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }

    //Enviar intent a siguiente activity (a ResumenDiagnosticosActivity)
    private fun enviarIntentSiguiente(
        activityDestino: Class<out Activity>, claveOrigen: String, idPaciente: String?, nombre: String?, apellidos: String?, sexo: String?, fechaNac: String?,
        nuhsa: String?, telefono: String?, email: String?, dni: String?, direccion: String?, localidad: String?, provincia: String?, codigoPostal: String?,
        esAdminMedico: String?, idMedico: String?, idUsuario: String?, fechaDiagnostico: String, diagnostico: String, tipoDiagnostico : String, fotoDiagnostico : String
    ) {
        val intent = Intent(this@RealizarDiagnosticoActivity, activityDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("idPaciente", idPaciente)
        intent.putExtra("nombrePaciente", nombre)
        intent.putExtra("apellidosPaciente", apellidos)
        intent.putExtra("sexoPaciente", sexo)
        intent.putExtra("fechaNacPaciente", fechaNac)
        intent.putExtra("nuhsaPaciente", nuhsa)
        intent.putExtra("telefonoPaciente", telefono)
        intent.putExtra("emailPaciente", email)
        intent.putExtra("dniPaciente", dni)
        intent.putExtra("direccionPaciente", direccion)
        intent.putExtra("localidadPaciente", localidad)
        intent.putExtra("provinciaPaciente", provincia)
        intent.putExtra("codigoPostalPaciente", codigoPostal)
        intent.putExtra("esAdminMedico", esAdminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        intent.putExtra("fechaDiagnostico", fechaDiagnostico)
        intent.putExtra("diagnosticoDiagnostico", diagnostico)
        intent.putExtra("tipoDiagnostico", tipoDiagnostico)
        intent.putExtra("fotoDiagnostico", fotoDiagnostico)
        startActivity(intent)
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

    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        btn_volver.performClick()
    }
}