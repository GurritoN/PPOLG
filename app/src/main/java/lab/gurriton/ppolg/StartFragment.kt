package lab.gurriton.ppolg

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_start.*
import android.os.AsyncTask.execute
import ReadRss
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView




class StartFragment : Fragment() {

    var RSSUrl: String? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInfo: UserInfo? = dataSnapshot.getValue(UserInfo::class.java)
                    RSSUrl = userInfo?.rssUrl
                    if (RSSUrl == null) {
                        val edit = EditText(context!!)
                        val builder = AlertDialog.Builder(context!!).setCancelable(false)
                                .setTitle("Enter RSS feed URL:")
                                .setView(edit)
                                .setPositiveButton("Set") { dialog, _ ->
                                    run {
                                        RSSUrl = edit.text.toString()
                                        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssUrl").setValue(RSSUrl)
                                        dialog.cancel()
                                    }
                                }
                        builder.show()
                    }
                    recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerview)
                    val readRss = ReadRss(context!!, recyclerView!!, RSSUrl!!, activity!!.resources.configuration.orientation)
                    readRss.execute()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
}
