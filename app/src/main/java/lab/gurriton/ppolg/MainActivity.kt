package lab.gurriton.ppolg

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    fun requestIMEI(){
        val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val imei: TextView = findViewById(R.id.imei)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei.text = manager.deviceId
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions").setMessage("This app need some permissions").setCancelable(false)
                .setNegativeButton("ОK") { dialog, id ->
                    run {
                        dialog.cancel()
                    }
                }.setOnCancelListener { requestIMEI() }
        val alert = builder.create()
        alert.show()

        val version: TextView = findViewById(R.id.version)
        version.text = packageManager.getPackageInfo(packageName, 0).versionName
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode ) {
            0 -> {
                val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                val imei: TextView = findViewById(R.id.imei)
                if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    imei.text = manager.deviceId
                else
                    imei.text = "Unknown"
            }
        }
    }
}
