package net.pooleaf.gamecore.v1

import net.pooleaf.gamecore.v1.configs.AutoGameConfig
import net.pooleaf.gamecore.v1.configs.QuickBarConfig
import net.pooleaf.gamecore.v1.configs.SpawnConfig
import net.pooleaf.gamecore.v2.configs.TeamConfig
import net.pooleaf.gamecore.v1.game.Game
import net.pooleaf.gamecore.v1.map.DefaultGameMapManager
import net.pooleaf.gamecore.v1.map.GameMap
import net.pooleaf.gamecore.v1.map.GameMapManager
import net.pooleaf.gamecore.v1.player.DefaultGamePlayerManager
import net.pooleaf.gamecore.v1.player.GamePlayer
import net.pooleaf.gamecore.v1.player.GamePlayerManager
import net.pooleaf.gamecore.v1.quickbars.QuickBarManager
import net.pooleaf.gamecore.v1.sql.GameSqlManager
import net.pooleaf.gamecore.v1.team.DefaultTeamManager
import net.pooleaf.gamecore.v1.team.Team
import net.pooleaf.gamecore.v1.team.TeamManager
import net.pooleaf.gamecore.v1.vote.map.MapVoteManager
import net.pooleaf.gamecore.v1.vote.start.StartVoteManager
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



    lateinit var startVoteManager: StartVoteManager
    lateinit var mapVoteManager: MapVoteManager


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
        GameCore.gamePlugin = gamePlugin
        GameCore.game = game

        GameCore.playerManager = playerManager
        GameCore.mapManager = mapManager
        GameCore.teamManager = teamManager
        quickBarManager = QuickBarManager()

        sqlManager = GameSqlManager()
        sqlManager.connect()

        loadConfig()

        startVoteManager = StartVoteManager()
        mapVoteManager = MapVoteManager()
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