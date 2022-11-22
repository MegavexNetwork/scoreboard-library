package net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.nms.base.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.sidebar.PaperSidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.sidebar.SidebarPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.team.TeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.nms.v1_19_R1.util.NativeAdventureUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static net.megavex.scoreboardlibrary.implementation.nms.base.util.UnsafeUtilities.getField;
import static net.megavex.scoreboardlibrary.implementation.nms.base.util.UnsafeUtilities.setField;

public class NMSImpl extends ScoreboardLibraryPacketAdapter<Packet<?>> {
  private final ClientboundSetDisplayObjectivePacket displayPacket = new ClientboundSetDisplayObjectivePacket(1, null);
  private final ClientboundSetObjectivePacket removePacket = UnsafeUtilities.findPacketConstructor(ClientboundSetObjectivePacket.class, MethodHandles.lookup()).invoke();
  private boolean nativeAdventure;

  public NMSImpl() {
    setField(getField(ClientboundSetDisplayObjectivePacket.class, "b"), displayPacket, objectiveName);
    setField(getField(ClientboundSetObjectivePacket.class, "d"), removePacket, objectiveName);
    UnsafeUtilities.UNSAFE.putInt(removePacket, UnsafeUtilities.UNSAFE.objectFieldOffset(getField(ClientboundSetObjectivePacket.class, "g")), 1);

    try {
      Class.forName("io.papermc.paper.adventure.PaperAdventure");
      nativeAdventure = true;
    } catch (ClassNotFoundException ignored) {
    }
  }


  @Override
  public @NotNull SidebarPacketAdapter<Packet<?>, ?> createSidebarNMS(@NotNull Sidebar sidebar) {
    return nativeAdventure ? new PaperSidebarPacketAdapterImpl(this, sidebar):new SidebarPacketAdapterImpl(this, sidebar);
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
  public @NotNull TeamsPacketAdapter<?, ?> createTeamNMS(@NotNull String teamName) {
    return nativeAdventure ? new PaperTeamsPacketAdapterImpl(this, teamName):new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return false;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    ((CraftPlayer) player).getHandle().connection.send(packet);
  }

  public net.minecraft.network.chat.Component fromAdventure(Component component, Locale locale, ComponentTranslator componentTranslator) {
    if (nativeAdventure) return NativeAdventureUtil.fromAdventureComponent(component);

    component = componentTranslator.translate(component, Objects.requireNonNull(locale));
    return net.minecraft.network.chat.Component.Serializer.fromJson(gson().serializeToTree(component));
  }
}
