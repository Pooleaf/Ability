package net.pooleaf.gamecore.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.sql.daos.GameDao

class GameSqlManager : AbstractSqlManager(GameCore.gamePlugin as CorePlugin) {

    val gameDao = GameDao(this)

}