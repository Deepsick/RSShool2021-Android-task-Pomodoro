package task.project.pomodorotimer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import task.project.pomodorotimer.databinding.ActivityMainBinding
import task.project.pomodorotimer.models.Stopwatch

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>()
    private val stopwatchAdapter = StopwatchAdapter(this)
    private var currentId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addTimerButton.setOnClickListener {
            val text = binding.timerEdittext.text.toString()
            val currentTime = if (text == "") {
                Toast.makeText(this, "Please, add timer time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                text.toLong() * 60L * 1000L
            }
            stopwatches.add(Stopwatch(currentId, currentTime, false))
            currentId++
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    override fun start(id: Int) {
        val runningTimer = stopwatches.find { it.isStarted }
        runningTimer?.let {
            stop(it.id, it.currentMs)
        }

        updateTimers(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        updateTimers(id, currentMs, false)
    }


    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun getContext(): Context {
        return this
    }


    private fun updateTimers(id: Int, currentMs: Long?, isStarted: Boolean) {
        stopwatches.forEachIndexed { index, timer ->
            if (timer.id == id) {
                stopwatches[index] = Stopwatch(timer.id, currentMs ?: timer.currentMs, isStarted)
            }
        }

        stopwatchAdapter.submitList(stopwatches.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)

        val runningTimer = stopwatches.find { it.isStarted }
        startIntent.putExtra(STARTED_TIMER_TIME_MS, runningTimer?.currentMs)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    companion object {
        private const val COMMAND_START = "START"
        private const val COMMAND_STOP = "STOP"
        private const val COMMAND_ID = "COMMAND_ID"
        private const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME_MS"
    }
}