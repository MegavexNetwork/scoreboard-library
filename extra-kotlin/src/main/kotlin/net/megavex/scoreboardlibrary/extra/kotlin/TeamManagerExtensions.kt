package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam
import net.megavex.scoreboardlibrary.api.team.TeamManager
import org.bukkit.entity.Player

public inline val TeamManager.teams: Collection<ScoreboardTeam> get() = teams()
public operator fun TeamManager.get(name: String): ScoreboardTeam? = team(name)
public inline val TeamManager.players: Collection<Player> get() = players()
