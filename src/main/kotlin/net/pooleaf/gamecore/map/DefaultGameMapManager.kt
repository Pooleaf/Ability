package net.pooleaf.gamecore.map

class DefaultGameMapManager: GameMapManager<GameMap>() {

    override fun create(): GameMap {
        return GameMap()
    }

}