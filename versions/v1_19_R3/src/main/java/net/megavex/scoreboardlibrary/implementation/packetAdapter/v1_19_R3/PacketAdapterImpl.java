package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.sidebar.PaperSidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.sidebar.SidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.util.NativeAdventureUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities.getField;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities.setField;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<Packet<?>> {
  private final ClientboundSetDisplayObjectivePacket displayPacket = new ClientboundSetDisplayObjectivePacket(POSITION_SIDEBAR, null);
  private final ClientboundSetObjectivePacket removePacket = UnsafeUtilities.findPacketConstructor(ClientboundSetObjectivePacket.class, MethodHandles.lookup()).invoke();
  private boolean nativeAdventure;

  public PacketAdapterImpl() {
    setField(getField(ClientboundSetDisplayObjectivePacket.class, "b"), displayPacket, objectiveName);
    setField(getField(ClientboundSetObjectivePacket.class, "d"), removePacket, objectiveName);
    UnsafeUtilities.UNSAFE.putInt(removePacket, UnsafeUtilities.UNSAFE.objectFieldOffset(getField(ClientboundSetObjectivePacket.class, "g")), OBJECTIVE_MODE_REMOVE);

    try {
      Class.forName("io.papermc.paper.adventure.PaperAdventure");
      nativeAdventure = true;
    } catch (ClassNotFoundException ignored) {
    }
  }


  @Override
  public @NotNull SidebarPacketAdapter<Packet<?>, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar) {
    return nativeAdventure ? new PaperSidebarPacketAdapterImpl(this, sidebar) : new SidebarPacketAdapterImpl(this, sidebar);
  }

  @Override
  public void displaySidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, displayPacket);
  }

  @Override
  public void removeSidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName) {
    return nativeAdventure ? new PaperTeamsPacketAdapterImpl(this, teamName) : new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return false;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    ((CraftPlayer) player).getHandle().connection.send(packet);
  }

  public net.minecraft.network.chat.Component fromAdventure(Component component, Locale locale) {
    if (nativeAdventure) return NativeAdventureUtil.fromAdventureComponent(component);

    component = GlobalTranslator.render(component, Objects.requireNonNull(locale));
    return net.minecraft.network.chat.Component.Serializer.fromJson(gson().serializeToTree(component));
  }
}
