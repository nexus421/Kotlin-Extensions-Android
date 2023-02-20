package bayern.kickner.kotlinextensionsandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bayern.kickner.kotlin_extensions_android.compress
import bayern.kickner.kotlin_extensions_android.decompress

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val compress = "Bananaramafoiasjrgfasrg anrdsfig nasdfklgji hnasdfölkgv jnasörig hasfdöoigb haröoguh aoöfrgbiu vhaöorbvigahrn öbaorfhb nalorfuib nvaföoib vnaröo asdf asdf sadf asdf asdf asdf asr gaer gaewrg na rl fasdej faslka sdfasd fasd fasd fasdf asdf asd fasd fawrgnljkawrh gawriulgfha sdgf asdfsaa sdfas dfasd fdsa fsadf df sdad fa Bananaramafoiasjrgfasrg anrdsfig nasdfklgji hnasdfölkgv jnasörig hasfdöoigb haröoguh aoöfrgbiu vhaöorbvigahrn öbaorfhb nalorfuib nvaföoib vnaröo asdf asdf sadf asdf asdf asdf asr gaer gaewrg na rl fasdej faslka sdfasd fasd fasd fasdf asdf asd fasd fawrgnljkawrh gawriulgfha sdgf asdfsaa sdfas dfasd fdsa fsadf df sdad fa Bananaramafoiasjrgfasrg anrdsfig nasdfklgji hnasdfölkgv jnasörig hasfdöoigb haröoguh aoöfrgbiu vhaöorbvigahrn öbaorfhb nalorfuib nvaföoib vnaröo asdf asdf sadf asdf asdf asdf asr gaer gaewrg na rl fasdej faslka sdfasd fasd fasd fasdf asdf asd fasd fawrgnljkawrh gawriulgfha sdgf asdfsaa sdfas dfasd fdsa fsadf df sdad fa Bananaramafoiasjrgfasrg anrdsfig nasdfklgji hnasdfölkgv jnasörig hasfdöoigb haröoguh aoöfrgbiu vhaöorbvigahrn öbaorfhb nalorfuib nvaföoib vnaröo asdf asdf sadf asdf asdf asdf asr gaer gaewrg na rl fasdej faslka sdfasd fasd fasd fasdf asdf asd fasd fawrgnljkawrh gawriulgfha sdgf asdfsaa sdfas dfasd fdsa fsadf df sdad fa".compress()
        val decompress = compress.decompress()
        println(decompress)

    }
}