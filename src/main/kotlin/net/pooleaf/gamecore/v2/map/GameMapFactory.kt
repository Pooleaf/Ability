package net.pooleaf.gamecore.v2.map

interface GameMapFactory<T: GameMap> {

    fun createGameMap(): T

}