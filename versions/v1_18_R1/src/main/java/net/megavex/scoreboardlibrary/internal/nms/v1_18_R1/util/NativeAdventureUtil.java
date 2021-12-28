package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Field;

public final class NativeAdventureUtil {

    private static final Class<?> clazz;
    private static final Field wrappedField;

    static {
        try {
            clazz = Class.forName("io.papermc.paper.adventure.AdventureComponent");
            wrappedField = clazz.getDeclaredField("wrapped");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private NativeAdventureUtil() {
    }

    public static net.minecraft.network.chat.Component fromAdventureComponent(Component component) {
        return new AdventureComponent(component);
    }

    public static Component toAdventureComponent(net.minecraft.network.chat.Component component) {
        if (clazz.isInstance(component)) {
            try {
                return (Component) wrappedField.get(component);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException("Not an AdventureComponent");
        }
    }
}
