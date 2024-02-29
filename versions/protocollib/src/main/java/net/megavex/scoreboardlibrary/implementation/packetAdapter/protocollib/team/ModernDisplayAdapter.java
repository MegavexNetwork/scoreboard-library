package net.megavex.scoreboardlibrary.implementation.packetAdapter.protocollib.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedTeamParameters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;
import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

// 1.13+
public class ModernDisplayAdapter implements TeamDisplayPacketAdapter {
  private final ProtocolManager pm;
  private final String teamName;
  private final ImmutableTeamProperties<Component> properties;

  public ModernDisplayAdapter(@NotNull ProtocolManager pm, @NotNull String teamName, @NotNull ImmutableTeamProperties<Component> properties) {
    this.pm = pm;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
    int modeIdx = MinecraftVersion.AQUATIC_UPDATE.atOrAbove() ? 0 : 1;
    packet.getIntegers().write(modeIdx, TeamConstants.MODE_REMOVE);
    packet.getStrings().write(0, teamName);
    packet.getSpecificModifier(Collection.class).write(0, players);

    for (Player player : players) {
      pm.sendServerPacket(player, packet);
    }
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    PacketSender<PacketContainer> sender = pm::sendServerPacket;
    char legacyChar = LegacyFormatUtil.getChar(properties.playerColor());
    EnumWrappers.ChatFormatting color = EnumWrappers.ChatFormatting.fromBukkit(ChatColor.getByChar(legacyChar));
    boolean usesParameters = MinecraftVersion.CAVES_CLIFFS_1.atOrAbove();
    boolean usesComponents = MinecraftVersion.AQUATIC_UPDATE.atOrAbove();

    LocalePacketUtil.sendLocalePackets(sender, players, locale -> {
      PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
      int modeIdx = usesComponents ? 0 : 1;
      packet.getIntegers().write(modeIdx, TeamConstants.mode(packetType));
      packet.getStrings().write(0, teamName);
      packet.getSpecificModifier(Collection.class).write(0, properties.entries());

      Component displayName = GlobalTranslator.render(properties.displayName(), locale);
      Component prefix = GlobalTranslator.render(properties.prefix(), locale);
      Component suffix = GlobalTranslator.render(properties.suffix(), locale);

      if (usesParameters) {
        WrappedTeamParameters params = WrappedTeamParameters.newBuilder()
          .displayName(wrapComponent(displayName))
          .prefix(wrapComponent(prefix))
          .suffix(wrapComponent(suffix))
          .nametagVisibility(properties.nameTagVisibility().key())
          .collisionRule(properties.collisionRule().key())
          .color(color)
          .options(properties.packOptions())
          .build();
        packet.getOptionalTeamParameters().write(0, Optional.of(params));
      } else {
        packet.getIntegers().write(modeIdx + 1, properties.packOptions());
        if (usesComponents) {
          packet.getChatFormattings().write(0, color);
        } else {
          packet.getIntegers().write(0, color.ordinal());
        }

        if (usesComponents) {
          packet.getChatComponents()
            .write(0, wrapComponent(displayName))
            .write(1, wrapComponent(prefix))
            .write(2, wrapComponent(suffix));

          packet.getStrings()
            .write(1, properties.nameTagVisibility().key())
            .write(2, properties.collisionRule().key());
        } else {
          packet.getStrings()
            .write(1, toLegacy(displayName))
            .write(2, toLegacy(prefix))
            .write(3, toLegacy(suffix))
            .write(4, properties.nameTagVisibility().key())
            .writeSafely(5, properties.collisionRule().key()); // Added in 1.9
        }
      }

      return packet;
    });
  }

  private @NotNull WrappedChatComponent wrapComponent(@NotNull Component component) {
    return WrappedChatComponent.fromJson(gson().serialize(component));
  }

  private @NotNull String toLegacy(@NotNull Component component) {
    return limitLegacyText(legacySection().serialize(component), TeamConstants.LEGACY_CHAR_LIMIT);
  }
}
