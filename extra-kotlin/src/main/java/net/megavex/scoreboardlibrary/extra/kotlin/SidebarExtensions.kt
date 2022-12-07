package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import java.util.*

public val Sidebar.maxLines: Int get() = maxLines()
public val Sidebar.locale: Locale? get() = locale()
public operator fun Sidebar.get(line: Int): Component? = line(line)
public operator fun Sidebar.set(line: Int, value: Component?): Unit = line(line, value)
public var Sidebar.title: Component
  get() = title()
  set(value) {
    title(value)
  }
