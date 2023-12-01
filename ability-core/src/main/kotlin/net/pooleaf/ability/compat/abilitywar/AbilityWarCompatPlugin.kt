package net.pooleaf.ability.compat.abilitywar

import Physical.Fighters.MainModule.AbilityBase
import Physical.Fighters.PhysicalFighters
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.ability.compat.CompatPlugin
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import org.bukkit.Bukkit

class AbilityWarCompatPlugin : CompatPlugin<PhysicalFighters, AbilityBase>() {

    override val name: String = "AbilityWar"

    private val listener = AbilityWarListener()

    override fun getOriginalAbilityClass(): Class<AbilityBase> {
        return AbilityBase::class.java
    }

    override fun getCompatAbilityClass(): Class<out CompatAbility<AbilityBase>> {
        return AbilityWarCompatAbility::class.java
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, AbilityPlugin.instance)
    }

    override fun onDisable() {
        BukkitReflectionUtil.unregisterListener(listener)
    }

}