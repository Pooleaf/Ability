package net.pooleaf.gamecore.v1.team

import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.player.GamePlayer

abstract class TeamManager<T: Team> {

    val teams: HashSet<T> = HashSet()


    /**
     * [T]를 생성하고 반환합니다.
     */
    abstract fun create(): T

    fun add(team: T) {
        teams.add(team)
    }

    fun remove(team: T) {
        teams.remove(team)
    }

    fun exists(team: T): Boolean {
        return teams.contains(team)
    }

    /**
     * [GamePlayer]가 소속된 [T]을 반환합니다.
     */
    fun getByGamePlayer(gamePlayer: GamePlayer): T? {
        return teams.firstOrNull { it.players.contains(gamePlayer) }
    }

    /**
     * [GamePlayer]가 탈락하지 않고 온라인 상태인 [T]의 [List]를 반환합니다.
     */
    fun getNotDefeatedOnlineTeams(): List<T> {
        return teams.filter { it.players.filter { it.isOnline && it.joined && !it.defeated && !it.observer }.isNotEmpty() }
            .toList()
    }

    /**
     * 게임에 참여한 모든 [GamePlayer]를 여러명 묶어 [Team]을 생성합니다.
     */
    open fun matchingAndCreateTeams() {
        // TODO 파티 구현 후 파티는 파티끼리 팀원 구성해야함
        var matchingTeam: T? = null
        GameCore.playerManager.getPlayingPlayers().forEach {
            // 팀이 null이면 새 팀 생성
            if (matchingTeam == null) {
                matchingTeam = create()
                add(matchingTeam!!)
            }

            // 플레이어를 팀에 넣기
            it.team = matchingTeam
            matchingTeam!!.addPlayer(it)

            // 팀이 꽉차면 팀 null
            if (matchingTeam!!.size() >= GameCore.teamConfig.playerCountPerTeam) {
                matchingTeam = null
            }
        }
    }

}