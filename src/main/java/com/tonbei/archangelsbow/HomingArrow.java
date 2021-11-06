/*
 *  This file contains the code under the MIT license.
 *  https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/LICENSE
 *
 *  Original Source : https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/src/main/java/moze_intel/projecte/gameObjs/entity/EntityHomingArrow.java
 */
package com.tonbei.archangelsbow;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomingArrow extends TickArrow {

    private LivingEntity target;
    private int newTargetCooldown = 0;

    public HomingArrow(@NotNull Arrow _arrow) {
        super(_arrow);
    }

    @Override
    public void tick() {

    }

    private void findNewTarget() {
        List<Entity> entities = this.getArrow().getNearbyEntities(8.0, 8.0, 8.0);
        List<LivingEntity> livingEntities = entities.stream()
                                                    .filter(e -> e instanceof LivingEntity && !(e instanceof Player))
                                                    .map(e -> (LivingEntity)e)
                                                    .collect(Collectors.toList());

        if(!livingEntities.isEmpty()) {
            livingEntities.sort(Comparator.comparing(HomingArrow.this::sqrDistance, Double::compare));
            target = livingEntities.get(0);
        }

        newTargetCooldown = 5;
    }

    private double sqrDistance(@NotNull Entity entity) {
        Location al = this.getArrow().getLocation();
        Location el = entity.getLocation();
        double dx = al.getX() - el.getX();
        double dy = al.getY() - el.getY();
        double dz = al.getZ() - el.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Nullable
    public LivingEntity getTarget() {
        return target;
    }

    private boolean hasTarget() {
        return target != null;
    }
}
