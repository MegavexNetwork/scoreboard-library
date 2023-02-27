package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam
import net.megavex.scoreboardlibrary.api.team.TeamDisplay
import net.megavex.scoreboardlibrary.api.team.TeamManager
import org.bukkit.entity.Player

public inline val ScoreboardTeam.teamManager: TeamManager get() = teamManager()
public inline val ScoreboardTeam.name: String get() = name()
public inline val ScoreboardTeam.defaultDisplay: TeamDisplay get() = defaultDisplay()
public operator fun ScoreboardTeam.get(player: Player): TeamDisplay = display(player)
public operator fun ScoreboardTeam.set(player: Player, teamDisplay: TeamDisplay): Unit = display(player, teamDisplay)
