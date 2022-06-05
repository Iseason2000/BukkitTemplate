package top.iseason.bukkit.bukkittemplate.common

import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.TemplatePlugin

fun submit(
    delay: Long = 0,
    period: Long = 0,
    async: Boolean = false,
    task: Submitter.() -> Unit
) {
    check(delay >= 0) { "delay must grater than 0" }
    check(period >= 0) { "period must grater than 0" }
    val submitter = Submitter(delay, period, async, task)
    if (async) {
        if (period > 0) {
            submitter.runTaskTimerAsynchronously(TemplatePlugin.getPlugin(), delay, period)
        } else {
            submitter.runTaskLaterAsynchronously(TemplatePlugin.getPlugin(), delay)
        }
    } else {
        if (period > 0) {
            submitter.runTaskTimer(TemplatePlugin.getPlugin(), delay, period)
        } else {
            submitter.runTaskLater(TemplatePlugin.getPlugin(), delay)
        }
    }
}

class Submitter(
    val delay: Long,
    val period: Long,
    val async: Boolean,
    val task: Submitter.() -> Unit
) : BukkitRunnable() {
    override fun run() {
        task()
    }
}