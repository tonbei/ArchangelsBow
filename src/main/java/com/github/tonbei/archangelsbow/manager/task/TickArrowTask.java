package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.util.Log;
import com.github.tonbei.archangelsbow.entity.TickArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TickArrowTask extends BukkitRunnable {

    private boolean isPaperMC;
    private Method isTicking;

    public TickArrowTask() {
        try {
            isTicking = Entity.class.getMethod("isTicking");
            isPaperMC = true;
        } catch (NoSuchMethodException e) {
            isPaperMC = false;
        }

        Log.info("Server Type : " + (isPaperMC ? "PaperMC" : "Not PaperMC"));
    }

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, TickArrow>> iterator = TickArrowManager.getIterator();

        while (iterator.hasNext()) {
            TickArrow ta = iterator.next().getValue();

            if (!ta.isActive()) {
                iterator.remove();
                Log.debug("TickArrow is removed.");
                continue;
            }

            Arrow arrow = ta.getArrow();

            if (isPaperMC) {
                try {
                    if ((boolean) isTicking.invoke(arrow)) {
                        ta.tick();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.error(e);
                    isPaperMC = false;
                }
            }
            if (!isPaperMC) {
                Location lo = arrow.getLocation();
                World wo = arrow.getWorld();

                if (lo.isWorldLoaded() && wo.isChunkLoaded((int) Math.round(lo.getX()), (int) Math.round(lo.getZ())))
                    if (wo.getChunkAt(lo).isEntitiesLoaded())
                        ta.tick();
            }
        }
    }
}
