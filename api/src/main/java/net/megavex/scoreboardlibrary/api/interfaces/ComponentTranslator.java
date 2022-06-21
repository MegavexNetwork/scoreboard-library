package net.megavex.scoreboardlibrary.api.interfaces;

import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

public interface ComponentTranslator {
  ComponentTranslator GLOBAL = GlobalTranslator::render;
  ComponentTranslator NONE = (component, locale) -> component;

  @NotNull Component translate(@NotNull Component component, @NotNull Locale locale);
}
