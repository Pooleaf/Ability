package net.pooleaf.gamecore.sidebar

interface GameSideBarElement {

    val priority: Int
    val texts: List<String>

    var show: Boolean

}