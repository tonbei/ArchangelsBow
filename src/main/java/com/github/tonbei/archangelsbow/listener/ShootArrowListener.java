package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import com.github.tonbei.archangelsbow.arrow.HomingArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Random;

public class ShootArrowListener implements Listener {

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBowClick(PlayerInteractEvent e) {
        if (!ABUtil.isArchangelsBow(e.getItem())) return;

        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            e.setUseItemInHand(Event.Result.DENY);
            shootTickArrow(e.getPlayer(), e.getItem(), 1, 3.0F, 1.0F);

        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            shootTickArrow(e.getPlayer(), e.getItem(), 7, 3.0F, 4.0F);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHitTickArrow(ProjectileHitEvent e) {
        Entity hitEntity = e.getHitEntity();

        if (!(hitEntity instanceof LivingEntity)) return;
        if (hitEntity instanceof Player) return;
        if (hitEntity instanceof Tameable && ((Tameable) hitEntity).isTamed()) return;

        if (TickArrowManager.isRegistered(e.getEntity().getUniqueId())) {
            if(((LivingEntity) hitEntity).getMaximumNoDamageTicks() != 0) {
                ((LivingEntity) hitEntity).setMaximumNoDamageTicks(0);
                Log.debug(hitEntity.getName() + "'s DamageTicks has been changed to 0.");
            }
        }
    }

    private void shootTickArrow(LivingEntity entity, ItemStack bow, int count, float speed, float spread) {
        Vector vector = entity.getVelocity();
        Location location = entity.getLocation();
        float xRot = location.getPitch();
        float yRot = location.getYaw();
        float x = (float) (-Math.sin(yRot * ((float)Math.PI / 180F)) * Math.cos(xRot * ((float)Math.PI / 180F)));
        float y = (float) -Math.sin(xRot * ((float)Math.PI / 180F));
        float z = (float) (Math.cos(yRot * ((float)Math.PI / 180F)) * Math.cos(xRot * ((float)Math.PI / 180F)));
        for (int i = 0; i < Math.max(0, count); i++) {
            Arrow arrow = entity.getWorld().spawnArrow(entity.getEyeLocation().subtract(0, 0.1, 0), new Vector(x, y, z), speed, spread);
            arrow.setVelocity(arrow.getVelocity().clone().add(new Vector(vector.getX(), entity.isOnGround() ? 0.0D : vector.getY(), vector.getZ())));

            //TODO 発射時の弓のレベルを格納
            arrow.getPersistentDataContainer().set(ABUtil.getHoming(), PersistentDataType.INTEGER,
                    bow.getItemMeta().getPersistentDataContainer().getOrDefault(ABUtil.getBlessing(), PersistentDataType.INTEGER, 0));
            TickArrowManager.register(new HomingArrow(arrow, ArchangelsBow.getABConfig().getStartHomingTick(), ArchangelsBow.getABConfig().getSearchRange()));

            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        }
    }
}
