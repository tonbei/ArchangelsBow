package com.tonbei.archangelsbow;

import org.bukkit.Bukkit;
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
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    public static final boolean isDebug = true;
    private static Logger logger;

    private boolean isPaperMC = false;
    private Method isTicking;

    Map<UUID, TickArrow> TickArrows = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this, this);
        if(logger == null) logger = this.getLogger();

        try {
            isTicking = Entity.class.getMethod("isTicking");
            isPaperMC = true;
        } catch (NoSuchMethodException e) {
            isPaperMC = false;
        }

        if(isDebug) logger.log(Level.INFO, "Server Type : " + (isPaperMC ? "PaperMC" : "Not PaperMC"));

        new BukkitRunnable() {
            @Override
            public void run() {
                for(TickArrow ta : TickArrows.values()) {
                    boolean defaultCheck = true;
                    Arrow arrow = ta.getArrow();

                    if(isPaperMC) {
                        try {
                            if((boolean) isTicking.invoke(arrow)) {
                                ta.tick();
                            }
                            defaultCheck = false;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    if(defaultCheck) {
                        Location lo = arrow.getLocation();
                        World wo = arrow.getWorld();

                        if(lo.isWorldLoaded() && wo.isChunkLoaded((int)Math.round(lo.getX()), (int)Math.round(lo.getZ())))
                            if(wo.getChunkAt(lo).isEntitiesLoaded())
                                ta.tick();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootBow(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            if(e.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getProjectile();
                TickArrows.put(arrow.getUniqueId(), new HomingArrow(arrow));
            }
        }
    }

    @NotNull
    public static Logger getPluginLogger() {
        return logger != null ? logger : Bukkit.getLogger();
    }
}
