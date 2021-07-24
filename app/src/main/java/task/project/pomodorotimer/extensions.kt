package task.project.pomodorotimer

const val START_TIME = "00:00:00"

fun Long.formatTime(): String {
    if (this <= 0L) {
        return START_TIME
    }

    val h = formatSlot(this / 1000 / 1000)
    val m = formatSlot(this / 1000 % 3600 / 60)
    val s = formatSlot(this / 1000 % 60)

    return "$h:$m:$s"
}

fun formatSlot(count: Long): String {
    return when {
        count >= 10L -> count.toString()
        else -> "0$count"
    }
}