package net.pooleaf.gamecore.scheduler

class SchedulerPipeline {

    val tasks: ArrayList<PipelineTask> = ArrayList()


    fun addTask(task: PipelineTask): SchedulerPipeline {
        tasks.add(task)
        return this
    }

    fun run() {
        var currentTask: PipelineTask? = null
        var result: Any? = Unit

        try {
            tasks.forEach {
                currentTask = it
                result = it.run(result)
            }
        } catch (exception: Exception) {
            currentTask?.catch?.let { it() }
            // TODO 공용 catch
        }
    }

}