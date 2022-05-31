package com.github.tonbei.archangelsbow.events;

import com.github.tonbei.archangelsbow.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.entity.HomingArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class ShootArrowEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getProjectile() instanceof Arrow && ABUtil.isArchangelsBow(e.getBow())) {
                Arrow arrow = (Arrow) e.getProjectile();
                //TODO 発射時の弓のレベルを格納
                arrow.getPersistentDataContainer().set(ABUtil.getHoming(), PersistentDataType.INTEGER,
                        e.getBow().getItemMeta().getPersistentDataContainer().getOrDefault(ABUtil.getBlessing(), PersistentDataType.INTEGER, 0));
                TickArrowManager.register(new HomingArrow(arrow,
                                            ArchangelsBow.getInstance().getABConfig().getStartHomingTick(),
                                            ArchangelsBow.getInstance().getABConfig().getSearchRange()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBowClick(PlayerInteractEvent e) {
        if (!ABUtil.isArchangelsBow(e.getItem())) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

        }
    }
}
