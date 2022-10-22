package net.pooleaf.gamecore.phase

class DelayPhase(
    val delayCount: Int
): Phase() {

    override fun onRun() {
        if (count == delayCount) {
            end()
        }
    }

}