package com.github.tonbei.archangelsbow.events;

import com.github.tonbei.archangelsbow.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.entity.HomingArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class TickArrowLoadEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities())
            if (ABUtil.isHomingArrow(entity))
                TickArrowManager.register(new HomingArrow((Arrow) entity,
                                            ArchangelsBow.getInstance().getABConfig().getStartHomingTick(),
                                            ArchangelsBow.getInstance().getABConfig().getSearchRange()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesUnload(EntitiesUnloadEvent e) {
        for (Entity entity : e.getEntities())
            if (entity instanceof Arrow && TickArrowManager.isRegistered(entity.getUniqueId()))
                TickArrowManager.remove(entity.getUniqueId());
    }
}