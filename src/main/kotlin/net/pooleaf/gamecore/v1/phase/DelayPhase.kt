package net.pooleaf.gamecore.v1.phase

class DelayPhase(
    val delayCount: Int
): Phase() {

    override fun onRun() {
        if (count == delayCount) {
            end()
        }
    }

}