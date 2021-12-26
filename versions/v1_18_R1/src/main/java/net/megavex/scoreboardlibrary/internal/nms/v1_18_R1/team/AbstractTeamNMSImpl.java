package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.internal.nms.base.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.internal.nms.base.TeamNMS;
import net.megavex.scoreboardlibrary.internal.nms.base.util.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.NMSImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities.getField;

public abstract class AbstractTeamNMSImpl extends TeamNMS<Packet<?>, NMSImpl> {

    protected static final Field displayNameField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "a"),
            prefixField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "b"),
            suffixField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "c"),
            entriesField = getField(ClientboundSetPlayerTeamPacket.class, "j"),
            nameTagVisibilityField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "d"),
            collisionRuleField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "e"),
            colorField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "f"),
            optionsField = getField(ClientboundSetPlayerTeamPacket.Parameters.class, "g");
    private static final Constructor<ClientboundSetPlayerTeamPacket> teamPacketConstructor;

    static {
        try {
            teamPacketConstructor = ClientboundSetPlayerTeamPacket.class.getDeclaredConstructor(String.class, int.class, Optional.class, Collection.class);
            teamPacketConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected ClientboundSetPlayerTeamPacket removePacket;

    AbstractTeamNMSImpl(NMSImpl impl, String teamName) {
        super(impl, teamName);
    }

    public static ClientboundSetPlayerTeamPacket createTeamsPacket(
            int method,
            String name,
            ClientboundSetPlayerTeamPacket.Parameters parameters,
            Collection<String> entries
    ) {
        try {
            return teamPacketConstructor.newInstance(name, method, Optional.ofNullable(parameters), entries == null ? ImmutableList.of() : entries);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeTeam(Iterable<Player> players) {
        if (removePacket == null) {
            removePacket = createTeamsPacket(MODE_REMOVE, teamName, null, null);
        }
        impl.sendPacket(players, removePacket);
    }

    @Override
    public TeamInfoNMS<String> createLegacyTeamInfoNMS(ImmutableTeamProperties<String> properties) {
        if (impl.protocolSupport()) return new LegacyTeamInfoNMS(properties, teamName);
        return super.createLegacyTeamInfoNMS(properties);
    }

    abstract class TeamInfoNMSImpl extends TeamNMS.TeamInfoNMS<Component> {

        static final UnsafeUtilities.PacketConstructor<ClientboundSetPlayerTeamPacket.Parameters> parametersConstructor =
                UnsafeUtilities.findPacketConstructor(ClientboundSetPlayerTeamPacket.Parameters.class, MethodHandles.lookup());

        public TeamInfoNMSImpl(ImmutableTeamProperties<Component> properties) {
            super(properties);
        }

        @Override
        public void addEntries(Collection<Player> players, Collection<String> entries) {
            teamEntry(players, entries, MODE_ADD_ENTRIES);
        }

        @Override
        public void removeEntries(Collection<Player> players, Collection<String> entries) {
            teamEntry(players, entries, MODE_REMOVE_ENTRIES);
        }

        private void teamEntry(Collection<Player> players, Collection<String> entries, int action) {
            impl.sendPacket(players, createTeamsPacket(action, teamName, null, entries));
        }

        protected void fillTeamPacket(ClientboundSetPlayerTeamPacket packet, Collection<String> entries) {
            if (packet.getPlayers() != entries) {
                UnsafeUtilities.setField(entriesField, packet, entries);
            }
        }

        protected void fillParameters(ClientboundSetPlayerTeamPacket.Parameters parameters, Locale locale) {
            String key = properties.nameTagVisibility().key();
            if (!Objects.equals(parameters.getNametagVisibility(), key)) {
                UnsafeUtilities.setField(nameTagVisibilityField, parameters, key);
            }

            key = properties.collisionRule().key();
            if (!Objects.equals(parameters.getCollisionRule(), key))
                UnsafeUtilities.setField(collisionRuleField, parameters, key);

            char c = LegacyFormatUtil.getChar(properties.playerColor());
            if (parameters.getColor() == null || parameters.getColor().code != c)
                UnsafeUtilities.setField(colorField, parameters, ChatFormatting.getByCode(c));

            int options = properties.packOptions();
            if (parameters.getOptions() != options)
                UnsafeUtilities.UNSAFE.putInt(parameters, UnsafeUtilities.UNSAFE.objectFieldOffset(optionsField), options);
        }
    }
}
