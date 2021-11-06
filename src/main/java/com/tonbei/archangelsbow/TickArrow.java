package com.tonbei.archangelsbow;

import org.bukkit.entity.Arrow;
import org.jetbrains.annotations.NotNull;

public abstract class TickArrow {

    private final Arrow arrow;

    public TickArrow(@NotNull Arrow _arrow) {
        arrow = _arrow;
    }

    @NotNull
    public Arrow getArrow() {
        return arrow;
    }

    public abstract void tick();
}
