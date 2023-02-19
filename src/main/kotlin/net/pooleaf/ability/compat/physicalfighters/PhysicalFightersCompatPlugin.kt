package net.pooleaf.ability.compat.physicalfighters

import Physical.Fighters.MainModule.AbilityBase
import Physical.Fighters.PhysicalFighters
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.ability.compat.CompatPlugin
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import org.bukkit.Bukkit

class PhysicalFightersCompatPlugin : CompatPlugin<PhysicalFighters, AbilityBase>() {

    override val name: String = "PhysicalFighters"

    private val listener = PhysicalFightersListener()

    override fun getOriginalAbilityClass(): Class<AbilityBase> {
        return AbilityBase::class.java
    }

    override fun getCompatAbilityClass(): Class<out CompatAbility<AbilityBase>> {
        return PhysicalFightersCompatAbility::class.java
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, AbilityPlugin.instance)
    }

    override fun onDisable() {
        BukkitReflectionUtil.unregisterListener(listener)
    }

}