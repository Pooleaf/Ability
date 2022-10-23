package net.pooleaf.gamecore.team

import net.pooleaf.gamecore.player.GamePlayer

class TeamManager {

    val teams: HashSet<Team> = HashSet()


    fun add(team: Team) {
        teams.add(team)
    }

    fun remove(team: Team) {
        teams.remove(team)
    }

    fun exists(team: Team): Boolean {
        return teams.contains(team)
    }

    /**
     * [GamePlayer]가 소속된 [Team]을 반환합니다.
     */
    fun getByGamePlayer(gamePlayer: GamePlayer): Team? {
        return teams.firstOrNull { it.players.contains(gamePlayer) }
    }

    /**
     * [GamePlayer]가 탈락하지 않고 온라인 상태인 [Team]의 [List]를 반환합니다.
     */
    fun getNotDefeatedOnlineTeams(): List<Team> {
        return teams.filter { it.players.filter { it.isOnline && it.joined && !it.defeated && !it.observer }.isNotEmpty() }
            .toList()
    }

}