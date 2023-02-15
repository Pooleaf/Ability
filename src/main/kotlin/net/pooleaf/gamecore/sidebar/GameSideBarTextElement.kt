package net.pooleaf.gamecore.sidebar

class GameSideBarTextElement(
    override val priority: Int,
    var text: String = "",
    override var show: Boolean = true
): GameSideBarElement {

    override val texts: List<String>
        get() = listOf<String>(text)

}