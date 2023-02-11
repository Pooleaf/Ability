package net.pooleaf.gamecore.v2.map

class DefaultGameMapFactory: GameMapFactory<GameMap> {

    override fun createGameMap(): GameMap {
        return GameMap()
    }

}