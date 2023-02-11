package net.pooleaf.gamecore.v1.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.sql.daos.GameDao

class GameSqlManager : AbstractSqlManager(GameCore.gamePlugin as CorePlugin) {

    val gameDao = GameDao(this)

}