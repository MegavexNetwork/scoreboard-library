package net.megavex.scoreboardlibrary.internal.nms.v1_18_R1.util;

import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.IChatBaseComponent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class NativeAdventureUtil {

    private static final Class<?> clazz;
    private static final MethodHandle constructor;
    private static final Field wrappedField;

    static {
        try {
            clazz = Class.forName("io.papermc.paper.adventure.AdventureComponent");

            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class, Component.class));
            wrappedField = clazz.getDeclaredField("wrapped");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private NativeAdventureUtil() {
    }

    public static IChatBaseComponent fromAdventureComponent(Component component) {
        try {
            return (IChatBaseComponent) constructor.invoke(component);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Component toAdventureComponent(IChatBaseComponent component) {
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
