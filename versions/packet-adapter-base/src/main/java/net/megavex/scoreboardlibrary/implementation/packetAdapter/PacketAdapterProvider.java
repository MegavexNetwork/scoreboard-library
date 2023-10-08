package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketAdapterProvider {
  @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName);

  @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName);

  boolean isLegacy(@NotNull Player player);
}
