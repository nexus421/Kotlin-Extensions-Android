package bayern.kickner.kotlinextensionsandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import bayern.kickner.kotlin_extensions_android.uri.AndroidFile
import bayern.kickner.kotlin_extensions_android.uri.deleteFileThroughUri
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MainActivity : AppCompatActivity() {

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    private var uri: Uri? = null
    private lateinit var available: List<AndroidFile>

    lateinit var registeererer: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.text)
        val btn1 = findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn4 = findViewById<Button>(R.id.btn4)


        val biometricManager = BiometricManager.from(this)
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showSnackBar("Authentication error: Code: $errorCode ($errString)")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showSnackBar("Failed to authenticate. Please try again.")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val type = result.authenticationType
                    showSnackBar("\uD83C\uDF89 Authentication successful! Type: $type \uD83C\uDF89")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Example biometric authentication")
            .setSubtitle("Please authenticate yourself first.")
            .setAllowedAuthenticators(DEVICE_CREDENTIAL or BIOMETRIC_WEAK)
            .build()


        registeererer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                tryAuthenticateBiometric(biometricManager, biometricPrompt, promptInfo)
            } else {
                showSnackBar("Failed to enroll in biometric")
            }
        }




        btn1.setOnClickListener {


            tryAuthenticateBiometric(biometricManager, biometricPrompt, promptInfo)

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
            available.random().toFile()
                .writeText("aeslchnrlnghvlreaksjhngresluahgvreslunhgval rera balrvbnalkrjv banlesrugb areslkuv blsakdrb vnlkfdsa b")
            File(Environment.getExternalStoragePublicDirectory("Documents"), "direkterTest.txt").writeText("oidaaaa")
        }

        println()

    }

    fun tryAuthenticateBiometric(biometricManager: BiometricManager, biometricPrompt: BiometricPrompt, promptInfo: BiometricPrompt.PromptInfo) {
        when (biometricManager.canAuthenticate(DEVICE_CREDENTIAL or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showSnackBar("No biometric features available on this device")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showSnackBar("Biometric features are currently unavailable")
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                showSnackBar("Biometric options are incompatible with the current Android version")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    registeererer.launch(
                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                DEVICE_CREDENTIAL or BIOMETRIC_WEAK
                            )
                        }
                    )
                } else {
                    showSnackBar("Could not request biometric enrollment in API level < 30")
                }
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                showSnackBar("Biometric features are unavailable because security vulnerabilities has been discovered in one or more hardware sensors")
            }

            else -> {
                throw IllegalStateException()
            }
        }
    }


}