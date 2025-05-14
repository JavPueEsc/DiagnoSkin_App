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

class AdaptadorMedicos(
    var listaMedicos: MutableList<ModeloMedico>
) : RecyclerView.Adapter<AdaptadorMedicos.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lbl_idMedico : TextView =itemView.findViewById(R.id.ADMIN_lbl_id_tarjetaMedico)
        val lbl_apellidos_nombre : TextView =itemView.findViewById(R.id.ADMIN_lbl_apellidosNombre_tarjetaMedico)
        val lbl_numColegiado : TextView =itemView.findViewById(R.id.ADMIN_lbl_numColegiado_tarjetaMedico)
        val lbl_especialidad : TextView =itemView.findViewById(R.id.ADMIN_lbl_especialidad_tarjetaMedico)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorMedicos.MyViewHolder {
        var filaMedico: View =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_tarjeta_medico, parent, false)
        return MyViewHolder(filaMedico)
    }

    override fun onBindViewHolder(holder: AdaptadorMedicos.MyViewHolder, position: Int) {
        //obtener el medico de la lista gracias al indice position
        var medico: ModeloMedico = listaMedicos[position]
        //Obtener los datos del diagnostico
        var idMedico: String = medico.idMedico
        var nombreMedico: String = medico.nombreMedico
        var apellidosMedico: String = medico.apellidosMedico
        var numColegiadoMedico: String = medico.numColegiadoMedico
        var especialidadMedico: String = medico.especialidadMedico

        //Setear los datos en sus respectivos campos
        holder.lbl_idMedico.text = holder.itemView.context.getString(R.string.ADMIN_lbl_id_tarjetaMedico, idMedico)
        holder.lbl_apellidos_nombre.text = holder.itemView.context.getString(R.string.ADMIN_lbl_apellidosNombre_tarjetaMedico, apellidosMedico, nombreMedico)
        holder.lbl_numColegiado.text = holder.itemView.context.getString(R.string.ADMIN_lbl_numColegiado_tarjetaMedico, numColegiadoMedico)
        holder.lbl_especialidad.text = holder.itemView.context.getString(R.string.ADMIN_lbl_especialidad_tarjetaMedico, especialidadMedico)
    }

    override fun getItemCount(): Int {
        return listaMedicos.size
    }
}
