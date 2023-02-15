package net.pooleaf.gamecore.sidebar

class GameSideBarNamedTextElement(
    override val priority: Int,
    var nameText: String,
    var valueText: String = "",
    override var show: Boolean = true
): GameSideBarElement {

    override val texts: List<String>
        get() = listOf<String>(nameText, valueText)

}