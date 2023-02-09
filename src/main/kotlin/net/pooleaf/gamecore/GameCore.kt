package net.pooleaf.gamecore

import net.pooleaf.gamecore.configs.AutoGameConfig
import net.pooleaf.gamecore.configs.QuickBarConfig
import net.pooleaf.gamecore.configs.SpawnConfig
import net.pooleaf.gamecore.configs.TeamConfig
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.map.DefaultGameMapManager
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.map.GameMapManager
import net.pooleaf.gamecore.player.DefaultGamePlayerManager
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.player.GamePlayerManager
import net.pooleaf.gamecore.quickbars.QuickBarManager
import net.pooleaf.gamecore.sql.GameSqlManager
import net.pooleaf.gamecore.team.DefaultTeamManager
import net.pooleaf.gamecore.team.Team
import net.pooleaf.gamecore.team.TeamManager
import net.pooleaf.gamecore.vote.map.MapVoteService
import net.pooleaf.gamecore.vote.start.StartVoteService
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object GameCore {

    lateinit var gamePlugin: JavaPlugin
    lateinit var game: Game

    lateinit var playerManager: GamePlayerManager<GamePlayer>
    lateinit var teamManager: TeamManager<Team>
    lateinit var mapManager: GameMapManager<GameMap>
    lateinit var quickBarManager: QuickBarManager

    lateinit var sqlManager: GameSqlManager



    lateinit var startVoteService: StartVoteService
    lateinit var mapVoteService: MapVoteService


    val autoGameConfig: AutoGameConfig by lazy {
        AutoGameConfig(File(gamePlugin.dataFolder, "game-config.yml"))
    }

    val spawnConfig: SpawnConfig by lazy {
        SpawnConfig(File(gamePlugin.dataFolder, "spawn-config.yml"))
    }

    val quickBarConfig: QuickBarConfig by lazy {
        QuickBarConfig(File(gamePlugin.dataFolder, "quickbar-config.yml"))
    }

    val teamConfig: TeamConfig by lazy {
        TeamConfig(File(gamePlugin.dataFolder, "team-config.yml"))
    }


    fun init(
        gamePlugin: JavaPlugin,
        game: Game,
        playerManager: GamePlayerManager<GamePlayer> = DefaultGamePlayerManager(),
        mapManager: GameMapManager<GameMap> = DefaultGameMapManager(),
        teamManager: TeamManager<Team> = DefaultTeamManager()
    ) {
        this.gamePlugin = gamePlugin
        this.game = game

        this.playerManager = playerManager
        this.mapManager = mapManager
        this.teamManager = teamManager
        this.quickBarManager = QuickBarManager()

        this.sqlManager = GameSqlManager()

        this.startVoteService = StartVoteService()
        this.mapVoteService = MapVoteService()

        sqlManager.connect()

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

        mapManager.loadAllConfig()
    }

}