package com.example.vamosrachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var moneyValue: String = ""
    private var groupValue: String = ""

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listenInputsChange()
        talk()
        share()
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.i("TTS", "TTS ativado!")
        } else {
            Log.e("TTS", "Inicialização falhou!")
        }
    }

    private fun talk() {
        val talkButton = findViewById<FloatingActionButton>(R.id.sound_button)
        val resultText = findViewById<TextView>(R.id.result_text)

        tts = TextToSpeech(this, this)


        talkButton.setOnClickListener {
            tts!!.speak(resultText.text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    private fun share() {
        val shareButton = findViewById<FloatingActionButton>(R.id.share_button)
        val resultText = findViewById<TextView>(R.id.result_text)
        val df = DecimalFormat("#0.00")

        shareButton.setOnClickListener {
            if (moneyValue !== "" && groupValue !== "") {
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("text/plain")

                val moneyFormatted = df.format(moneyValue.toDouble())

                val message = "O valor(R$ $moneyFormatted) da conta dividida para $groupValue pessoas foi de R$ ${resultText.text}"

                intent.putExtra(android.content.Intent.EXTRA_TEXT, message)
                startActivity(Intent.createChooser(intent, "Escolha um aplicativo"))
            }
        }
    }

    private fun listenInputsChange() {
        val moneyInput = findViewById<EditText>(R.id.money_input)
        val groupInput = findViewById<EditText>(R.id.group_input)

        moneyInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                moneyValue = p0.toString()
                changeResultText()
            }
        })

        groupInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                groupValue = p0.toString()
                changeResultText()
            }
        })
    }

    private fun changeResultText() {
        val resultText = findViewById<TextView>(R.id.result_text)
        val df = DecimalFormat("#0.00")

        val mValue = when(val value = this.moneyValue.toDoubleOrNull()) {
            null -> 0.0
            else -> value
        }

        val gValue = when(val value = this.groupValue.toIntOrNull()) {
            null -> 0
            else -> value
        }

        if (mValue == 0.0 || gValue == 0 || gValue == 1) {
            resultText.text = getString(R.string.initial_value)
        } else {
            resultText.text = df.format(mValue / gValue)
        }
    }
}