package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam
import net.megavex.scoreboardlibrary.api.team.TeamInfo
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility

public inline val TeamInfo.team: ScoreboardTeam get() = team()
public inline val TeamInfo.entries: Collection<String> get() = entries()

public inline var TeamInfo.displayName: Component
  get() = displayName()
  set(value) {
    displayName(value)
  }

public inline var TeamInfo.prefix: Component
  get() = prefix()
  set(value) {
    prefix(value)
  }

public inline var TeamInfo.suffix: Component
  get() = suffix()
  set(value) {
    suffix(value)
  }

public inline var TeamInfo.isFriendlyFire: Boolean
  get() = friendlyFire()
  set(value) {
    friendlyFire(value)
  }

public inline var TeamInfo.canSeeFriendlyInvisibles: Boolean
  get() = canSeeFriendlyInvisibles()
  set(value) {
    canSeeFriendlyInvisibles(value)
  }

public inline var TeamInfo.nameTagVisibility: NameTagVisibility
  get() = nameTagVisibility()
  set(value) {
    nameTagVisibility(value)
  }

public inline var TeamInfo.collisionRule: CollisionRule
  get() = collisionRule()
  set(value) {
    collisionRule(value)
  }

public inline var TeamInfo.playerColor: NamedTextColor?
  get() = playerColor()
  set(value) {
    playerColor(value)
  }
