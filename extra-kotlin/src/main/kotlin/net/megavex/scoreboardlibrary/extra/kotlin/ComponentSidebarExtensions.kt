package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentBasedSidebar
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

public inline var ComponentBasedSidebar.titleComponent: SidebarComponent
  get() = titleComponent()
  set(value) = titleComponent(value)

public inline var ComponentBasedSidebar.rootComponent: SidebarComponent
  get() = rootComponent()
  set(value) = rootComponent(value)
