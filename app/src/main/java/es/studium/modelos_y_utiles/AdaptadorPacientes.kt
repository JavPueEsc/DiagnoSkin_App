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

class AdaptadorPacientes(
    var listaPacientes: MutableList<ModeloPaciente>
) : RecyclerView.Adapter<AdaptadorPacientes.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lbl_idPaciente : TextView =itemView.findViewById(R.id.PA_lbl_idPaciente_tarjeta)
        val lbl_apellidos_nombre : TextView =itemView.findViewById(R.id.PA_lbl_apellidos_nombre_tarjeta)
        val lbl_nuhsa : TextView =itemView.findViewById(R.id.PA_lbl_Nuhsa_tarjeta)
        val lbl_dni : TextView =itemView.findViewById(R.id.PA_lbl_dni_tarjeta)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorPacientes.MyViewHolder {
        var filaPaciente: View =
            LayoutInflater.from(parent.context).inflate(R.layout.pa_tarjeta_paciente, parent, false)
        return MyViewHolder(filaPaciente)
    }

    override fun onBindViewHolder(holder: AdaptadorPacientes.MyViewHolder, position: Int) {
        //obtener el diagnostico de la lista gracias al indice position
        var paciente: ModeloPaciente = listaPacientes[position]
        //Obtener los datos del diagnostico
        var idPaciente: String = paciente.idPaciente
        var nombrePaciente: String = paciente.nombrePaciente
        var apellidosPaciente: String = paciente.apellidosPaciente
        var nuhsaPaciente: String = paciente.nuhsaPaciente
        var dniPaciente: String = paciente.dniPaciente

        //Setear los datos en sus respectivos campos
        holder.lbl_idPaciente.text = "${holder.itemView.context.getString(R.string.PA_lbl_idPaciente_tarjeta)}$idPaciente"
        holder.lbl_apellidos_nombre.text = "${holder.itemView.context.getString(R.string.PA_lbl_apellidoNombre_tarjeta)} ${apellidosPaciente}, $nombrePaciente"
        holder.lbl_nuhsa.text = "${holder.itemView.context.getString(R.string.PA_lbl_nuhsa_tarjeta)} $nuhsaPaciente"
        holder.lbl_dni.text = "${holder.itemView.context.getString(R.string.PA_lbl_dni_tarjeta)} $dniPaciente"
    }

    override fun getItemCount(): Int {
        return listaPacientes.size
    }
}
