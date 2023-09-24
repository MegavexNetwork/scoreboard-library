package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.sidebar.PaperSidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.sidebar.SidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.PacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<Packet<?>> {
  private final ClientboundSetDisplayObjectivePacket displayPacket = createSidebarDisplayPacket();
  private final ClientboundSetObjectivePacket removePacket = createRemoveSidebarPacket();
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

  private ClientboundSetDisplayObjectivePacket createSidebarDisplayPacket() {
    ClientboundSetDisplayObjectivePacket displayPacket;
    try {
      Class.forName("net.minecraft.world.scores.DisplaySlot"); // Added in 1.20.2
      displayPacket = new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, null);
    } catch (ClassNotFoundException ignored) {
      ConstructorAccessor<ClientboundSetDisplayObjectivePacket> constructor = ReflectUtil.constructorAccessor(
        ClientboundSetDisplayObjectivePacket.class,
        int.class,
        Objective.class
      );
      displayPacket = constructor.invoke(POSITION_SIDEBAR, null);
    }

    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(displayPacket, objectiveName());
    return displayPacket;
  }

  private ClientboundSetObjectivePacket createRemoveSidebarPacket() {
    ClientboundSetObjectivePacket packet = ReflectUtil.findPacketConstructor(ClientboundSetObjectivePacket.class).invoke();
    PacketAccessors.SET_OBJECTIVE_NAME.set(packet, objectiveName());
    PacketAccessors.SET_OBJECTIVE_MODE.set(packet, OBJECTIVE_MODE_REMOVE);
    return packet;
  }

  @Override
  public @NotNull SidebarPacketAdapter<Packet<?>, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar) {
    return isNativeAdventure ? new PaperSidebarPacketAdapterImpl(this, sidebar) : new SidebarPacketAdapterImpl(this, sidebar);
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

  public net.minecraft.network.chat.Component fromAdventure(Component component, Locale locale) {
    if (isNativeAdventure) return NativeAdventureUtil.fromAdventureComponent(component);

    component = GlobalTranslator.render(component, Objects.requireNonNull(locale));
    return net.minecraft.network.chat.Component.Serializer.fromJson(gson().serializeToTree(component));
  }
}
