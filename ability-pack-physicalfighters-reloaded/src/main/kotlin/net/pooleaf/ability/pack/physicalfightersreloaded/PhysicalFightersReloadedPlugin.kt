package net.pooleaf.ability.pack.physicalfightersreloaded

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.compat.physicalfighters.PhysicalFightersCompatPlugin
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class PhysicalFightersReloadedPlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: PhysicalFightersReloadedPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ PhysicalFightersReloaded ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        val physicalFightersPluginName = PhysicalFightersCompatPlugin().name

        // 능력 등록
        AbilityApi.unsafe.abilityManager.registerAbilities(this)

        // 이 플러그인에서 구현된 능력들을 PhysicalFighters에서 찾아서 밴 처리
        AbilityApi.unsafe.abilityManager.getAbilities().filter { it.pluginName == this.name }
            .forEach { AbilityApi.unsafe.abilityManager.getAbilityByFullName("${physicalFightersPluginName}:${it.name}")?.ban = true }
    }

}