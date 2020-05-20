package mx.edu.ittepic.ladm_u4_practica_1_braylosky_ramirez_fletes_20_xx

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var baseDatos = "practica6"
    var pHilo : Automatica ?= null

    var request = 101
    /*var cols = listOf<String>(CallLog.Calls._ID,
                              CallLog.Calls.NUMBER,
                              CallLog.Calls.TYPE,
                              CallLog.Calls.DURATION).toTypedArray()*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pHilo = Automatica(this)
        pHilo?.start()


        save.setOnClickListener {
            if(numero.text.isEmpty() || nombre.text.isEmpty()){
                AlertDialog.Builder(this).setMessage("CAMPOS VACIOS").show()
                return@setOnClickListener
            }
            insertarContacto(nombre.text.toString(), numero.text.toString())
            insertarSms(cuerpo.text.toString())
            limpiar()
        }
        var cols = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CALL_LOG)
        var sms = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)
        var rst = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)

        if(cols != PackageManager.PERMISSION_GRANTED ||
            sms != PackageManager.PERMISSION_GRANTED ||
            rst != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.SEND_SMS),request)
        }

        hayPlantillas()
        cargarLista()



        /*if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1){android.Manifest.permission.READ_CALL_LOG},101)
        }else{
            displayLog()
        }*/
    }
   fun limpiar(){
        nombre.setText("")
       numero.setText("")
       cuerpo.setText("")
       radioButton.isChecked == false

    }

    fun insertarSms(body : String) {
        try {
            var baseDatos = Tablas(this, baseDatos, null, 1)
            var insertar = baseDatos.writableDatabase
            var SQL = "UPDATE SMS SET TEXTO ='${body}' WHERE ID=?"
            var spam = arrayOf(1)
            var agradable = arrayOf(2)


            if(radioButton.isChecked == true){
                insertar.execSQL(SQL, spam)
            }else if(radioButton.isChecked == false) {
                insertar.execSQL(SQL, agradable)
            }

            insertar.close()
            baseDatos.close()

            AlertDialog.Builder(this).setMessage("MENSAJE PERSONALIZADO CARGADO ")
        } catch (e : SQLiteException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun insertarContacto(nom: String, num: String) {
        var baseDatos = Tablas(this, baseDatos, null, 1)
        var insertar = baseDatos.writableDatabase
        var SQL1 = "INSERT INTO CONTACTOS VALUES(NULL, '${nom}','${num}', '1')"
        var SQL2 = "INSERT INTO CONTACTOS VALUES(NULL, '${nom}','${num}', '2')"

        if(radioButton.isChecked == true){
            insertar.execSQL(SQL1)

        }else if(radioButton.isChecked == false){
            insertar.execSQL(SQL2)
        }
        insertar.close()
        baseDatos.close()

        AlertDialog.Builder(this).setMessage("SE INSERTÓ CORRECTAMENTE EL CONTACTO de ${nom}").show()
    }

    fun spamAgradable(numero : String) : String {
        try {
            var baseDatos = Tablas(this, baseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM CONTACTOS"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                cursor.moveToFirst()
                var contador = cursor.count - 1

                (0..contador).forEach {
                    if(cursor.getString(2) == numero) {
                        if(cursor.getString(3) == "1") {
                            return "Spam"
                        } else if(cursor.getString(3) == "2") {
                            return "Agradable"
                        }
                    }
                    cursor.moveToNext()
                }
            }
            select.close()
            baseDatos.close()
        } catch (e : SQLiteException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
        return "Call_Log"
    }

    fun hayPlantillas(){
        try {
            var baseDatos = Tablas(this, baseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM SMS WHERE ID = 1"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.moveToFirst()){
            } else {
                //SE CREAN 2 MENSAJES PARA DESPUES REMPLAZARLOS CON LOS PERSONALIZADOS
                platillasSMS()
                platillasSMS()
            }

            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){ }
    }

    fun platillasSMS() {
        var baseDatos = Tablas(this, baseDatos, null, 1)
        var insertar = baseDatos.writableDatabase
        var SQL = "INSERT INTO SMS VALUES(NULL, '')"

        insertar.execSQL(SQL)
        insertar.close()
        baseDatos.close()
    }

    fun cargarSms(sms : Int) : String {

        try {
            var baseDatos = Tablas(this, baseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM SMS WHERE ID = ?"
            var content = arrayOf(sms.toString())
            var cursor = select.rawQuery(SQL, content)

            if(cursor.moveToFirst()){
                return cursor.getString(1)
            }
            select.close()
            baseDatos.close()
        } catch (error : SQLiteException){ }
        return "--"
    }

    fun enviarSMS() {
        try {

            var baseDatos = Tablas(this, baseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM CALL_LOG"
            var cursor = select.rawQuery(SQL, null)
            var contador = 0
            if(cursor.count > 0) {
                cursor.moveToFirst()
                var cantidad = cursor.count - 1

                (0..cantidad).forEach {
                    contador++
                    if(cursor.getString(2) == "false" && (contador % 2) == 0) {
                        if(spamAgradable(cursor.getString(1)) == "Spam") {
                            SmsManager.getDefault().sendTextMessage(cursor.getString(1),
                                null, cargarSms(1), null, null)

                        } else if(spamAgradable(cursor.getString(1)) == "Agradable") {

                            SmsManager.getDefault().sendTextMessage(cursor.getString(1),
                                null, cargarSms(2), null, null)
                        }
                    }
                    cursor.moveToNext()
                }
            }

            select.close()
            baseDatos.close()
        } catch (e : SQLiteException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
    fun cargarLista() {
        try {

            var baseDatos = Tablas(this, baseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM CALL_LOG"
            var cursor = select.rawQuery(SQL, null)
            var contador = 0
            if(cursor.count > 0) {
                var content = ArrayList<String>()
                cursor.moveToFirst()
                var cantidad = cursor.count-1
                (0..cantidad).forEach {
                    contador++
                    if((contador % 2) == 0){
                        var data = "Numero Telefonico: ${cursor.getString(1)} \n Tipo de Llamada: ${spamAgradable(cursor.getString(1))}"
                        content.add(data)
                    }
                    cursor.moveToNext()
                }
                Lista.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, content)
            } else if(cursor.count == 0){
                var noHay = ArrayList<String>(); var data = "Lista Vacia - Sin Llamadas Recibidas"

                noHay.add(data)
                Lista.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,noHay)
            }

            select.close(); baseDatos.close()
        } catch (e : SQLiteException){
            AlertDialog.Builder(this).setMessage(e.toString())        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == request){
            cargarLista()
        }
    }





/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            displayLog()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayLog() {
        var from = listOf<String>(CallLog.Calls.NUMBER,
                                            CallLog.Calls.DURATION,
                                            CallLog.Calls.TYPE).toTypedArray()
        var rs = contentResolver.query(CallLog.Calls.CONTENT_URI,cols,null,null,
                                   "${CallLog.Calls.LAST_MODIFIED} DESC")


    }*/
}
