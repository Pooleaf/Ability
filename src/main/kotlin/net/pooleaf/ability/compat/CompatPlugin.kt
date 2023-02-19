package net.pooleaf.ability.compat

import net.pooleaf.core.modules.support.common.util.ReflectionUtil
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * 호환 플러그인
 * T: 원본 JavaPlugin
 * AT: 원본 능력 객체
 */
abstract class CompatPlugin<T : JavaPlugin, AT> {

    var isEnable = false
        internal set

    abstract val name: String


    fun getPlugin(): T {
        return Bukkit.getPluginManager().getPlugin(name) as T
    }

    fun existsPlugin(): Boolean {
        return getPlugin() != null
    }

    open fun onEnable() {}
    open fun onDisable() {}

    /**
     * 원본 능력 클래스를 반환합니다.
     */
    abstract fun getOriginalAbilityClass(): Class<AT>

    /**
     * 호환 능력 클래스를 반환합니다.
     */
    abstract fun getCompatAbilityClass(): Class<out CompatAbility<AT>>

    /**
     * 호환 플러그인의 능력을 변환하여 불러와 반환합니다.
     */
    fun loadAbilities(): List<CompatAbility<*>> {
        val plugin = getPlugin() ?: error("${name} not exists")
        val pluginFile = ReflectionUtil.getFile(plugin) ?: error("${name} file not exists")

        val compatAbilityClass = getCompatAbilityClass() ?: error("compatAbilityClass cannot be null")

        return ReflectionUtil.getClasses(pluginFile)
            .filter { it != getOriginalAbilityClass() }
            .filter { getOriginalAbilityClass().isAssignableFrom(it) }
            .map { it as Class<out AT> }
            .mapNotNull { clazz ->
                try {
                    val compatAbility = compatAbilityClass.newInstance()

                    compatAbility.originalAbility = clazz.newInstance()
                    compatAbility.convertFromOriginalAbility()

                    compatAbility
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    null
                }
            }
    }

}