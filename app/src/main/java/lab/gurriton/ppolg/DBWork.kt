package lab.gurriton.ppolg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_edit_user_info.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.util.Collections.replaceAll
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class DBWork{
    companion object {
        fun BitmapToByteArray(bitmap: Bitmap) : ByteArray
        {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return baos.toByteArray()
        }

        fun ByteArrayToBitmap(byteArray: ByteArray) : Bitmap
        {
            return BitmapFactory.decodeByteArray(byteArray,0, byteArray.size)
        }

        fun XMLToString(doc: Document): String{
            val tf = TransformerFactory.newInstance()
            val transformer = tf.newTransformer()
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            val writer = StringWriter()
            transformer.transform(DOMSource(doc), StreamResult(writer))
            val output = writer.getBuffer().toString().replace("\n|\r", "")
            return output
        }

        fun StringToXML(str: String): Document?{
            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val d1 = builder.parse(InputSource(StringReader(str)))
                return d1
            }
            catch (ex: Exception){
                return null
            }
        }

        fun GetUser(): FirebaseUser? {
            return FirebaseAuth.getInstance().currentUser
        }

        fun GetUserInfo(): DatabaseReference?{
            val user = GetUser()
            if (user != null)
                return FirebaseDatabase.getInstance().getReference().child("users").child(user.uid)
            return null
        }

        fun GetAvatar(): Task<ByteArray>?{
            val user = GetUser()
            if (user != null)
                return FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024 * 1024 * 1024)
            return null
        }

        fun SaveUserInfo(userInfo: UserInfo): Task<Void>? {
            val user = GetUser()
            if (user != null)
                return FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).setValue(userInfo)
            return null
        }

        fun SaveAvatar(bitmap: Bitmap): UploadTask?{
            val user = GetUser()
            if (user != null) {
                val storage = FirebaseStorage.getInstance().getReference()
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                return storage.child("avatars/" + user.uid + ".jpg").putBytes(baos.toByteArray())
            }
            return null
        }
        fun SaveRSSCache(doc: Document){
            val user = GetUser()
            if (user != null)
                FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssCache").setValue(XMLToString(doc))
        }

        fun SaveRSSUrl(str: String){
            val user = GetUser()
            if (user != null)
                FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssUrl").setValue(str)
        }
    }
}