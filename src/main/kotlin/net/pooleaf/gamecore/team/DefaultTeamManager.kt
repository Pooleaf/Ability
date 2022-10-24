package net.pooleaf.gamecore.team

class DefaultTeamManager: TeamManager<Team>() {

    override fun create(): Team {
        return Team()
    }

}