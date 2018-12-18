package lab.gurriton.ppolg

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import androidx.core.content.ContextCompat.getSystemService
import android.net.ConnectivityManager
import android.net.Network
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    lateinit var cm: ConnectivityManager
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        NavigationUI.setupWithNavController(nav_view, findNavController(R.id.nav_host))
        nav_view.getHeaderView(0).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            findNavController(R.id.nav_host).navigate(R.id.userPage)
        }
        nav_view.setNavigationItemSelectedListener(this)
        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected)
            network_icon.setImageResource(R.drawable.ic_network_bad)
        cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network){
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_active)}
            }
            override fun onUnavailable() {
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_bad)}
            }
            override fun onLosing(network: Network?, maxMsToLive: Int) {
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_bad)}
            }
            override fun onLost(network: Network){
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_bad)}
            }
        })

    }



    override fun onResume() {
        super.onResume()
        val header = nav_view.getHeaderView(0)
        val user = DBWork.GetUser()
        if (user == null) {
            nav_view.menu.setGroupVisible(R.id.menu_group, false)
            nav_view.menu.setGroupVisible(R.id.login_group, true)
            nav_view.menu.setGroupVisible(R.id.logout_group, false)
            header.isClickable = false
            header.findViewById<ImageView>(R.id.header_image).setImageResource(R.mipmap.ic_launcher_round)
            header.findViewById<TextView>(R.id.header_email).text = "Unauthorized"
        }
        else {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage("Loading")
            progressDialog?.show()
            nav_view.menu.setGroupVisible(R.id.menu_group, true)
            nav_view.menu.setGroupVisible(R.id.login_group, false)
            nav_view.menu.setGroupVisible(R.id.logout_group, true)
            header.isClickable = true
            DBWork.GetAvatar()?.addOnSuccessListener {
                header.findViewById<ImageView>(R.id.header_image).setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }?.addOnFailureListener {
                header.findViewById<ImageView>(R.id.header_image).setImageResource(R.mipmap.ic_launcher_round)
            }?.addOnCompleteListener { progressDialog?.dismiss() }
            header.findViewById<TextView>(R.id.header_email).text = user.email
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (findNavController(R.id.nav_host).currentDestination?.id == R.id.editUserInfo)
                askAndNavigateToFragment(R.id.userPage)
            else
                super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                if (findNavController(R.id.nav_host).currentDestination?.id == R.id.editUserInfo)
                    askAndNavigateToFragment(R.id.imeiActivity)
                else
                    findNavController(R.id.nav_host).navigate(R.id.imeiActivity)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host)
        item.isChecked = false
        if (navController.currentDestination?.id == R.id.editUserInfo) {
            askAndNavigateToFragment(item.itemId, item)
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }
        if (item.itemId == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut()
            recreate()
        }
        item.isChecked = true
        navController.navigate(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun askAndNavigateToFragment(id: Int, item: MenuItem? = null) {
        val builder = AlertDialog.Builder(this);
        builder.setMessage("You're about to loose unsaved changes!")
                .setPositiveButton("Leave") { _, _ ->
                    run {
                        item?.isChecked = true
                        if (id == R.id.logout){
                            findNavController(R.id.nav_host).navigate(R.id.startFragment)
                            FirebaseAuth.getInstance().signOut()
                            recreate()
                        }
                        else
                            findNavController(R.id.nav_host).navigate(id)
                    }
                }.setNegativeButton("Stay") { _, _ ->
                }
        builder.create().show();
    }

    override fun onPause() {
        progressDialog = null
        super.onPause()
    }
}
