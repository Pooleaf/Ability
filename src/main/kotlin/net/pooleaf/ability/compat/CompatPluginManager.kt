package net.pooleaf.ability.compat

import net.pooleaf.core.modules.support.common.manager.AbstractManager
import org.bukkit.plugin.java.JavaPlugin

class CompatPluginManager : AbstractManager<String, CompatPlugin<out JavaPlugin, *>>() {

    fun add(plugin: CompatPlugin<out JavaPlugin, *>) {
        set(plugin.name, plugin)
    }

}