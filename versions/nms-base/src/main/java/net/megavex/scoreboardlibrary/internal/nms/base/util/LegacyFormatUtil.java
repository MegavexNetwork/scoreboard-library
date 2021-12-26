package net.megavex.scoreboardlibrary.internal.nms.base.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.parseChar;
import static net.kyori.adventure.translation.GlobalTranslator.render;

public final class LegacyFormatUtil {

    private static final Map<NamedTextColor, Character> legacyMap;

    static {
        ChatColor[] values = ChatColor.values();
        legacyMap = CollectionProvider.map(values.length);
        for (ChatColor value : values) {
            if (!value.isColor()) continue;

            char c = value.getChar();
            LegacyFormat format = Objects.requireNonNull(parseChar(c));
            legacyMap.put((NamedTextColor) format.color(), c);
        }
    }

    private LegacyFormatUtil() {
    }

    public static String limitLegacyText(String text, int limit) {
        if (text.length() <= limit) {
            return text;
        }

        int lastNotColorCharIndex = limit - 1;
        while (text.charAt(lastNotColorCharIndex) == ChatColor.COLOR_CHAR) {
            lastNotColorCharIndex--;
        }
        return text.substring(0, lastNotColorCharIndex + 1);
    }

    public static String serialize(@Nullable Component component, Locale locale) {
        if (component == null || component == empty()) return "";

        return legacySection().serialize(render(component, locale));
    }

    public static char getChar(NamedTextColor color) {
        if (color == null) return 'r';

        return legacyMap.getOrDefault(color, '\0');
    }
}
