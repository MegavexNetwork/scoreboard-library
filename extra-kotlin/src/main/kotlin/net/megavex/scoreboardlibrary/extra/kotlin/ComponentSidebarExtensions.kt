package net.megavex.scoreboardlibrary.extra.kotlin

import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebar
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

public inline var ComponentSidebar.titleComponent: SidebarComponent
  get() = titleComponent()
  set(value) = titleComponent(value)

public operator fun ComponentSidebar.plusAssign(component: SidebarComponent) {
  addComponent(component)
}
