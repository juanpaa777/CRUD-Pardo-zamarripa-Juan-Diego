package com.example.crud2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MascotaAdapter
    private lateinit var searchView: SearchView
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columnas

        searchView = findViewById(R.id.searchView)
        categorySpinner = findViewById(R.id.categorySpinner)

        // Configurar el Spinner con categorías
        val categories = arrayOf("Todas", "Gatos", "Perros")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapterSpinner

        val registrarButton: Button = findViewById(R.id.registrarButton)
        registrarButton.setOnClickListener {
            val intent = Intent(this, RegistroMascotaActivity::class.java)
            startActivity(intent)
        }

        // Configurar el buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMascotas(newText, categorySpinner.selectedItem.toString())
                return true
            }
        })

        // Configurar el Spinner para filtrar por categoría
        categorySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterMascotas(searchView.query.toString(), categorySpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista de mascotas cada vez que la actividad vuelve a estar en primer plano
        loadMascotas()
    }

    private fun loadMascotas() {
        val mascotas = databaseHelper.getAllMascotas()
        adapter = MascotaAdapter(mascotas) { mascotaId ->
            val intent = Intent(this, DetalleMascotaActivity::class.java)
            intent.putExtra("MASCOTA_ID", mascotaId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun filterMascotas(query: String?, category: String) {
        val filteredList = databaseHelper.getAllMascotas().filter { mascota ->
            // Verificar si el nombre coincide con la consulta
            val matchesQuery = query.isNullOrEmpty() || mascota.nombre.contains(query, ignoreCase = true)

            // Verificar si la especie coincide con la categoría seleccionada
            val matchesCategory = category == "Todas" || mascota.especie.equals(category.dropLast(1), ignoreCase = true)

            // Retornar verdadero si ambas condiciones se cumplen
            matchesQuery && matchesCategory
        }
        adapter.mascotas = filteredList
        adapter.notifyDataSetChanged()
    }
}