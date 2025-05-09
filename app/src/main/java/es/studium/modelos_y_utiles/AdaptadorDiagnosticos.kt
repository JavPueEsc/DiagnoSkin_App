package es.studium.modelos_y_utiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import es.studium.diagnoskin_app.R

class AdaptadorDiagnosticos(
    var listaDiagnosticos: MutableList<ModeloDiagnostico>
) : RecyclerView.Adapter<AdaptadorDiagnosticos.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_diagnostico :ImageView=itemView.findViewById(R.id.PA_XDIA_img_fotoDiagnostico)
        val lbl_idDiagnostico : TextView =itemView.findViewById(R.id.PA_XDIA_lbl_idDiagnostico_tarjeta)
        val lbl_diagnosticoDiagnostico : TextView =itemView.findViewById(R.id.PA_XDIA_lbl_diagnosticoDiagnostico_tarjeta)
        val lbl_tipoDiagnostico : TextView =itemView.findViewById(R.id.PA_XDIA_lbl_pronosticoDiagnostico_tarjeta)
        val lbl_fechaDiagnostico : TextView =itemView.findViewById(R.id.PA_XDIA_lbl_fechaDiagnostico_tarjeta)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorDiagnosticos.MyViewHolder {
        var filaDiagnostico: View =
            LayoutInflater.from(parent.context).inflate(R.layout.pa_xdiag_tarjeta_diagnostico, parent, false)
        return MyViewHolder(filaDiagnostico)
    }

    override fun onBindViewHolder(holder: AdaptadorDiagnosticos.MyViewHolder, position: Int) {
        //obtener el diagnostico de la lista gracias al indice position
        var diagnostico: ModeloDiagnostico = listaDiagnosticos[position]
        //Obtener los datos del diagnostico
        var fotoDiagnostico: ByteArray = diagnostico.fotoDiagnostico
        var idDiagnostico: String = diagnostico.idDiagnostico
        var diagnosticoDiagnostico: String = diagnostico.diagnosticoDiagnostico
        var tipoDiagnostico: String = diagnostico.gravedadDiagnostico
        var fechaDiagnostico: String = diagnostico.fechaDiagnostico

        //Setear los datos en sus respectivos campos
        //Pasar imagen de ByteArray a Bitmap para poder mostrarla
        val imagenBitmap = byteArrayABitmap(fotoDiagnostico)
        if (imagenBitmap != null) {
            holder.img_diagnostico.setImageBitmap(imagenBitmap)
        } else {
            holder.img_diagnostico.setImageResource(R.drawable.imagen_por_defecto) // opcional
        }
        holder.lbl_idDiagnostico.text = "${holder.itemView.context.getString(R.string.PA_XDIA_lbl_idDiagnostico_tarjeta)}$idDiagnostico"
        holder.lbl_diagnosticoDiagnostico.text = "${holder.itemView.context.getString(R.string.PA_XDIA_lbl_diagnosticoDiagnostico_tarjeta)} ${diagnosticoDiagnostico}"
        holder.lbl_tipoDiagnostico.text = "${holder.itemView.context.getString(R.string.PA_XDIA_lbl_pronosticoDiagnostico_tarjeta)} $tipoDiagnostico"
        holder.lbl_fechaDiagnostico.text = "${holder.itemView.context.getString(R.string.PA_XDIA_lbl_fechaDiagnostico_tarjeta)} $fechaDiagnostico"
    }

    override fun getItemCount(): Int {
        return listaDiagnosticos.size
    }

    fun byteArrayABitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if (bitmap != null) {
                Log.d("CONVERSION_IMAGEN", "Conversión a Bitmap exitosa. Tamaño: ${byteArray.size} bytes")
            } else {
                Log.e("CONVERSION_IMAGEN", "Fallo en la conversión a Bitmap. ByteArray no válido.")
            }
            bitmap
        } else {
            Log.w("CONVERSION_IMAGEN", "ByteArray es null. No se puede convertir.")
            null
        }
    }
}
