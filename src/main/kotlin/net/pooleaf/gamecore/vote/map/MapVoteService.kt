package net.pooleaf.gamecore.vote.map

class MapVoteService {

    val mapVote = MapVote()
    val mapVoteGui = MapVoteGui(mapVote)


    fun initVote() {
        mapVote.clear()
        mapVoteGui.updateAsynchronously()
    }

}