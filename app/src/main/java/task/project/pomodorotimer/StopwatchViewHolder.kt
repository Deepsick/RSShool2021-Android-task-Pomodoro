package task.project.pomodorotimer

import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import task.project.pomodorotimer.databinding.StopwatchItemBinding
import task.project.pomodorotimer.models.Stopwatch

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {
    private var timer: CountDownTimer? = null
    private var startTime: Long = 0

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.formatTime()

        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initListeners(stopwatch)
    }

    private fun initListeners(stopwatch: Stopwatch) {
        binding.deleteButton.setOnClickListener {
            listener.delete(stopwatch.id)
        }

        binding.startButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        timer?.cancel()
        timer = getTimer(stopwatch)
        timer?.start()

        binding.startButton.text = "STOP"
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        timer?.cancel()

        binding.startButton.text = "START"
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, UNIT) {
            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.formatTime()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (startTime == 0L) {
                    startTime = stopwatch.currentMs
                    binding.viewPie.setPeriod(startTime)
                }
                stopwatch.currentMs -= UNIT

                val pieTime = startTime - stopwatch.currentMs
                binding.viewPie.setCurrent(pieTime)

                if (stopwatch.currentMs == 0L) {
                    stopTimer(stopwatch)
                    stopwatch.currentMs = startTime
                    binding.timerView.setBackgroundColor(ContextCompat.getColor(listener.getContext(), R.color.tomato_dark))
                }
                binding.stopwatchTimer.text = stopwatch.currentMs.formatTime()

            }
        }
    }

    private companion object {
        private const val UNIT = 1000L
    }
}