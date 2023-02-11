package net.pooleaf.gamecore.v1.map

class DefaultGameMapManager: GameMapManager<GameMap>() {

    override fun create(): GameMap {
        return GameMap()
    }

}