package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.PacketUtil;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<Packet<?>> {
  private boolean isNativeAdventure;

  public PacketAdapterImpl() {
    try {
      Class.forName("io.papermc.paper.adventure.PaperAdventure");

      // Hide from relocation checkers
      String notRelocatedPackage = "net.ky".concat("ori.adventure.text");

      // The native adventure optimisations only work when the adventure library isn't relocated
      if (Component.class.getPackage().getName().equals(notRelocatedPackage)) {
        isNativeAdventure = true;
      }
    } catch (ClassNotFoundException ignored) {
    }
  }

  @Override
  public @NotNull ObjectivePacketAdapter<?, ?> createObjectiveAdapter(@NotNull String objectiveName) {
    return isNativeAdventure ? new PaperObjectivePacketAdapter(this, objectiveName) : new SpigotObjectivePacketAdapter(this, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName) {
    return isNativeAdventure ? new PaperTeamsPacketAdapterImpl(this, teamName) : new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return false;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    PacketUtil.sendPacket(player, packet);
  }

  public net.minecraft.network.chat.Component fromAdventure(@NotNull Component adventure, @Nullable Locale locale) {
    if (isNativeAdventure) {
      return NativeAdventureUtil.fromAdventureComponent(adventure);
    }

    Component rendered;
    if (locale != null) {
      rendered = GlobalTranslator.render(adventure, Objects.requireNonNull(locale));
    } else {
      rendered = adventure;
    }

    return net.minecraft.network.chat.Component.Serializer.fromJson(gson().serializeToTree(rendered));
  }
}
