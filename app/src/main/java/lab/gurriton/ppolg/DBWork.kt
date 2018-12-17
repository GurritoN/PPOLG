package lab.gurriton.ppolg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

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
    }
}