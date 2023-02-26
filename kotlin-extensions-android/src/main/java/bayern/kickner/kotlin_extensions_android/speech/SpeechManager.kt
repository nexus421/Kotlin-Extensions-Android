package bayern.kickner.kotlin_extensions_android.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.io.Closeable

/**
 * Simple voice detection with keyword search.
 * How does this work?
 * We use the SpeechRecognizer from Google. When onReadyForSpeech the recognizer waits for voice input. If something is detected (or nothing within a small time) the detection ends.
 * If the Result contains the keyword, onResult will be called. Otherwise the recognizer will be startet again.
 *
 * @param inputSearcher Validate the input which is required. Example: Want a Keyword? Make it.contains(KEYWORD). Want a date? Make dateRegex.find(it). Why use this inputSearcher? Sometimes the first recognized result is not the right one. With this, you can search all received results and check them.
 * @param preferOffline if true, the speech recognizer will use the offline-mode. Warning: Results are pretty bad compared to online! You should use the default here.
 * @param onReadyForSpeech will be called, when the speech is ready to detect voice. Maybe you want the user to notify about that?
 * @param onErrorCode Will be called after any ErrorCode (except code 7, which means "no input"). If an error occurs, recignition stops (except code 7)
 * @param onResult if inputSearcher returns true, the String will be returned. If nothing will be detected, this method will be never called.
 */
class SpeechManager(
    private val context: Context,
    private val inputSearcher: (String) -> Boolean,
    private val maxResults: Int = 3,
    private val preferOffline: Boolean = false,
    val onReadyForSpeech: (() -> Unit)? = null,
    val onErrorCode: ((Int) -> Unit)? = null,
    val onResult: (String) -> Unit
) : RecognitionListener, Closeable {

    private val speech: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, preferOffline)
    }
    private var ready = false

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speech.setRecognitionListener(this)
        } else {
            // Handle error
            Log.e("SpeechManager", "Speech not available")
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        if (ready.not()) {
            onReadyForSpeech?.let { it() }
            ready = true
        }
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onRmsChanged(rmsdB: Float) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(error: Int) {
        if (error == SpeechRecognizer.ERROR_NO_MATCH) start()
        else {
            onErrorCode?.let { it(error) } ?: Log.e("SpeechManager", "Spracherkennung-Fehler-Code: $error")
            close()
        }
    }

    override fun onResults(result: Bundle) {
        //Das erste Ergebnis ist das wahrscheinlich Beste.
        val results = result.speechResults()
        println(results.joinToString())
        results.forEach {
            if(inputSearcher(it)) {
                close()
                return onResult(it)
            }
        }
        start() //Nichts gefunden, nochmal suche starten.
    }

    override fun onPartialResults(partialResults: Bundle) {

    }

    override fun onEvent(eventType: Int, params: Bundle?) {

    }

    fun Bundle.speechResults() = this.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList()
    fun Bundle.speechConfidenceScores(): FloatArray = this.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES) ?: floatArrayOf()

    fun start() {
        speech.startListening(recognizerIntent)
    }

    override fun close() {
        try {
            speech.stopListening()
            speech.destroy()
        } catch (e: Exception) {
            Log.e("SpeechManager", "Fehler beim Schließen des SpeechRecognizers.", e)
        }
    }

}