package com.example.crud

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.crud2.Mascota

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mascotas.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "mascotas"
        private const val COLUMN_ID = "id_mascota"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_ESPECIE = "especie"
        private const val COLUMN_RAZA = "raza"
        private const val COLUMN_EDAD = "edad"
        private const val COLUMN_VACUNADA = "vacunada"
        private const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT,
                $COLUMN_ESPECIE TEXT,
                $COLUMN_RAZA TEXT,
                $COLUMN_EDAD INTEGER,
                $COLUMN_VACUNADA INTEGER,
                $COLUMN_IMAGEN TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_IMAGEN TEXT")
        }
    }

    fun insertMascota(nombre: String, especie: String, raza: String, edad: Int, vacunada: Boolean, imagen: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_ESPECIE, especie)
            put(COLUMN_RAZA, raza)
            put(COLUMN_EDAD, edad)
            put(COLUMN_VACUNADA, if (vacunada) 1 else 0)
            put(COLUMN_IMAGEN, imagen)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllMascotas(): List<Mascota> {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val mascotas = mutableListOf<Mascota>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val especie = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIE))
                val raza = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAZA))
                val edad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD))
                val vacunada = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VACUNADA)) == 1
                val imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN))

                mascotas.add(Mascota(id, nombre, especie, raza, edad, vacunada, imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mascotas
    }
    fun getMascotaById(id: Int): Mascota? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_ID=?", arrayOf(id.toString()), null, null, null)
        return if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
            val especie = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIE))
            val raza = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAZA))
            val edad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD))
            val vacunada = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VACUNADA)) == 1
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN))
            Mascota(id, nombre, especie, raza, edad, vacunada, imagen)
        } else {
            null
        }.also { cursor.close() }
    }

    fun deleteMascota(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
    fun updateMascota(id: Int, nombre: String, especie: String, raza: String, edad: Int, vacunada: Boolean, imagen: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_ESPECIE, especie)
            put(COLUMN_RAZA, raza)
            put(COLUMN_EDAD, edad)
            put(COLUMN_VACUNADA, if (vacunada) 1 else 0)
            put(COLUMN_IMAGEN, imagen)
        }
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}