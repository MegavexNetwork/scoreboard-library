package net.megavex.scoreboardlibrary.api.sidebar.line;

import net.kyori.adventure.text.Component;

import java.util.Objects;

record StaticLine(Component value) implements SidebarLine {

    StaticLine(Component value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    @Override
    public Component computeValue() {
        return value;
    }
}
