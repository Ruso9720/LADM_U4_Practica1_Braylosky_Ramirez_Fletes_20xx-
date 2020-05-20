package mx.edu.ittepic.ladm_u4_practica_1_braylosky_ramirez_fletes_20_xx

class Automatica (p:MainActivity) : Thread() {
    private var play = false
    private var p = p

    override fun run() {
        super.run()
        play = true
        while (play) {
            sleep(2000)
            p.runOnUiThread {
                p.enviarSMS()
            }
        }
    }
}