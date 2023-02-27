package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam
import net.megavex.scoreboardlibrary.api.team.TeamDisplay
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility

public inline val TeamDisplay.team: ScoreboardTeam get() = team()
public inline val TeamDisplay.entries: Collection<String> get() = entries()

public inline var TeamDisplay.displayName: Component
  get() = displayName()
  set(value) {
    displayName(value)
  }

public inline var TeamDisplay.prefix: Component
  get() = prefix()
  set(value) {
    prefix(value)
  }

public inline var TeamDisplay.suffix: Component
  get() = suffix()
  set(value) {
    suffix(value)
  }

public inline var TeamDisplay.isFriendlyFire: Boolean
  get() = friendlyFire()
  set(value) {
    friendlyFire(value)
  }

public inline var TeamDisplay.canSeeFriendlyInvisibles: Boolean
  get() = canSeeFriendlyInvisibles()
  set(value) {
    canSeeFriendlyInvisibles(value)
  }

public inline var TeamDisplay.nameTagVisibility: NameTagVisibility
  get() = nameTagVisibility()
  set(value) {
    nameTagVisibility(value)
  }

public inline var TeamDisplay.collisionRule: CollisionRule
  get() = collisionRule()
  set(value) {
    collisionRule(value)
  }

public inline var TeamDisplay.playerColor: NamedTextColor?
  get() = playerColor()
  set(value) {
    playerColor(value)
  }
