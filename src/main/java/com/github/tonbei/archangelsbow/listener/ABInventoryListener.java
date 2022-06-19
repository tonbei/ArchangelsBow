package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.util.ABUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ABInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSetBowInAnvil(PrepareAnvilEvent e) {
        AnvilInventory ai = e.getInventory();
        ItemStack first = ai.getItem(0);
        ItemStack second = ai.getItem(1);

        if (ABUtil.isArchangelsBow(first) || ABUtil.isArchangelsBow(second)) {
            ai.setItem(2, null);
            e.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSetBowInEnchantTable(PrepareItemEnchantEvent e) {
        if (ABUtil.isArchangelsBow(e.getItem()))
            e.setCancelled(true);
    }
}
