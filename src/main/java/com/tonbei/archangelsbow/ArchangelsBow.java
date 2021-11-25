package com.tonbei.archangelsbow;

import com.tonbei.archangelsbow.entity.HomingArrow;
import com.tonbei.archangelsbow.entity.TickArrow;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    public static final boolean isDebug = true;
    public static final int BOW_MAX_LEVEL = 5;

    static boolean isRecipeRegistered = false;
    private ArchangelsBowConfig config;

    private boolean isPaperMC = false;
    private Method isTicking;

    private static final Map<UUID, TickArrow> TickArrows = new HashMap<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        Log.setLogger(this.getLogger());
        config = new ArchangelsBowConfig(this);
        if (config.isEnableCraft() && !isRecipeRegistered) ArchangelsBowUtil.addRecipe();

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

                        if (lo.isWorldLoaded() && wo.isChunkLoaded((int) Math.round(lo.getX()), (int) Math.round(lo.getZ())))
                            if (wo.getChunkAt(lo).isEntitiesLoaded())
                                ta.tick();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        if (isRecipeRegistered)
            ArchangelsBowUtil.removeRecipe();
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
            ((Player) sender).getInventory().addItem(ArchangelsBowUtil.getArchangelsBow(level));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.reloadConfig();

            if (config.isEnableCraft() && !isRecipeRegistered) {
                ArchangelsBowUtil.addRecipe();
            } else if (!config.isEnableCraft() && isRecipeRegistered) {
                ArchangelsBowUtil.removeRecipe();
            }

            Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                            + ChatColor.GREEN + "Archangel's Bow configuration was reloaded.", sender);
            return true;
        }

        return false;
    }

    public static void register(@NotNull TickArrow arrow) {
        Objects.requireNonNull(arrow);

        TickArrows.put(arrow.getArrow().getUniqueId(), arrow);
        Log.debug("TickArrow registered.");
    }

    @Nullable
    public static TickArrow remove(UUID key) {
        return TickArrows.remove(key);
    }

    public static boolean isRegistered(UUID key) {
        return TickArrows.containsKey(key);
    }

    @Nullable
    public static TickArrow getTickArrow(UUID key) {
        return TickArrows.get(key);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getProjectile();
                register(new HomingArrow(arrow, config.getStartHomingTick(), config.getSearchRange()));

                //TickArrows.entrySet().stream().map(map -> map.getKey().toString() + " : " + map.getValue().toString()).forEach(Log::debug);
            }
        }
    }

    public void onHitArrow(EntityDamageByEntityEvent e) {
        //TODO
    }
}
