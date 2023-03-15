package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.sidebar;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.PacketAdapterImpl;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SidebarPacketAdapterImpl extends AbstractSidebarImpl {
  private final ClientboundSetObjectivePacket createPacket;
  private final ClientboundSetObjectivePacket updatePacket;

  public SidebarPacketAdapterImpl(PacketAdapterImpl impl, Sidebar sidebar) {
    super(impl, sidebar);

    var locale = sidebar.locale();
    if (locale != null) {
      createPacket = objectivePacketConstructor.invoke();
      createObjectivePacket(createPacket, MODE_CREATE);
      updateDisplayName(createPacket, sidebar.title(), locale);

      updatePacket = objectivePacketConstructor.invoke();
      createObjectivePacket(updatePacket, MODE_UPDATE);
      updateDisplayName(updatePacket, sidebar.title(), locale);
    } else {
      createPacket = null;
      updatePacket = null;
    }
  }

  private void updateDisplayName(ClientboundSetObjectivePacket packet, Component displayName, Locale locale) {
    var vanilla = packetAdapter().fromAdventure(displayName, locale);
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, vanilla);
  }

  @Override
  public void updateTitle(@NotNull Component displayName) {
    var locale = sidebar().locale();
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
      LocalePacketUtilities.sendLocalePackets(packetAdapter().localeProvider, sidebar().locale(), packetAdapter(), players, locale -> {
        var packet = objectivePacketConstructor.invoke();
        createObjectivePacket(packet, type == ObjectivePacket.CREATE ? MODE_CREATE : MODE_UPDATE);
        updateDisplayName(packet, sidebar().title(), locale);
        return packet;
      });
    }
  }
}
