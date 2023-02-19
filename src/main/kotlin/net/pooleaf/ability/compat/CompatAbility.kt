package net.pooleaf.ability.compat

import net.pooleaf.ability.ability.Ability

abstract class CompatAbility<T> : Ability() {

    var originalAbility: T? = null

    abstract fun convertFromOriginalAbility()

}