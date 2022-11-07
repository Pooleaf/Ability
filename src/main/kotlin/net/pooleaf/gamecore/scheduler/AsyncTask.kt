package net.pooleaf.gamecore.scheduler

import net.pooleaf.core.modules.commonscheduler.CommonSchedulerModule
import net.pooleaf.core.plugin.CorePlugin
import java.util.concurrent.CompletableFuture

class AsyncTask(
    plugin: CorePlugin,
    resolve: (result: Any?) -> Any,
    catch: (() -> Unit)? = null,
    delayTick: Long = 0
): PipelineTask(
    plugin,
    resolve,
    catch,
    delayTick
) {

    override fun run(result: Any?): Any {
        val future = CompletableFuture<Any>()

        CommonSchedulerModule.bukkit().scheduler.runAsync(plugin, {
            val value = resolve(result)
            future.complete(value)
        }, delayTick)

        return future.get()
    }

}