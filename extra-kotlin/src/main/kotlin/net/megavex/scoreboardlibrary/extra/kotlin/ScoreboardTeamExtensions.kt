package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam
import net.megavex.scoreboardlibrary.api.team.TeamInfo
import net.megavex.scoreboardlibrary.api.team.TeamManager
import org.bukkit.entity.Player

public inline val ScoreboardTeam.teamManager: TeamManager get() = teamManager()
public inline val ScoreboardTeam.name: String get() = name()
public inline val ScoreboardTeam.globalInfo: TeamInfo get() = globalInfo()
public operator fun ScoreboardTeam.get(player: Player): TeamInfo = teamInfo(player)
public operator fun ScoreboardTeam.set(player: Player, teamInfo: TeamInfo): Unit = teamInfo(player, teamInfo)
