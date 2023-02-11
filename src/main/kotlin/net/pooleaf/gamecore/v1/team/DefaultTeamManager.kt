package net.pooleaf.gamecore.v1.team

class DefaultTeamManager: TeamManager<Team>() {

    override fun create(): Team {
        return Team()
    }

}