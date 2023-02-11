package net.pooleaf.gamecore.v1.phase

class RunnablePhase(
    val runnable: Runnable
): Phase() {

    override fun onStart() {
        runnable.run()
        end()
    }

}