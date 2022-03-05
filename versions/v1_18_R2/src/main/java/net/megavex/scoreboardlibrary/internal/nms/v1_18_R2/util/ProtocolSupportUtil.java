package net.megavex.scoreboardlibrary.internal.nms.v1_18_R2.util;

import org.bukkit.entity.Player;
import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

public final class ProtocolSupportUtil {

    private ProtocolSupportUtil() {
    }

    public static boolean isLegacy(Player player) {
        Connection connection = ProtocolSupportAPI.getConnection(player);
        if (connection == null) return false;

        // TODO: may work on older versions, need to test that
        return connection.getVersion().isBetween(ProtocolVersion.MINECRAFT_1_12_2, ProtocolVersion.MINECRAFT_1_8);
    }
}
