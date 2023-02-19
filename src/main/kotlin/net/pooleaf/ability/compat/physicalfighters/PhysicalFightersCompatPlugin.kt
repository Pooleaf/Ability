package net.pooleaf.ability.compat.physicalfighters

import Physical.Fighters.PhysicalFighters
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.ability.compat.CompatPlugin

class PhysicalFightersCompatPlugin : CompatPlugin<PhysicalFighters>() {

    override val name: String = "PhysicalFighters"

    override fun getOriginalAbilityClass(): Class<Any> {
        TODO("Not yet implemented")
    }

    override fun getCompatAbilityClass(): Class<out CompatAbility<Any>> {
        TODO("Not yet implemented")
    }

}