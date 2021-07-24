package task.project.pomodorotimer

import android.content.Context

interface StopwatchListener {
    fun start(id: Int)
    fun stop(id: Int, currentMs: Long)
    fun delete(id: Int)
    fun getContext(): Context
}