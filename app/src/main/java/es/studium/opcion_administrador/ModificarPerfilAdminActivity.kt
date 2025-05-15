package es.studium.opcion_administrador

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import es.studium.diagnoskin_app.R
import es.studium.opcion_perfil.DatosDelMedicoActivity
import es.studium.operacionesbd_centrosmedicos.ConsultaRemotaCentrosMedicos
import es.studium.operacionesbd_medicos.ModificacionRemotaMedicos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ModificarPerfilAdminActivity : AppCompatActivity() {
    //Declaracion de las vistas
    private lateinit var lbl_id : TextView
    private lateinit var lbl_nombre : TextView
    private lateinit var lbl_apellido : TextView
    private lateinit var lbl_telefono : TextView
    private lateinit var lbl_emailMedico : TextView
    private lateinit var lbl_especialidad : TextView
    private lateinit var lbl_numColegiado : TextView
    private lateinit var lbl_nombreCentro : TextView
    private lateinit var btn_esAdminMedico : SwitchCompat
    private lateinit var btn_bloqueo : SwitchCompat
    private lateinit var btn_aceptar : Button

    //Declaracion de variables para recibir los extras
    private lateinit var idMedicoRecibido : String
    private lateinit var nombreMedicoRecibido: String
    private lateinit var apellidosMedicoRecibido: String
    private lateinit var telefonoMedicoRecibido: String
    private lateinit var emailMedicoRecibido: String
    private lateinit var especialidadmedicoRecibida: String
    private lateinit var numColegiadoMedicoRecibido: String
    private lateinit var esAdminMedicoRecibido: String
    private lateinit var idCentroMedicoFKRecibido: String
    private lateinit var idUsuarioFKRecibido: String
    private lateinit var esAdminMedico_AdminRecibido: String
    private lateinit var idMedicoAdmin_Recibido : String
    private lateinit var idUsuario_AdminRecibido: String

    //Declaracion elementos para consulta de centros Médicos
    private lateinit var result: JSONArray
    private lateinit var jsonObject: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity_modificar_perfil_admin)
        //Recibir los extras
        val extras= intent.extras
        if(extras!=null){
            procesarExtras(extras)
        }

        //Enlazar las vistas
        lbl_id = findViewById(R.id.ADMIN_lbl_titulo_ModificarPerfilAdmin)
        lbl_nombre = findViewById(R.id.ADMIN_lbl_nombre_ModificarPerfilAdmin)
        lbl_apellido = findViewById(R.id.ADMIN_lbl_apellidos_ModificarPerfilAdmin)
        lbl_telefono = findViewById(R.id.ADMIN_lbl_telefono_ModificarPerfilAdmin)
        lbl_emailMedico = findViewById(R.id.ADMIN_lbl_email_ModificarPerfilAdmin)
        lbl_especialidad = findViewById(R.id.ADMIN_lbl_especialidad_ModificarPerfilAdmin)
        lbl_numColegiado = findViewById(R.id.ADMIN_lbl_numColegiado_ModificarPerfilAdmin)
        lbl_nombreCentro = findViewById(R.id.ADMIN_lbl_centroTrabajo_ModificarPerfilAdmin)
        btn_esAdminMedico = findViewById(R.id.ADMIN_btn_administrador_ModificarPerfilAdmin)
        btn_aceptar = findViewById(R.id.ADMIN_btn_aceptar_ModificarPerfilAdmin)
        btn_bloqueo = findViewById(R.id.ADMIN_btn_bloquearUsuario_ModificarPerfilAdmin)

        //Setear los campos con los datos del médico
        lbl_id.text = getString(R.string.ADMIN_lbl_titulo_ModificarPerfilAdmin,idMedicoRecibido)
        lbl_nombre.text = getString(R.string.ADMIN_lbl_nombre_ModificarPerfilAdmin, nombreMedicoRecibido)
        lbl_apellido.text = getString(R.string.ADMIN_lbl_apellidos_ModificarPerfilAdmin,apellidosMedicoRecibido)
        lbl_telefono.text = getString(R.string.ADMIN_lbl_telefono_ModificarPerfilAdmin,telefonoMedicoRecibido)
        lbl_emailMedico.text = getString(R.string.ADMIN_lbl_email_ModificarPerfilAdmin,emailMedicoRecibido)
        lbl_especialidad.text = getString(R.string.ADMIN_lbl_especialidad_ModificarPerfilAdmin, especialidadmedicoRecibida)
        lbl_numColegiado.text = getString(R.string.ADMIN_lbl_numColegiado_ModificarPerfilAdmin, numColegiadoMedicoRecibido)
        lbl_nombreCentro.text = getString(R.string.ADMIN_lbl_centroTrabajo_ModificarPerfilAdmin,consultaCentroMedico(idCentroMedicoFKRecibido))
        if(esAdminMedicoRecibido.toInt()==2){
            btn_bloqueo.isChecked=true
            btn_esAdminMedico.visibility=View.GONE
        }
        else{
            if(esAdminMedicoRecibido.toInt()==0){
                btn_esAdminMedico.isChecked = false
            }
            else{
                btn_esAdminMedico.isChecked = true
            }
        }
        //gestion del botón de bloqueo
        btn_bloqueo.setOnClickListener{
            if(btn_bloqueo.isChecked){
                btn_esAdminMedico.visibility = View.GONE
            }
            else{
                btn_esAdminMedico.visibility = View.VISIBLE
            }
        }

        //Gestion boton Aceptar
        btn_aceptar.setOnClickListener {
            if(idMedicoRecibido == idMedicoAdmin_Recibido){
                Toast.makeText(this@ModificarPerfilAdminActivity,R.string.ADMIN_toastErrorMismoUsuario_ModificarPerfilAdmin, Toast.LENGTH_SHORT).show()
                Toast.makeText(this@ModificarPerfilAdminActivity,R.string.ADMIN_toastConsultaOtroAdmin_ModificarPerfilAdmin, Toast.LENGTH_SHORT).show()
                enviarIntentVuelta(PrincipalMedicosActivity::class.java,"ModificarPerfilAdmin",idUsuario_AdminRecibido,idMedicoAdmin_Recibido, esAdminMedico_AdminRecibido)
            }
            else{
                var idMedicoModificado = idMedicoRecibido
                var nombreMedicoModificado = nombreMedicoRecibido
                var apellidosModificado = apellidosMedicoRecibido
                var telefonoMedicoModificado = telefonoMedicoRecibido
                var emailMedicoModificado = emailMedicoRecibido
                var especialidadMedicoModificado = especialidadmedicoRecibida
                var numColegiadoMedicoModificado = numColegiadoMedicoRecibido
                var esAdminMedicoModificado = ""
                if(btn_bloqueo.isChecked){
                    esAdminMedicoModificado="2"
                }
                else{
                    if(btn_esAdminMedico.isChecked){
                        esAdminMedicoModificado="1"
                    }
                    else{
                        esAdminMedicoModificado="0"
                    }
                }
                var idCentroMedicoFKModificado = idCentroMedicoFKRecibido
                var idUsuarioKFModificado = idUsuarioFKRecibido

                // Realizar la actualización eb bbdd
                val modificarcionMedico = ModificacionRemotaMedicos()
                // Como ModificacionRemotaMedicos es suspend, se necesita una corrutina
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = modificarcionMedico.modificarMedico(
                        idMedicoModificado,
                        nombreMedicoModificado,
                        apellidosModificado,
                        telefonoMedicoModificado,
                        emailMedicoModificado,
                        especialidadMedicoModificado,
                        numColegiadoMedicoModificado,
                        esAdminMedicoModificado,
                        idCentroMedicoFKModificado,
                        idUsuarioKFModificado
                    )
                    //Para poder usar un toast en la rutina hay que usar este bloque
                    runOnUiThread {
                        if (resultado) {
                            Toast.makeText(
                                this@ModificarPerfilAdminActivity, R.string.ADMIN_toastExito_ModificarPerfilAdmin, Toast.LENGTH_SHORT).show()
                            enviarIntentVuelta(PrincipalMedicosActivity::class.java,"ModificarPerfilAdmin",idUsuario_AdminRecibido,idMedicoAdmin_Recibido, esAdminMedico_AdminRecibido)
                        } else {
                            Toast.makeText(
                                this@ModificarPerfilAdminActivity,
                                R.string.PA_toastErrorModificacion_ModificarPaciente,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    fun consultaCentroMedico(idCentroMedico: String?): String {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var nombreCentroMedicoBD: String =""
        var consultaRemotaCentrosMedicos = ConsultaRemotaCentrosMedicos()
        result = consultaRemotaCentrosMedicos.obtenerCentroMedicoPorId(idCentroMedico)
        //Verificamos que result no está vacío
        try {
            if (result.length() > 0) {
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    var idCentroMedicoBD = jsonObject.getString("idCentroMedico")
                    nombreCentroMedicoBD = jsonObject.getString("nombreCentroMedico")
                }
            } else {
                Log.e("ModificarMedicoActivity", "El JSONObject de centro medico está vacío")
            }
        } catch (e: JSONException) {
            Log.e("ModificarMedicoActivity", "Error al procesar el JSON", e)
        }
        return nombreCentroMedicoBD
    }

    //Procesar los extras recibido
    fun procesarExtras(extras : Bundle){
        idMedicoRecibido = extras.getString("idMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        nombreMedicoRecibido = extras.getString("nombreMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        apellidosMedicoRecibido = extras.getString("apellidosMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        telefonoMedicoRecibido = extras.getString("telefonoMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        emailMedicoRecibido= extras.getString("emailMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        especialidadmedicoRecibida= extras.getString("especialidadMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        numColegiadoMedicoRecibido= extras.getString("numColegiadoMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        esAdminMedicoRecibido= extras.getString("esAdminMedico")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idCentroMedicoFKRecibido= extras.getString("idCentroMedicoFK")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuarioFKRecibido= extras.getString("idUsuarioFK")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        esAdminMedico_AdminRecibido= extras.getString("esAdminMedicoRecibido")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idMedicoAdmin_Recibido = extras.getString("idMedicoRecibido")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
        idUsuario_AdminRecibido = extras.getString("idUsuarioRecibido")
            ?: getString(R.string.LO_ErrorExtraNoRecibido)
    }
    //Gestión de la pulsación del triangulo (barra navegación Android)
    override fun onBackPressed() {
        super.onBackPressed()
        // Pulsa el botón volver
        enviarIntentVuelta(PrincipalMedicosActivity::class.java,"ModificarPerfilAdmin",idUsuario_AdminRecibido,idMedicoAdmin_Recibido, esAdminMedico_AdminRecibido)
    }

    //Enviar intent de vuelta
    fun enviarIntentVuelta( actividadDestino: Class<out Activity>, claveOrigen : String, idUsuario:String?, idMedico:String, esAminMedico: String){
        val intent = Intent(this@ModificarPerfilAdminActivity, actividadDestino)
        intent.putExtra(claveOrigen, claveOrigen)
        intent.putExtra("esAdminMedico", esAminMedico)
        intent.putExtra("idMedico", idMedico)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
    }
}