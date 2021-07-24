package task.project.pomodorotimer.models

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean
)