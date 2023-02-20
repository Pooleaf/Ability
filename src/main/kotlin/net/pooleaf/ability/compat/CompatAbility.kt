package net.pooleaf.ability.compat

import net.pooleaf.ability.ability.Ability

abstract class CompatAbility<T> : Ability(), Cloneable {

    var originalAbility: T? = null

    abstract fun convertFromOriginalAbility()

    public override fun clone(): Any {
        return super.clone()
    }

}