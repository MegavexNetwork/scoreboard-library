package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import org.bukkit.entity.Player
import java.util.*

public inline val Sidebar.maxLines: Int get() = maxLines()
public inline val Sidebar.locale: Locale? get() = locale()
public operator fun Sidebar.get(line: Int): Component? = line(line)
public operator fun Sidebar.set(line: Int, value: Component?): Unit = line(line, value)
public inline var Sidebar.title: Component
  get() = title()
  set(value) {
    title(value)
  }
public inline val Sidebar.players: Collection<Player> get() = players()
