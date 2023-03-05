package net.pooleaf.ability.compat.bitability

import Xeon.VisualAbility.MainModule.AbilityBase
import Xeon.VisualAbility.VisualAbility
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.ability.compat.CompatPlugin
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import org.bukkit.Bukkit

class BitAbilityCompatPlugin : CompatPlugin<VisualAbility, AbilityBase>() {

    override val name: String = "BitAbility"

    private val listener = BitAbilityListener()

    override fun getOriginalAbilityClass(): Class<AbilityBase> {
        return AbilityBase::class.java
    }

    override fun getCompatAbilityClass(): Class<out CompatAbility<AbilityBase>> {
        return BitCompatAbility::class.java
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, AbilityPlugin.instance)
    }

    override fun onDisable() {
        BukkitReflectionUtil.unregisterListener(listener)
    }

}