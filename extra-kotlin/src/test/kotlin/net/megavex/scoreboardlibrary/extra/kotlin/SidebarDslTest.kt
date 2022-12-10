package net.megavex.scoreboardlibrary.extra.kotlin

import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class SidebarDslTest {
  private val scoreboardLibrary = NoopScoreboardLibrary()

  @Test
  fun `puts lines in correct places`() {
    val sidebar = scoreboardLibrary.createSidebar()
    val line = text("Hello")
    sidebar.lines {
      emptyLine()
      line(line)
      emptyLine()
    }

    assertEquals(empty(), sidebar.line(0))
    assertEquals(line, sidebar.line(1))
    assertEquals(empty(), sidebar.line(2))
  }

  @Test
  fun `respects maxLines property`() {
    val sidebar = scoreboardLibrary.createSidebar()
    assertThrows<IllegalStateException> {
      sidebar.lines {
        repeat(sidebar.maxLines + 1) {
          emptyLine()
        }
      }
    }
  }

  @Test
  fun `skips existing lines`() {
    val sidebar = scoreboardLibrary.createSidebar()
    sidebar.lines {
      line(text("1"))
    }

    sidebar.lines {
      line(text("2"))
    }

    sidebar.lines {
      line(text("3"))
    }

    assertEquals(text("1"), sidebar.line(0))
    assertEquals(text("2"), sidebar.line(1))
    assertEquals(text("3"), sidebar.line(2))
  }

  @Test
  fun `throws if there are no available lines`() {
    val sidebar = scoreboardLibrary.createSidebar()
    sidebar.lines {
      repeat(sidebar.maxLines) {
        emptyLine()
      }
    }

    assertThrows<IllegalStateException> {
      sidebar.lines {
        line(text("this shouldn't work"))
      }
    }
  }

  @Test
  fun directions() {
    val sidebar = scoreboardLibrary.createSidebar(5)
    sidebar.lines(direction = LinesBuilder.Direction.BOTTOM_TO_TOP) {
      line(text("1"))
      line(text("2"))
      line(text("3"))
    }

    assertEquals(text("1"), sidebar.line(4))
    assertEquals(text("2"), sidebar.line(3))
    assertEquals(text("3"), sidebar.line(2))
  }

  @Test
  fun skip() {
    val sidebar = scoreboardLibrary.createSidebar()
    sidebar.lines {
      line(text("1"))
      skip(lines = 2)
      line(text("3"))
    }

    sidebar.lines {
      line(text("2"))
    }

    assertEquals(text("1"), sidebar.line(0))
    assertEquals(text("2"), sidebar.line(1))
    assertEquals(null, sidebar.line(2))
    assertEquals(text("3"), sidebar.line(3))
  }

  @Test
  fun `detects invalid progression`() {
    val sidebar = scoreboardLibrary.createSidebar()
    assertThrows<IllegalArgumentException> {
      sidebar.lines(progression = -1..5) {}
    }

    assertThrows<IllegalArgumentException> {
      sidebar.lines(progression = 0..sidebar.maxLines) {}
    }
  }

  @Test
  fun `dynamic line`() {
    val sidebar = scoreboardLibrary.createSidebar()
    var test = 0
    var dynamicLine: LinesBuilder.DynamicLine

    sidebar.lines {
      dynamicLine = dynamicLine { text(test) }
    }

    assertEquals(text(test), sidebar.line(0))
    test++
    dynamicLine.update()
    assertEquals(text(test), sidebar.line(0))
  }
}
