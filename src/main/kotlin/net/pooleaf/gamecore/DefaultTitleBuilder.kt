package net.pooleaf.gamecore

import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder

class DefaultTitleBuilder: TitleBuilder() {

    init {
        stay(20)
        fadeIn(10)
        fadeOut(10)
    }

}