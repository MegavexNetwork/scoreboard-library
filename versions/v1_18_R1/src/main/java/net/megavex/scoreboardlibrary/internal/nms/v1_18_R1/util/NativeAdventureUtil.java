package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Field;

public final class NativeAdventureUtil {

    private NativeAdventureUtil() {
    }

    public static net.minecraft.network.chat.Component fromAdventureComponent(Component component) {
        return new AdventureComponent(component);
    }
}
