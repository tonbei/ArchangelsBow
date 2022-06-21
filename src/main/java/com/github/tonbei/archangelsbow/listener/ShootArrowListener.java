package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import com.github.tonbei.archangelsbow.arrow.HomingArrow;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import com.github.tonbei.archangelsbow.util.NMSUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wither;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.Random;

public class ShootArrowListener implements Listener {

    private final Random random = new Random();

    private final Class<?> craftLivingEntityClass;
    private final Method getHandleMethod;
    private final Method getExperienceRewardMethod;

    public ShootArrowListener() {
        String version = NMSUtil.getVersion();
        if (version == null) throw new RuntimeException("Failed to find version for running Minecraft server");

        craftLivingEntityClass = NMSUtil.getClass("org.bukkit.craftbukkit." + version + ".entity.CraftLivingEntity");
        if (craftLivingEntityClass == null) throw new RuntimeException("Failed to find CraftLivingEntity class");

        getHandleMethod = NMSUtil.getMethod(craftLivingEntityClass, "getHandle");
        if (getHandleMethod == null) throw new RuntimeException("Failed to find CraftLivingEntity.getHandle() method");

        Class<?> nmsEntityLivingClass = NMSUtil.getClass("net.minecraft.world.entity.EntityLiving");
        if (nmsEntityLivingClass == null) throw new RuntimeException("Failed to find EntityLiving class");

        //Class<?> nmsPlayerClass = NMSUtil.getClass("net.minecraft.world.entity.player.EntityHuman");
        //if (nmsPlayerClass == null) throw new RuntimeException("Failed to find Player class");

        //getExperienceRewardMethod = NMSUtil.getMethod(nmsEntityLivingClass, "getExpValue", nmsPlayerClass);
        getExperienceRewardMethod = NMSUtil.getMethod(nmsEntityLivingClass, "getExpValue");
        if (getExperienceRewardMethod == null) throw new RuntimeException("Failed to find EntityLiving.getExpValue() method");
    }

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
        if (!(e.getHitEntity() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) e.getHitEntity();

        if (hitEntity instanceof Player) return;
        if (hitEntity instanceof Tameable && ((Tameable) hitEntity).isTamed()) return;

        if (TickArrowManager.isRegistered(e.getEntity().getUniqueId())) {
            if(hitEntity.getMaximumNoDamageTicks() != 0) {
                hitEntity.setMaximumNoDamageTicks(0);
                Log.debug(hitEntity.getName() + "'s DamageTicks has been changed to 0.");
            }

            if (hitEntity instanceof Wither && hitEntity.getHealth() <= hitEntity.getMaxHealth() / 2.0) {

            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        if (entity instanceof Player) return;
        if (entity instanceof EnderDragon) return;

        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent cause = (EntityDamageByEntityEvent) entity.getLastDamageCause();

            if (TickArrowManager.isRegistered(cause.getDamager().getUniqueId())) {
                try {
                    Object craftLivingEntity = craftLivingEntityClass.cast(entity);
                    Object nmsEntityLiving = getHandleMethod.invoke(craftLivingEntity);
                    int dropExp = (int) getExperienceRewardMethod.invoke(nmsEntityLiving, (Object) null);
                    e.setDroppedExp(dropExp);
                    Log.debug("Dropped Exp has been changed to " + dropExp);
                } catch (Exception ex) {
                    Log.error(ex);
                }
            }
        }
    }

    private void shootTickArrow(Player player, ItemStack bow, int count, float speed, float spread) {
        Vector vector = player.getVelocity();
        Location location = player.getLocation();
        float xRot = location.getPitch();
        float yRot = location.getYaw();
        float x = (float) (-Math.sin(yRot * ((float)Math.PI / 180F)) * Math.cos(xRot * ((float)Math.PI / 180F)));
        float y = (float) -Math.sin(xRot * ((float)Math.PI / 180F));
        float z = (float) (Math.cos(yRot * ((float)Math.PI / 180F)) * Math.cos(xRot * ((float)Math.PI / 180F)));
        for (int i = 0; i < Math.max(0, count); i++) {
            //TODO
            //player.sendMessage("" + player.getTotalExperience());

            Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation().subtract(0, 0.1, 0), new Vector(x, y, z), speed, spread);
            arrow.setVelocity(arrow.getVelocity().clone().add(new Vector(vector.getX(), player.isOnGround() ? 0.0D : vector.getY(), vector.getZ())));

            //TODO 発射時の弓のレベルを格納
            arrow.getPersistentDataContainer().set(ABUtil.getHoming(), PersistentDataType.INTEGER,
                    bow.getItemMeta().getPersistentDataContainer().getOrDefault(ABUtil.getBlessing(), PersistentDataType.INTEGER, 0));
            TickArrowManager.register(new HomingArrow(arrow, ArchangelsBow.getABConfig().getStartHomingTick(), ArchangelsBow.getABConfig().getSearchRange(), player));

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        }
    }
}
