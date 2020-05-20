package mx.edu.ittepic.ladm_u4_practica_1_braylosky_ramirez_fletes_20_xx

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class Tablas(context : Context,
                nombreBD : String,
                cursorFactory: SQLiteDatabase.CursorFactory?,
                numeroVersion : Int) : SQLiteOpenHelper(context, nombreBD, cursorFactory, numeroVersion) {

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            /*
                 1 -> MENSAJE PARA PERSONAS AGRADABLES
                 2 -> MENSAJE PARA PERSONAS NO AGRADABLES

                 1 -> TELEFONOS DE PERSONAS AGRADABLES
                 2 -> TELEFONOS DE PERSONAS NO AGRADABLES
             */
            db?.execSQL("CREATE TABLE CALL_LOG(ID INTEGER PRIMARY KEY AUTOINCREMENT, TELEFONO VARCHAR(10), TIPO BOOLEAN)")
            db?.execSQL("CREATE TABLE CONTACTOS(ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR (40), TELEFONO VARCHAR(10), TIPO VARCHAR(1))")
            db?.execSQL("CREATE TABLE SMS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TEXTO VARCHAR(200))")


        } catch (error : SQLiteException){ }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}