package net.pooleaf.ability.ability

import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.util.ReflectionUtil
import net.pooleaf.core.plugin.CorePlugin
import java.util.Random

class AbilityManager {

    private val abilities = ArrayList<Ability>()


    /**
     * [ability]를 등록합니다.
     */
    fun registerAbility(ability: Ability) {
        if (!ability.isInitialized) return

        abilities.add(ability)
        Logger.log("${ability.fullName} 능력이 등록되었습니다.")
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
     * [Ability]들을 등록합니다.
     */
    fun registerAbilities(abilities: List<Ability>) {
        abilities.forEach { registerAbility(it) }
    }

    fun getAbilities(): List<Ability> {
        return abilities
            .filter { it.isInitialized }
            .filter { !it.ban && !isBlacklistedAbility(it) }
    }

    fun getDefaultDrawAbilities(): List<Ability> {
        return abilities
            .filter { it.isInitialized }
            .filter { !it.ban && !isBlacklistedAbility(it) }
            .filter { it.rank != AbilityRank.HIDDEN }
    }

    /**
     * 플레이어에게 할당된 [Ability]들을 반환합니다.
     */
    fun getAssignedAbilities(): List<Ability> {
        return AbilityApi.unsafe.playerManager.getJoinedPlayers()
            .filter { it.ability != null }
            .map { it.ability!! }
            .toList()
    }

    /**
     * 플레이어에게 임시로 할당된 [Ability]들을 반환합니다.
     */
    fun getTempAssignedAbilities(): List<Ability> {
        return AbilityApi.unsafe.playerManager.getJoinedPlayers()
            .filter { it.tempAbility != null }
            .map { it.tempAbility!! }
            .toList()
    }

    /**
     * 랜덤 [Ability]를 반환합니다.
     */
    fun getRandomAbility(): Ability {
        return getAbilities().filter { !it.ban }[Random().nextInt(getAbilities().size)]
    }

    /**
     * 아무도 할당받지 않은 랜덤 [Ability]를 반환합니다.
     */
    fun getRandomAbilityNoDuplicated(): Ability? {
        val assignedAbilityFullNames = getAssignedAbilities().map { it.fullName }

        return getAbilities().filter { !assignedAbilityFullNames.contains(it.fullName) }
            .toList()
            .random()
    }

    /**
     * 아무도 임시로 할당받지 않은 랜덤 [Ability]를 반환합니다.
     */
    fun getRandomAbilityNoDuplicatedInTemp(): Ability? {
        val assignedAbilityFullNames = getTempAssignedAbilities().map { it.fullName }

        return getAbilities().filter { !assignedAbilityFullNames.contains(it.fullName) }
            .toList()
            .random()
    }


    /**
     * [abilityName] 이름을 가진 [Ability]를 반환합니다.
     * 띄어쓰기와 대소문자를 구분하지 않고 검색합니다.
     */
    fun getAbility(abilityName: String): Ability? {
        val noSpaceAbilityName = abilityName.replace(" ", "")

        return getAbilities().firstOrNull { it.name.replace(" ", "").equals(noSpaceAbilityName, true) }
    }

    /**
     * [abilityFullName] 풀네임을 가진 [Ability]를 반환합니다.
     * 띄어쓰기와 대소문자를 구분하지 않고 검색합니다.
     */
    fun getAbilityByFullName(abilityFullName: String): Ability? {
        val noSpaceAbilityFullName = abilityFullName.replace(" ", "")

        return getAbilities().firstOrNull { it.fullName.replace(" ", "").equals(noSpaceAbilityFullName, true) }
    }

    /**
     * 해당 능력이 블랙리스트에 등록된 능력인지 확인합니다.
     * 띄어쓰기와 대소문자를 구분하지 않고 검색합니다.
     */
    fun isBlacklistedAbility(ability: Ability): Boolean {
        val noSpaceAbilityFullName = ability.fullName.replace(" ", "")
        return AbilityApi.abilityBlacklistConfig.blacklist.find { noSpaceAbilityFullName.equals(it.replace(" ", ""), true) } != null
    }

}