package net.pooleaf.ability.ability

import net.pooleaf.core.modules.support.common.util.ReflectionUtil
import net.pooleaf.core.plugin.CorePlugin
import java.util.Random

class AbilityManager {

    val abilities = ArrayList<Ability>()


    /**
     * [ability]를 등록합니다.
     */
    fun registerAbility(ability: Ability) {
        abilities.add(ability)
    }

    /**
     * [ability]가 등록되어 있는지 확인합니다.
     */
    fun isRegisteredAbility(ability: Ability): Boolean {
        return abilities.any { it -> it.pluginName == ability.pluginName && it.name == it.name }
    }

    /**
     * [plugin]의 [Ability]들을 등록합니다.
     */
    fun registerAbilities(plugin: CorePlugin) {
        ReflectionUtil.getClasses(plugin).forEach { pluginClass ->
            try {
                if (!Ability::class.java.isAssignableFrom(pluginClass)) return@forEach

                val ability = pluginClass.newInstance() as Ability
                registerAbility(ability)
            } catch (e: Exception) {
            } catch (e: Error) {
            }
        }
    }

    /**
     * 랜덤 [Ability]를 반환합니다.
     */
    fun getRandomAbility(): Ability {
        return abilities.filter { !it.ban }[Random().nextInt(abilities.size)]
    }

    /**
     * [abilityName] 이름을 가진 [Ability]를 반환합니다.
     * 띄어쓰기와 대소문자를 구분하지 않고 검색합니다.
     */
    fun getAbility(abilityName: String): Ability {
        val noSpaceAbilityName = abilityName.replace(" ", "")

        return abilities.filter { it.name.replace(" ", "").equals(noSpaceAbilityName, true) }.first()
    }

}