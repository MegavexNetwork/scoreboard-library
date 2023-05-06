package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebar
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

public fun ComponentSidebar.components(block: ComponentSidebarBuilder.() -> Unit) {
  ComponentSidebarBuilder(this).apply(block)
}

public class ComponentSidebarBuilder internal constructor(private val sidebar: ComponentSidebar) {

  public fun staticLine(line: Component) {
    sidebar.addComponent(SidebarComponent.staticLine(line))
  }
}
