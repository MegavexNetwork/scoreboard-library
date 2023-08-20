package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.sidebar;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SidebarPacketAdapterImpl extends AbstractSidebarImpl {
  private final ClientboundSetObjectivePacket createPacket;
  private final ClientboundSetObjectivePacket updatePacket;

  public SidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    Locale locale = sidebar.locale();
    if (locale != null) {
      createPacket = PacketAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
      createObjectivePacket(createPacket, MODE_CREATE);
      updateDisplayName(createPacket, sidebar.title(), locale);

      updatePacket = PacketAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
      createObjectivePacket(updatePacket, MODE_UPDATE);
      updateDisplayName(updatePacket, sidebar.title(), locale);
    } else {
      createPacket = null;
      updatePacket = null;
    }
  }

  private void updateDisplayName(ClientboundSetObjectivePacket packet, Component displayName, Locale locale) {
    net.minecraft.network.chat.Component vanilla = packetAdapter().fromAdventure(displayName, locale);
    PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(packet, vanilla);
  }

  @Override
  public void updateTitle(@NotNull Component displayName) {
    Locale locale = sidebar().locale();
    if (locale != null) {
      updateDisplayName(createPacket, displayName, locale);
      updateDisplayName(updatePacket, displayName, locale);
    }
  }

  @Override
  public void sendObjectivePacket(@NotNull Collection<Player> players, @NotNull ObjectivePacket type) {
    if (sidebar().locale() != null) {
      packetAdapter().sendPacket(players, type == ObjectivePacket.CREATE ? createPacket : updatePacket);
    } else {
      LocalePacketUtil.sendLocalePackets(packetAdapter().localeProvider, sidebar().locale(), packetAdapter(), players, locale -> {
        ClientboundSetObjectivePacket packet = PacketAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
        createObjectivePacket(packet, type == ObjectivePacket.CREATE ? MODE_CREATE : MODE_UPDATE);
        updateDisplayName(packet, sidebar().title(), locale);
        return packet;
      });
    }
  }
}
