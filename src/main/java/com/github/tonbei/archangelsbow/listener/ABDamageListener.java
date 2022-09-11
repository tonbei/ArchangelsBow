package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;

public class ABDamageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if (e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (player.getMetadata(InventoryUpdateManager.AB_LEVEL_META_KEY).stream().findFirst().map(MetadataValue::asInt).orElse(0) >= 3) {
                e.setCancelled(true);
            }
        }
    }
}
