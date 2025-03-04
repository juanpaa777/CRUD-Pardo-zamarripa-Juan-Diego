package com.example.crud2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.URL

class MascotaAdapter(var mascotas: List<Mascota>, private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    class MascotaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreTextView)
        val especieTextView: TextView = view.findViewById(R.id.especieTextView)
        val razaTextView: TextView = view.findViewById(R.id.razaTextView)
        val edadTextView: TextView = view.findViewById(R.id.edadTextView)
        val vacunadaTextView: TextView = view.findViewById(R.id.vacunadaTextView)
        val imagenView: ImageView = view.findViewById(R.id.imagenView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotas[position]
        holder.nombreTextView.text = mascota.nombre
        holder.especieTextView.text = "Especie: ${mascota.especie}"
        holder.razaTextView.text = "Raza: ${mascota.raza}"
        holder.edadTextView.text = "Edad: ${mascota.edad} años"
        holder.vacunadaTextView.text = if (mascota.vacunada) "Vacunada: Sí" else "Vacunada: No"

        // Cargar la imagen
        if (mascota.imagen.startsWith("content://")) {
            // Si es una URI local, cargar desde el almacenamiento interno
            try {
                val inputStream = holder.itemView.context.contentResolver.openInputStream(Uri.parse(mascota.imagen))
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    holder.imagenView.setImageBitmap(bitmap)
                } else {
                    // Si no se pudo cargar la imagen, mostrar una imagen por defecto
                    holder.imagenView.setImageResource(R.drawable.ic_search)
                }
                inputStream?.close() // Cerrar el InputStream
            } catch (e: Exception) {
                e.printStackTrace()
                // Si ocurre un error, mostrar una imagen por defecto
                holder.imagenView.setImageResource(R.drawable.ic_search)
            }
        } else if (mascota.imagen.startsWith("http://") || mascota.imagen.startsWith("https://")) {
            // Si es un enlace, cargar desde la URL
            LoadImageTask(holder.imagenView).execute(mascota.imagen)
        } else {
            // Si no es una URI ni un enlace, mostrar una imagen por defecto
            holder.imagenView.setImageResource(R.drawable.ic_search)
        }

        holder.itemView.setOnClickListener { listener(mascota.id) }
    }

    override fun getItemCount() = mascotas.size

    private class LoadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            return try {
                val url = URL(params[0])
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageView.setImageBitmap(result)
            } else {
                // Si no se pudo cargar la imagen, mostrar una imagen por defecto
                imageView.setImageResource(R.drawable.ic_search)
            }
        }
    }
}