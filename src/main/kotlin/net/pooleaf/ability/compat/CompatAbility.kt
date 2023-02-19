package net.pooleaf.ability.compat

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.ClonableAbility

abstract class CompatAbility<T> : Ability(), ClonableAbility {

    var originalAbility: T? = null

    abstract fun convertFromOriginalAbility()

}