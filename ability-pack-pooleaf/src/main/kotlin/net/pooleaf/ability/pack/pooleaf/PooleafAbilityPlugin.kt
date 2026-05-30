package net.pooleaf.ability.pack.pooleaf

import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class PooleafAbilityPlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: PooleafAbilityPlugin
    }

    override fun onStart() {
        instance = this

        prefix = "§a[ PooleafAbility ]"
        color = CommonChatColor.GREEN
        registerLoggerPrefix()

        AbilityApi.unsafe.abilityManager.registerAbilities(this)
    }

}
