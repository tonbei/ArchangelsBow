package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.util.Log;
import com.github.tonbei.archangelsbow.arrow.TickArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.entity.Arrow;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TickArrowTask extends BukkitRunnable {

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

            if (arrow.isTicking()) {
                ta.tick();
            }
        }
    }
}
