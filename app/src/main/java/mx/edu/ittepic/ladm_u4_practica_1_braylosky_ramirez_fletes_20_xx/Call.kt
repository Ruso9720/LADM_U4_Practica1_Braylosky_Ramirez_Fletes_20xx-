package mx.edu.ittepic.ladm_u4_practica_1_braylosky_ramirez_fletes_20_xx

import android.Manifest.permission_group.CALL_LOG
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class Call : BroadcastReceiver() {
    val baseDatos = "practica6"
    var puntero : Context?= null
    var contador = 0
    var take = true


    override fun onReceive(context : Context, intent: Intent?) {
        try {
            puntero = context
            var tManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var telefono = MyPhoneStateListener()


            tManager.listen(telefono, PhoneStateListener.LISTEN_CALL_STATE)

        } catch (e: Exception) {
            Log.e("Error", "${e}")
        }
    }

    private inner class MyPhoneStateListener : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            Log.d("MyPhoneListener", "$state   incoming no:$incomingNumber")

            if(state == 2){
                take = false
            }

            if (take == true && state == 0 ) {
                var num = "$incomingNumber"
                Log.d("LLamadaPerdida", num)
                contador++
                try {
                    if(!num.isEmpty()) {
                        var baseDatos = Tablas (puntero!!, baseDatos, null, 1)
                        var insertar = baseDatos.writableDatabase
                        var SQL = "INSERT INTO CALL_LOG VALUES (NULL ,'${num}', 'false')"

                        insertar.execSQL(SQL)
                        baseDatos.close()
                        Log.d("Registro", "EXITO AL REGISTRAR" + contador)
                        AlertDialog.Builder(puntero).setMessage("LLAMADA PERDIDA REGISTRADA")

                    }
                } catch (e : Exception) {
                AlertDialog.Builder(puntero).setMessage(e.toString())
                }
            }
        }
    }
}