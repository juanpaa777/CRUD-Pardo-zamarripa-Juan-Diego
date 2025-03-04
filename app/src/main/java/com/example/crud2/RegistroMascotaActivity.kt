package com.example.crud2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.DatabaseHelper

class RegistroMascotaActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var nombreEditText: EditText
    private lateinit var especieSpinner: Spinner
    private lateinit var razaEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var vacunadaCheckBox: CheckBox
    private lateinit var imagenEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var seleccionarImagenButton: Button

    private var mascotaId: Int = -1
    private var imagenUri: Uri? = null // Para almacenar la URI de la imagen seleccionada

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mascota)

        databaseHelper = DatabaseHelper(this)

        nombreEditText = findViewById(R.id.nombreEditText)
        especieSpinner = findViewById(R.id.especieSpinner)
        razaEditText = findViewById(R.id.razaEditText)
        edadEditText = findViewById(R.id.edadEditText)
        vacunadaCheckBox = findViewById(R.id.vacunadaCheckBox)
        imagenEditText = findViewById(R.id.imagenEditText)
        guardarButton = findViewById(R.id.guardarButton)
        seleccionarImagenButton = findViewById(R.id.seleccionarImagenButton)


        val especies = arrayOf("Gato", "Perro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, especies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        especieSpinner.adapter = adapter


        mascotaId = intent.getIntExtra("MASCOTA_ID", -1)
        if (mascotaId != -1) {
            cargarDatosMascota(mascotaId)
        }

       
        seleccionarImagenButton.setOnClickListener {
            abrirGaleria()
        }

        guardarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val especie = especieSpinner.selectedItem.toString()
            val raza = razaEditText.text.toString()
            val edad = edadEditText.text.toString().toIntOrNull() ?: 0
            val vacunada = vacunadaCheckBox.isChecked
            val imagen = if (imagenUri != null) {
                imagenUri.toString() // Guardar la URI de la imagen seleccionada
            } else {
                imagenEditText.text.toString() // Usar el enlace de imagen si no se seleccionó una imagen
            }

            if (nombre.isNotEmpty() && raza.isNotEmpty() && edad > 0) {
                if (mascotaId == -1) {
                    // Insertar nueva mascota
                    databaseHelper.insertMascota(nombre, especie, raza, edad, vacunada, imagen)
                    Toast.makeText(this, "Mascota registrada", Toast.LENGTH_SHORT).show()
                } else {
                    // Actualizar mascota existente
                    databaseHelper.updateMascota(mascotaId, nombre, especie, raza, edad, vacunada, imagen)
                    Toast.makeText(this, "Mascota actualizada", Toast.LENGTH_SHORT).show()
                }
                finish() // Cierra la actividad
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.data // Obtener la URI de la imagen seleccionada
            imagenEditText.setText(imagenUri.toString()) // Mostrar la URI en el EditText
        }
    }

    private fun cargarDatosMascota(id: Int) {
        val mascota = databaseHelper.getMascotaById(id)
        if (mascota != null) {
            nombreEditText.setText(mascota.nombre)
            especieSpinner.setSelection((especieSpinner.adapter as ArrayAdapter<String>).getPosition(mascota.especie))
            razaEditText.setText(mascota.raza)
            edadEditText.setText(mascota.edad.toString())
            vacunadaCheckBox.isChecked = mascota.vacunada
            imagenEditText.setText(mascota.imagen)
        }
    }
}