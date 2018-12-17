package lab.gurriton.ppolg

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_imei.*


class ImeiActivity : AppCompatActivity() {

    val READ_PHONE_STATE_REQUEST_CODE = 0

    fun requestIMEI(){
        val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val imei: TextView = findViewById(R.id.imei)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei.text = manager.deviceId
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permissions")
                    .setMessage("This page need permissions to show IMEI")
                    .setCancelable(false)
                    .setNegativeButton("Got it!") { dialog, _ -> dialog.cancel() }
                    .setOnCancelListener { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE) }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_imei)

        setSupportActionBar(findViewById(R.id.imei_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        imei_toolbar.setNavigationOnClickListener { super.onBackPressed() }
        supportActionBar?.title = "About"

        requestIMEI()
        val version: TextView = findViewById(R.id.version)
        version.text = BuildConfig.VERSION_NAME
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode ) {
            READ_PHONE_STATE_REQUEST_CODE -> {
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
