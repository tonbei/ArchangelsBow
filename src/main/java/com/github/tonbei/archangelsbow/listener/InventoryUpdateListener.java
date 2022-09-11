package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class InventoryUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        InventoryUpdateManager.add((Player) e.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDrop(PlayerDropItemEvent e) {
        InventoryUpdateManager.add(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        InventoryUpdateManager.add(e.getPlayer());
    }
}
