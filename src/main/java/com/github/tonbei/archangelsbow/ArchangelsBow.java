package com.github.tonbei.archangelsbow;

import com.github.tonbei.archangelsbow.entity.HomingArrow;
import com.github.tonbei.archangelsbow.entity.TickArrow;
import com.github.tonbei.archangelsbow.events.ShootArrowEvent;
import com.github.tonbei.archangelsbow.events.TickArrowLoadEvent;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    private static ArchangelsBow INSTANCE;

    private ABConfig config;

    private boolean isPaperMC = false;
    private Method isTicking;

    public static ArchangelsBow getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = getPlugin(ArchangelsBow.class));
    }

    public ABConfig getABConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new TickArrowLoadEvent(), this);
        this.getServer().getPluginManager().registerEvents(new ShootArrowEvent(), this);
        INSTANCE = this;
        Log.setLogger(this.getLogger());
        ABUtil.init(this);
        config = new ABConfig(this);
        if (config.isEnableCraft()) ABUtil.addRecipe();

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
                Iterator<Map.Entry<UUID, TickArrow>> iterator = TickArrowManager.getIterator();

                while (iterator.hasNext()) {
                    TickArrow ta = iterator.next().getValue();

                    if (!ta.isActive()) {
                        iterator.remove();
                        Log.debug("TickArrow is removed.");
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

                        if (lo.isWorldLoaded() && wo.isChunkLoaded((int) Math.round(lo.getX()), (int) Math.round(lo.getZ())))
                            if (wo.getChunkAt(lo).isEntitiesLoaded())
                                ta.tick();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);

        for (World world : Bukkit.getWorlds())
            for (Chunk chunk : world.getLoadedChunks())
                if (chunk.isEntitiesLoaded())
                    for (Entity entity : chunk.getEntities())
                        if (ABUtil.isHomingArrow(entity))
                            TickArrowManager.register(new HomingArrow((Arrow) entity, config.getStartHomingTick(), config.getSearchRange()));
    }

    @Override
    public void onDisable() {
        ABUtil.removeRecipe(false);
        TickArrowManager.unload();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) return false;

        if (args[0].equalsIgnoreCase("get")) {
            if (!(sender instanceof Player)) {
                Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                                + ChatColor.RED + "This command can only be used in-game.", sender);
                return true;
            }

            int level = 1;
            if (args.length >= 2 && args[1].matches("^\\d+$")) {
                level = Integer.parseInt(args[1]);
            }
            ((Player) sender).getInventory().addItem(ABUtil.getArchangelsBow(level));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.reloadConfig();

            if (config.isEnableCraft()) {
                ABUtil.addRecipe();
            } else {
                ABUtil.removeRecipe(false);
            }

            Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                            + ChatColor.GREEN + "Archangel's Bow configuration was reloaded.", sender);
            return true;
        }

        return false;
    }

    public void onHitArrow(EntityDamageByEntityEvent e) {
        //TODO
    }

    public void onSetBowInAnvil(PrepareAnvilEvent e) {
        //TODO
    }

    public void onSetBowInEnchantTable(PrepareItemEnchantEvent e) {
        //TODO
    }
}
