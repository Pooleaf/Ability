package net.pooleaf.gamecore.phase

class RunnablePhase(
    val runnable: Runnable
): Phase() {

    override fun onStart() {
        runnable.run()
        end()
    }

}