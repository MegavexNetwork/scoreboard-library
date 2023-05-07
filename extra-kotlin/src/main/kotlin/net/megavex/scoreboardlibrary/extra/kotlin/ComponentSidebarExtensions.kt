package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

public inline val ComponentSidebarLayout.titleComponent: SidebarComponent
  get() = titleComponent()

public inline val ComponentSidebarLayout.linesComponent: SidebarComponent
  get() = linesComponent()
