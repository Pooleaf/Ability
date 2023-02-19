package net.pooleaf.ability.compat.bitability

import Xeon.VisualAbility.MainModule.AbilityBase
import Xeon.VisualAbility.VisualAbility
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.ability.compat.CompatPlugin

class BitAbilityCompatPlugin : CompatPlugin<VisualAbility, AbilityBase>() {

    override val name: String = "BitAbility"

    override fun getOriginalAbilityClass(): Class<AbilityBase> {
        return AbilityBase::class.java
    }

    override fun getCompatAbilityClass(): Class<out CompatAbility<AbilityBase>> {
        return BitCompatAbility::class.java
    }

}