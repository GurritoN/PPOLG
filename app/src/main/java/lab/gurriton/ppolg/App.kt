package lab.gurriton.ppolg

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class PPOLGApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}