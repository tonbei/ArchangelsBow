package com.tonbei.archangelsbow;

import org.bukkit.entity.Arrow;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TickArrow {

    private final Arrow arrow;

    public TickArrow(@NotNull Arrow arrow) {
        this.arrow = Objects.requireNonNull(arrow);
    }

    @NotNull
    public Arrow getArrow() {
        return arrow;
    }

    public abstract void tick();

    public abstract boolean isActive();
}
