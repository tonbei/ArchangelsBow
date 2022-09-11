package com.github.tonbei.archangelsbow.listener;

import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Random;

public class HitTickArrowListener implements Listener {

    private final Random random = new Random();
    private final String CANCEL_ESCAPE_META_KEY = "ArchangelsBow:CancelEscape";
    private final String CANCEL_TARGET_META_KEY = "ArchangelsBow:CancelTarget";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByTickArrow(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) e.getEntity();

        if (hitEntity instanceof Player) return;
        if (hitEntity instanceof Villager) return;
        if (hitEntity instanceof Tameable && ((Tameable) hitEntity).isTamed()) return;

        if (TickArrowManager.isRegistered(e.getDamager().getUniqueId())) {
            removeNoDamageTicks(hitEntity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHitWitherOrEnderman(ProjectileCollideEvent e) {
        if (!(e.getCollidedWith() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) e.getCollidedWith();
        Projectile projectile = e.getEntity();

        if ((hitEntity instanceof Wither && hitEntity.getHealth() <= hitEntity.getMaxHealth() / 2.0)
                || hitEntity instanceof Enderman) {
            if (TickArrowManager.isRegistered(projectile.getUniqueId())) {
                if (hitEntity instanceof Enderman) {
                    hitEntity.setMetadata(CANCEL_ESCAPE_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), true));
                    hitEntity.setMetadata(CANCEL_TARGET_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), true));
                }
                removeNoDamageTicks(hitEntity);
                hitEntity.damage(random.nextInt(5) + 6.0, (Entity) projectile.getShooter());
                projectile.remove();
                TickArrowManager.remove(projectile.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEndermanEscape(EndermanEscapeEvent e) {
        if (e.getReason() == EndermanEscapeEvent.Reason.INDIRECT
                && e.getEntity().hasMetadata(CANCEL_ESCAPE_META_KEY)) {
            e.setCancelled(true);
            e.getEntity().removeMetadata(CANCEL_ESCAPE_META_KEY, ArchangelsBow.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTargetPlayer(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player)) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();

        if (e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY
                || e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY) {
            if (entity instanceof Enderman && entity.hasMetadata(CANCEL_TARGET_META_KEY)) {
                e.setCancelled(true);
                entity.removeMetadata(CANCEL_TARGET_META_KEY, ArchangelsBow.getInstance());
            }
            if (entity instanceof PigZombie) {
                Player player = (Player) e.getTarget();
                if (player.getMetadata(InventoryUpdateManager.AB_LEVEL_META_KEY).stream().findFirst().map(MetadataValue::asInt).orElse(0) >= 1) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private void removeNoDamageTicks(LivingEntity entity) {
        if(entity.getMaximumNoDamageTicks() != 0) {
            entity.setMaximumNoDamageTicks(0);
            Log.debug(entity.getName() + "'s DamageTicks has been changed to 0.");
        }
    }
}
