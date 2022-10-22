package net.pooleaf.gamecore

import net.pooleaf.gamecore.configs.GameConfig
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.map.DefaultGameMapManager
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.map.GameMapManager
import net.pooleaf.gamecore.player.DefaultGamePlayerManager
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.player.GamePlayerManager
import net.pooleaf.gamecore.team.TeamManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object GameCore {

    lateinit var gamePlugin: JavaPlugin
    lateinit var game: Game

    lateinit var playerManager: GamePlayerManager<GamePlayer>
    lateinit var mapManager: GameMapManager<GameMap>

    val teamManager: TeamManager = TeamManager()

    val gameConfig: GameConfig by lazy {
        GameConfig(File(gamePlugin.dataFolder, "game-config.yml"))
    }


    fun init(
        gamePlugin: JavaPlugin,
        game: Game,
        playerManager: GamePlayerManager<GamePlayer> = DefaultGamePlayerManager(),
        mapManager: GameMapManager<GameMap> = DefaultGameMapManager()
    ) {
        this.gamePlugin = gamePlugin
        this.game = game

        this.playerManager = playerManager
        this.mapManager = mapManager
    }

}