package com.github.tonbei.archangelsbow;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.tonbei.archangelsbow.config.ABConfig;
import com.github.tonbei.archangelsbow.arrow.HomingArrow;
import com.github.tonbei.archangelsbow.listener.ABCraftListener;
import com.github.tonbei.archangelsbow.listener.ABDamageListener;
import com.github.tonbei.archangelsbow.listener.ABInventoryListener;
import com.github.tonbei.archangelsbow.listener.HitTickArrowListener;
import com.github.tonbei.archangelsbow.listener.InventoryUpdateListener;
import com.github.tonbei.archangelsbow.listener.PlayerGlideListener;
import com.github.tonbei.archangelsbow.listener.ServerTickEndListener;
import com.github.tonbei.archangelsbow.listener.ShootArrowListener;
import com.github.tonbei.archangelsbow.listener.TickArrowLoadListener;
import com.github.tonbei.archangelsbow.manager.ABRecipeManager;
import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import com.github.tonbei.archangelsbow.manager.TickArrowManager;
import com.github.tonbei.archangelsbow.manager.TickTaskManager;
import com.github.tonbei.archangelsbow.packet.GlidingInventoryPacketListener;
import com.github.tonbei.archangelsbow.packet.InventoryUpdatePacketListener;
import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    private static ArchangelsBow INSTANCE;

    private ABConfig config;
    private ABRecipeManager recipeManager;

    public static ArchangelsBow getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = getPlugin(ArchangelsBow.class));
    }

    public static ABConfig getABConfig() {
        return getInstance().config;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        Log.setLogger(this.getLogger());
        config = new ABConfig(this);
        recipeManager = new ABRecipeManager(this);
        if (config.isEnableCraft()) recipeManager.addRecipe();

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new TickArrowLoadListener(), this);
        pluginManager.registerEvents(new ShootArrowListener(), this);
        pluginManager.registerEvents(new ABInventoryListener(), this);
        pluginManager.registerEvents(new HitTickArrowListener(), this);
        pluginManager.registerEvents(new ABCraftListener(recipeManager), this);
        pluginManager.registerEvents(new PlayerGlideListener(), this);
        pluginManager.registerEvents(new InventoryUpdateListener(), this);
        pluginManager.registerEvents(new ServerTickEndListener(), this);
        pluginManager.registerEvents(new ABDamageListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new GlidingInventoryPacketListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryUpdatePacketListener(this));

        TickTaskManager.init(this);

        for (World world : Bukkit.getWorlds())
            for (Chunk chunk : world.getLoadedChunks())
                if (chunk.isEntitiesLoaded())
                    for (Entity entity : chunk.getEntities())
                        if (ABUtil.isHomingArrow(entity))
                            TickArrowManager.register(new HomingArrow((Arrow) entity, config.getStartHomingTick(), config.getSearchRange()));
    }

    @Override
    public void onDisable() {
        recipeManager.removeRecipe(false);
        TickArrowManager.clear();
        TickTaskManager.clear();
        InventoryUpdateManager.clear();
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
                recipeManager.addRecipe();
            } else {
                recipeManager.removeRecipe(false);
            }

            Log.infoSenders("[" + ChatColor.GREEN + "Archangel's Bow" + ChatColor.RESET + "] "
                            + ChatColor.GREEN + "Archangel's Bow configuration was reloaded.", sender);
            return true;
        }

        return false;
    }
}
