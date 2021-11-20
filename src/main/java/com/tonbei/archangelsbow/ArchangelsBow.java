package com.tonbei.archangelsbow;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    public static final boolean isDebug = true;

    private boolean isPaperMC = false;
    private Method isTicking;

    private static final Map<UUID, TickArrow> TickArrows = new HashMap<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        Log.setLogger(this.getLogger());

        try {
            isTicking = Entity.class.getMethod("isTicking");
            isPaperMC = true;
        } catch (NoSuchMethodException e) {
            isPaperMC = false;
        }

        Log.info("Server Type : " + (isPaperMC ? "PaperMC" : "Not PaperMC"));

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, TickArrow>> iterator = TickArrows.entrySet().iterator();

                while (iterator.hasNext()) {
                    TickArrow ta = iterator.next().getValue();

                    if (!ta.isActive()) {
                        iterator.remove();
                        Log.debug("TickArrow removed.");
                        continue;
                    }

                    Arrow arrow = ta.getArrow();

                    if (isPaperMC) {
                        try {
                            if ((boolean) isTicking.invoke(arrow)) {
                                ta.tick();
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Log.error(e);
                            isPaperMC = false;
                        }
                    }
                    if (!isPaperMC) {
                        Location lo = arrow.getLocation();
                        World wo = arrow.getWorld();

                        if (lo.isWorldLoaded() && wo.isChunkLoaded((int)Math.round(lo.getX()), (int)Math.round(lo.getZ())))
                            if (wo.getChunkAt(lo).isEntitiesLoaded())
                                ta.tick();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {

    }

    public static void register(@NotNull TickArrow arrow) {
        Objects.requireNonNull(arrow);

        TickArrows.put(arrow.getArrow().getUniqueId(), arrow);
        Log.debug("TickArrow registered.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getProjectile();
                register(new HomingArrow(arrow));

                //TickArrows.entrySet().stream().map(map -> map.getKey().toString() + " : " + map.getValue().toString()).forEach(Log::debug);
            }
        }
    }
}
