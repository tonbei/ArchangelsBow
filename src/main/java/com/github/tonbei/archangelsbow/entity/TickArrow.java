package com.github.tonbei.archangelsbow.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class TickArrow {

    private final Arrow arrow;
    protected final Random random = new Random();

    public TickArrow(@NotNull Arrow arrow) {
        Validate.notNull(arrow, "Arrow must not be null.");
        this.arrow = arrow;
    }

    @NotNull
    public Arrow getArrow() {
        return arrow;
    }

    public abstract void tick();

    public abstract boolean isActive();

    protected void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vector vector = new Vector(x, y, z).normalize()
                                            .add(new Vector(random.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                                                            random.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                                                            random.nextGaussian() * (double) 0.0075F * (double) inaccuracy))
                                            .multiply((double) velocity);
        arrow.setVelocity(vector);
        float HorizontalDis = (float) Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
        float yaw = (float) (Math.atan2(vector.getX(), vector.getZ()) * (double) (180F / (float) Math.PI));
        float pitch = (float) (Math.atan2(vector.getY(), HorizontalDis) * (double) (180F / (float) Math.PI));
        arrow.setRotation(yaw, pitch);
    }
}
