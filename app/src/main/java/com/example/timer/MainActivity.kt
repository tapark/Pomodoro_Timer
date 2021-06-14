package com.example.timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val minutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.minutesTextView)
    }

    private val secondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.secondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null

    private var incomingId: Int? = null

    private var clockId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()

    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 메모리 할당 해제
    }


    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object:  SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        updateTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress == 0 || seekBar == null)
                    {
                        currentCountDownTimer?.cancel()
                        currentCountDownTimer = null
                        soundPool.autoPause()
                        return
                    }
                    currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
                    currentCountDownTimer?.start()

                    soundPool.play(incomingId!!, 1F, 1F, 0, -1, 1F)
                }
            }
        )
    }

    private fun createCountDownTimer(initMillis: Long): CountDownTimer {
        return object: CountDownTimer(initMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {

                updateTime(0)
                updateSeekBar(0)

                soundPool.autoPause()
                soundPool.play(clockId!!, 1F, 1F, 0, 0, 1F)
            }
        }
    }

    private fun updateTime(remainMillis: Long) {

        val remainSeconds: Long = remainMillis / 1000

        minutesTextView.text = "%02d\'".format(remainSeconds / 60)
        secondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

    private fun initSounds() {
        incomingId = soundPool.load(this, R.raw.pling_sound, 1)
        clockId = soundPool.load(this, R.raw.clocksound, 1)
    }
}