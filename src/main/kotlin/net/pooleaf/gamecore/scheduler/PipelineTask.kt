package net.pooleaf.gamecore.scheduler

import net.pooleaf.core.plugin.CorePlugin

abstract class PipelineTask(
    val plugin: CorePlugin,
    val resolve: (result: Any?) -> Any,
    var catch: (() -> Unit)? = null,
    val delayTick: Long = 0
) {

    abstract fun run(result: Any? = Unit): Any

}