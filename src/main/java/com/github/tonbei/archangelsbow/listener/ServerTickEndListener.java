package com.github.tonbei.archangelsbow.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.tonbei.archangelsbow.manager.TickTaskManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ServerTickEndListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTickEnd(ServerTickEndEvent e) {
        TickTaskManager.getTasks().forEach(Runnable::run);
        TickTaskManager.getOnetimeTasks().forEach(Runnable::run);
        TickTaskManager.clearOnetimeTasks();
    }
}
