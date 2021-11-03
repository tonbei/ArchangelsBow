package com.tonbei.archangelsbow;

import org.bukkit.entity.Arrow;

public abstract class TickArrow {

    private final Arrow arrow;

    public TickArrow(Arrow _arrow){
        arrow = _arrow;
    }

    public Arrow getArrow(){
        return arrow;
    }

    public abstract void tick();
}
