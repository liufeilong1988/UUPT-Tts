package com.uupt.examplepdtts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uupt.examplepdtts.databinding.ActivityMainBinding
import com.uupt.paddle.tts.UuTensorTts

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var uuTensorTts: UuTensorTts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.speak.setOnClickListener {
            uuTensorTts?.speakText(binding.text.text.toString())
        }
        this.initTensor()
    }

    private fun initTensor() {
        uuTensorTts = UuTensorTts(this) {}
    }

    override fun onDestroy() {
        this.uuTensorTts?.onDestroy()
        this.uuTensorTts = null
        super.onDestroy()
    }
}