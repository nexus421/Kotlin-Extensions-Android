package bayern.kickner.kotlinextensionsandroid

import android.content.Context
import android.net.Uri
import android.os.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import bayern.kickner.kotlin_extensions_android.showDialogWithTimer
import bayern.kickner.kotlin_extensions_android.showToastOnMainThread
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

            showDialogWithTimer(this, "Test", "Das ist ein Test Text") {
                showToastOnMainThread("Bananarama")
            }
        }

        btn2.setOnClickListener {
            uri?.let {
                deleteFileThroughUri(it)
            }
        }

        btn3.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(200, 1))

            }
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