package net.pooleaf.gamecore

import net.pooleaf.gamecore.configs.AutoGameConfig
import net.pooleaf.gamecore.configs.QuickBarConfig
import net.pooleaf.gamecore.configs.SpawnConfig
import net.pooleaf.gamecore.configs.TeamConfig
import net.pooleaf.gamecore.game.GameManager
import net.pooleaf.gamecore.map.*
import net.pooleaf.gamecore.player.DefaultGamePlayerManager
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.player.GamePlayerManager
import net.pooleaf.gamecore.player.GamePlayerService
import net.pooleaf.gamecore.quickbar.QuickBarManager
import net.pooleaf.gamecore.team.TeamManager
import net.pooleaf.gamecore.team.TeamService
import net.pooleaf.gamecore.vote.map.MapVoteManager
import net.pooleaf.gamecore.vote.start.StartVoteManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object GameCore {

    object unsafe {
        lateinit var gamePlugin: JavaPlugin

        lateinit var gameManager: GameManager

        lateinit var mapManager: GameMapManager<GameMap>
        lateinit var mapService: GameMapService

        lateinit var playerManager: GamePlayerManager<GamePlayer>
        lateinit var playerService: GamePlayerService

        lateinit var teamManager: TeamManager
        lateinit var teamService: TeamService

        lateinit var startVoteManager: StartVoteManager
        lateinit var mapVoteManager: MapVoteManager

        lateinit var quickBarManager: QuickBarManager


        val autoGameConfig: AutoGameConfig by lazy {
            AutoGameConfig(File(GameCore.gamePlugin.dataFolder, "game-config.yml"))
        }

        val spawnConfig: SpawnConfig by lazy {
            SpawnConfig(File(GameCore.gamePlugin.dataFolder, "spawn-config.yml"))
        }

        val quickBarConfig: QuickBarConfig by lazy {
            QuickBarConfig(File(GameCore.gamePlugin.dataFolder, "quickbar-config.yml"))
        }

        val teamConfig: TeamConfig by lazy {
            TeamConfig(File(GameCore.gamePlugin.dataFolder, "team-config.yml"))
        }


        fun init() {
            gameManager = GameManager()

            mapManager = DefaultGameMapManager()
            mapService = GameMapService()

            playerManager = DefaultGamePlayerManager()
            playerService = GamePlayerService()

            teamManager = TeamManager()
            teamService = TeamService()

            startVoteManager = StartVoteManager()
            mapVoteManager = MapVoteManager()

            quickBarManager = QuickBarManager()

            loadConfig()
        }

        fun loadConfig() {
            autoGameConfig.load()
            autoGameConfig.save()

            spawnConfig.load()
            spawnConfig.save()

            quickBarConfig.load()
            quickBarConfig.save()

            teamConfig.load()
            teamConfig.save()

            mapService.loadMapConfigs()
        }
    }

    val gamePlugin
        get() = unsafe.gamePlugin

    val autoGameConfig
        get() = unsafe.autoGameConfig

    val spawnConfig
        get() = unsafe.spawnConfig

    val quickBarConfig
        get() = unsafe.quickBarConfig

    val teamConfig
        get() = unsafe.teamConfig


    val game
        get() = unsafe.gameManager.game

    val currentMap
        get() = unsafe.mapManager.currentMap


    fun init(
        gamePlugin: JavaPlugin,
        game: net.pooleaf.gamecore.game.Game
    ) {
        unsafe.gamePlugin = gamePlugin

        unsafe.init()
        unsafe.gameManager.game = game
    }

}