package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun Sidebar.lines(
  direction: LinesBuilder.Direction = LinesBuilder.Direction.TOP_TO_BOTTOM,
  block: LinesBuilder.() -> Unit
) {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }

  val range = when (direction) {
    LinesBuilder.Direction.TOP_TO_BOTTOM -> IntProgression.fromClosedRange(0, maxLines - 1, 1)
    LinesBuilder.Direction.BOTTOM_TO_TOP -> IntProgression.fromClosedRange(maxLines - 1, 0, -1)
  }

  block(LinesBuilder(this, range))
}

@OptIn(ExperimentalContracts::class)
public fun Sidebar.lines(
  progression: IntProgression,
  block: LinesBuilder.() -> Unit
) {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }

  require(progression.first in 0 until maxLines) { "invalid range" }
  require(progression.last in 0 until maxLines) { "invalid range" }

  block(LinesBuilder(this, progression))
}

public typealias DynamicLine = () -> Unit

public class LinesBuilder internal constructor(private val sidebar: Sidebar, progression: IntProgression) {
  public enum class Direction {
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
  }

  private var iterator = progression.iterator()

  public fun line(value: Component) {
    sidebar.line(nextLine(), value)
  }

  public fun emptyLine() {
    line(empty())
  }

  public fun dynamicLine(valueProvider: () -> Component): DynamicLine {
    val lineIndex = nextLine()
    val dynamicLine = {
      sidebar.line(lineIndex, valueProvider())
    }
    dynamicLine()
    return dynamicLine
  }

  public fun skip(lines: Int = 1) {
    require(lines >= 1) { "lines cannot be less than 1" }
    repeat(lines) {
      if (!iterator.hasNext()) {
        error("reached end of range")
      }
      iterator.nextInt()
    }
  }

  public fun nextLine(): Int {
    while (iterator.hasNext()) {
      val line = iterator.nextInt()
      if (sidebar.line(line) == null) {
        return line
      }
    }

    error("reached end of range")
  }
}
