package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.SpigotTeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.PacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<Packet<?>>, ComponentProvider {
  private boolean isNativeAdventure;

  public PacketAdapterProviderImpl() {
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
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return isNativeAdventure
      ? new PaperObjectivePacketAdapter(this, this, objectiveName)
      : new SpigotObjectivePacketAdapter(this, this, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return isNativeAdventure
      ? new PaperTeamsPacketAdapterImpl(this, this, teamName)
      : new SpigotTeamsPacketAdapter(this, this, teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    return LineRenderingStrategy.MODERN;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    PacketUtil.sendPacket(player, packet);
  }

  @Override
  public net.minecraft.network.chat.@NotNull Component fromAdventure(@NotNull Component adventure, @Nullable Locale locale) {
    if (isNativeAdventure) {
      return NativeAdventureUtil.fromAdventureComponent(adventure);
    }

    Component translated = adventure;
    if (locale != null) {
      translated = GlobalTranslator.render(adventure, locale);
    }

    JsonElement json = gson().serializeToTree(translated);
    // TODO
    return Objects.requireNonNull(net.minecraft.network.chat.Component.Serializer.fromJson(json, CraftRegistry.getMinecraftRegistry()));
  }
}
