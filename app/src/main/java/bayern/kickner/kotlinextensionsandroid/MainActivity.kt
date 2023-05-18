package bayern.kickner.kotlinextensionsandroid

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import bayern.kickner.kotlin_extensions_android.uri.AndroidFile
import bayern.kickner.kotlin_extensions_android.uri.deleteFileThroughUri
import java.io.File

class MainActivity : AppCompatActivity() {

    private var uri: Uri? = null
    private lateinit var available: List<AndroidFile>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.text)
        val btn1 = findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn4 = findViewById<Button>(R.id.btn4)

        btn1.setOnClickListener {
//            when (val result = createFileInPublicAndWriteData(folderPath = "TestFolderAllone", displayName = "modul.txt", bytesToWrite = "".toByteArray())) {
//                is ResultOf2.Failure -> Toast.makeText(this, result.value.name, Toast.LENGTH_SHORT).show()
//                is ResultOf2.Success -> uri = result.value
//            }
        }

        btn2.setOnClickListener {
            uri?.let {
                deleteFileThroughUri(it)
            }
        }

        btn3.setOnClickListener {
//            when (val result = getAllAccessibleFilesFromPublic()) {
//                is ResultOf2.Failure -> Toast.makeText(this, result.value.joinToString { it.name }, Toast.LENGTH_SHORT).show()
//                is ResultOf2.Success -> {
//                    available = result.value
//                    text.text = result.value.joinToString(separator = "\n")
//                }
//            }
        }

        btn4.setOnClickListener {
//            uri?.writeDataToFile(this, "Test Bananarama".toByteArray())
//            uri?.let { File(it.path).writeText("Uri to File worked!") }
            available.random().toFile().writeText("aeslchnrlnghvlreaksjhngresluahgvreslunhgval rera balrvbnalkrjv banlesrugb areslkuv blsakdrb vnlkfdsa b")
            File(Environment.getExternalStoragePublicDirectory("Documents"), "direkterTest.txt").writeText("oidaaaa")
        }

        println()

    }
}