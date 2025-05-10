package es.studium.opcionpacientes

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.studium.diagnoskin_app.R
import es.studium.modelos_y_utiles.ModeloIA
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RealizarDiagnosticoActivity : AppCompatActivity() {
    //Declaración de las vistas
    private lateinit var camara : androidx.camera.view.PreviewView
    private lateinit var img_foto : ImageView
    private lateinit var btn_tomarFoto : Button
    private lateinit var btn_cargarFoto : Button
    private lateinit var btn_diagnosticar : Button

    //Variables para usar la cámara
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    //Variables para realizar el diagnóstico
    private lateinit var modeloIA: ModeloIA
    private lateinit var prediccion : String

    //Variable para permitir el poder cargar imágenes
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val photoView = findViewById<ImageView>(R.id.PA_XDIAG_fotoDiagnostico_RealizarDiagnosticos)
            photoView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pa_xdiag_activity_realizar_diagnostico)
        //Enlazar las vistas
        camara = findViewById(R.id.PA_XDIAG_camara_RealizarDiagnosticos)
        img_foto = findViewById(R.id.PA_XDIAG_fotoDiagnostico_RealizarDiagnosticos)
        btn_tomarFoto = findViewById(R.id.PA_XDIAG_btn_tomarFoto_RealizarDiagnostico)
        btn_cargarFoto = findViewById(R.id.PA_XDIAG_btn_cargarFoto_RealizarDiagnostico)
        btn_diagnosticar = findViewById(R.id.PA_XDIAG_btn_predecir_RealizarDiagnostico)

        //Inicializar el Modelo predictor
        modeloIA = ModeloIA(this)

        //Solicitar los permisos para poder usar la cámara
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
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
            // Obtener la URI de la imagen cargada en el ImageView
            val imageUri = getImageUriFromImageView()

            if (imageUri != null) {
                try {
                    // Realizar la predicción utilizando ModelPredictor
                    val (predictedClass, confidence) = modeloIA.predict(imageUri)
                    val message = "Predicción: $predictedClass (Confianza: %.2f%%)".format(confidence)
                    prediccion = predictedClass
                    // Mostrar el resultado en un Toast
                    if(confidence < 60){
                        Toast.makeText(this, R.string.PA_XDIAG_ImagenNoValida_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, R.string.PA_XDIAG_Exito_RealizarDiagnostico, Toast.LENGTH_SHORT).show()

                        //Intent a la siguiente Activity <-------------------------
                    }
                } catch (e: Exception) {
                    // En caso de error al procesar la imagen
                    Toast.makeText(this, R.string.PA_XDIAG_ErrorProcesarImagen_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error al procesar la imagen", e)
                }
            } else {
                Toast.makeText(this, R.string.PA_XDIAG_ErrorImagenVacia_RealizarDiagnostico, Toast.LENGTH_SHORT).show()
            }

        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //Predicción (3)
    private fun getImageUriFromImageView(): Uri? {
        // Obtener la URI de la imagen mostrada en el ImageView
        return (img_foto.drawable as? BitmapDrawable)?.let {
            val bitmap = it.bitmap
            val tempUri = getImageUri(bitmap)
            tempUri
        }
    }
    //Predicción (3)
    private fun getImageUri(bitmap: Bitmap): Uri? {
        // Convertir el bitmap a URI (guardar en almacenamiento temporal)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "temp_image", null)
        return Uri.parse(path)
    }

    // Función para redimensionar la imagen (4)
    private fun resizeImage(image: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(image, 224, 224, true)
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    // Mostrar la imagen en el ImageView
                    savedUri?.let {
                        img_foto.setImageURI(it)
                    }
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
}