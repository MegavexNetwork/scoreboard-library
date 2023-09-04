package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerNameProviderTest {
  @Test
  void duplicateTest() {
    List<String> names = PlayerNameProvider.provideLinePlayerNames(100);
    Set<String> uniqueNames = new HashSet<>(names.size());
    for (String name : names) {
      assertTrue(uniqueNames.add(name), "duplicate player name " + name);
    }
  }
}
