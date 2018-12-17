package lab.gurriton.ppolg

import android.app.Activity.RESULT_OK
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.android.synthetic.main.fragment_edit_user_info.*
import android.content.pm.PackageManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.jvm.javaClass


class EditUserInfo : Fragment() {

    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var photo: Bitmap? = null
    lateinit var email: String
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var phone: String
    lateinit var rss: String
    var savedPhoto: Bitmap? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("first_name", view?.findViewById<EditText>(R.id.edit_first_name)?.text.toString())
        outState.putString("last_name", view?.findViewById<EditText>(R.id.edit_last_name)?.text.toString())
        outState.putString("email", view?.findViewById<EditText>(R.id.edit_email)?.text.toString())
        outState.putString("phone", view?.findViewById<EditText>(R.id.edit_phone)?.text.toString())
        outState.putString("RSS", view?.findViewById<EditText>(R.id.edit_rss)?.text.toString())
        if (edit_profile_photo != null)
            outState.putByteArray("avatar", DBWork.BitmapToByteArray(photo!!))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        edit_profile_photo.setImageResource(R.mipmap.ic_launcher_round)
        if (savedInstanceState != null){
            val image = savedInstanceState.getByteArray("avatar")
            if (image != null){
                photo = DBWork.ByteArrayToBitmap(image)
            }

            view?.findViewById<EditText>(R.id.edit_email)?.setText(savedInstanceState.getString("email"))
            view?.findViewById<EditText>(R.id.edit_first_name)?.setText(savedInstanceState.getString("first_name"))
            view?.findViewById<EditText>(R.id.edit_last_name)?.setText(savedInstanceState.getString("last_name"))
            view?.findViewById<EditText>(R.id.edit_phone)?.setText(savedInstanceState.getString("phone"))
            view?.findViewById<EditText>(R.id.edit_rss)?.setText(savedInstanceState.getString("RSS"))
            view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(photo)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val user = DBWork.GetUser()
            edit_email.setText(user!!.email)
            DBWork.GetUserInfo()?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInfo: UserInfo? = dataSnapshot.getValue(UserInfo::class.java)
                    edit_first_name.setText(userInfo?.firstName)
                    edit_last_name.setText(userInfo?.lastName)
                    edit_phone.setText(userInfo?.phone)
                    edit_rss.setText(userInfo?.rssUrl)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            DBWork.GetAvatar()?.addOnSuccessListener {
                photo = BitmapFactory.decodeByteArray(it, 0, it.size)
                edit_profile_photo.setImageBitmap(photo)
            }
        }
        edit_gallery.setOnClickListener { takeFromGallery() }
        edit_camera.setOnClickListener { takePhoto() }
        edit_save.setOnClickListener { save(edit_email?.text.toString(), edit_first_name?.text.toString(), edit_last_name?.text.toString(), edit_phone?.text.toString(), this.photo, edit_rss?.text.toString()) }

    }

    fun takePhoto() {
        if (checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), TAKE_PHOTO)
        } else {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, TAKE_PHOTO)
        }
    }

    fun takeFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    if (data != null && data.data != null) {
                        photo = BitmapFactory.decodeStream(activity!!.contentResolver.openInputStream(data.data!!))
                        edit_profile_photo.setImageBitmap(photo)
                    }
                }
                TAKE_PHOTO -> {
                    if (data != null) {
                        photo = data.extras?.get("data") as Bitmap
                        edit_profile_photo.setImageBitmap(photo)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode ) {
            TAKE_PHOTO -> {
                val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, TAKE_PHOTO)
            }
        }
    }

    fun save(email: String, firstName: String, lastName: String, phone: String, photo: Bitmap?, rss: String){
        val user = DBWork.GetUser()

        if (user != null) {
            user.updateEmail(email)

            DBWork.SaveUserInfo(UserInfo(email, firstName, lastName, phone, rss))

            if (photo != null) {
                DBWork.SaveAvatar(photo)
            }
        }
    }

    override fun onPause() {
        email = edit_email?.text.toString()
        firstName = edit_first_name?.text.toString()
        lastName = edit_last_name?.text.toString()
        phone = edit_phone?.text.toString()
        savedPhoto = this.photo
        rss = edit_rss?.text.toString()
        super.onPause()
    }

    override fun onDestroy() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Save changes?")
                .setCancelable(false)
                .setNegativeButton("Discard") { dialog, _ ->
                    run {
                        dialog.cancel()
                    }
                }
                .setPositiveButton("Save") { dialog, _ ->
                    run {
                        save(email, firstName, lastName, phone, photo, rss)
                        dialog.cancel()
                    }
                }
        val alert = builder.create()
        alert.show()
        super.onDestroy()
    }
}
