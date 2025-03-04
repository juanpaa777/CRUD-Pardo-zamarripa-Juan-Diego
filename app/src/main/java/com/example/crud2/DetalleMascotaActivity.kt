package com.example.crud2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.DatabaseHelper
import java.net.URL

class DetalleMascotaActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var mascotaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_mascota)

        databaseHelper = DatabaseHelper(this)

        // Obtener el ID de la mascota desde el intent
        mascotaId = intent.getIntExtra("MASCOTA_ID", -1)

        if (mascotaId != -1) {
            val mascota = databaseHelper.getMascotaById(mascotaId)
            if (mascota != null) {
                // Mostrar los detalles de la mascota
                findViewById<TextView>(R.id.detalleNombreTextView).text = "Nombre: ${mascota.nombre}"
                findViewById<TextView>(R.id.detalleEspecieTextView).text = "Especie: ${mascota.especie}"
                findViewById<TextView>(R.id.detalleRazaTextView).text = "Raza: ${mascota.raza}"
                findViewById<TextView>(R.id.detalleEdadTextView).text = "Edad: ${mascota.edad} años"
                findViewById<TextView>(R.id.detalleVacunadaTextView).text = if (mascota.vacunada) "Vacunada: Sí" else "Vacunada: No"

                // Cargar la imagen en grande
                LoadImageTask(findViewById(R.id.detalleImagenView)).execute(mascota.imagen)

                // Configurar botones
                findViewById<Button>(R.id.editarButton).setOnClickListener {
                    val intent = Intent(this, RegistroMascotaActivity::class.java)
                    intent.putExtra("MASCOTA_ID", mascotaId)
                    startActivity(intent)
                }

                findViewById<Button>(R.id.eliminarButton).setOnClickListener {
                    databaseHelper.deleteMascota(mascotaId)
                    Toast.makeText(this, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

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
            result?.let {
                imageView.setImageBitmap(it)
            }
        }
    }
}