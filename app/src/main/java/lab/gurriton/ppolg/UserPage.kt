package lab.gurriton.ppolg

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_edit_user_info.*
import kotlinx.android.synthetic.main.fragment_user_page.*

class UserPage : Fragment() {

    var oneCallbackTriggered: Boolean = false
    var progressDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(context)
        progressDialog?.setMessage("Loading")
        progressDialog?.show()
        val user = FirebaseAuth.getInstance().currentUser
        email.setText(user!!.email)
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userInfo: UserInfo? = dataSnapshot.getValue(UserInfo::class.java)
                if(first_name != null)
                    first_name.setText(userInfo?.firstName)
                if(last_name != null)
                    last_name.setText(userInfo?.lastName)
                if(phone != null)
                    phone.setText(userInfo?.phone)
                if(rss != null)
                    rss.setText(userInfo?.rssUrl)
                if (oneCallbackTriggered)
                    progressDialog?.dismiss()
                oneCallbackTriggered = true
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        profile_photo.setImageResource(R.mipmap.ic_launcher_round)
        DBWork.GetAvatar()?.addOnSuccessListener {
            if (profile_photo != null)
                profile_photo.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }?.addOnCompleteListener {
            if (oneCallbackTriggered)
                progressDialog?.dismiss()
            oneCallbackTriggered = true
        }
        edit_button.setOnClickListener { activity!!.findNavController(R.id.nav_host).navigate(R.id.action_userPage_to_editUserInfo) }

    }

    override fun onPause() {
        progressDialog = null
        super.onPause()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }
}
