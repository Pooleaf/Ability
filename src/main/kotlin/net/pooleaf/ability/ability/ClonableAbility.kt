package net.pooleaf.ability.ability

interface ClonableAbility : Cloneable {

    public override fun clone(): Any {
        return super.clone()
    }

}