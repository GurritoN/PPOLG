package lab.gurriton.ppolg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

        fun StringToXML(str: String): Document{
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val d1 = builder.parse(InputSource(StringReader(str)))
            return d1
        }
    }
}