package com.tonbei.archangelsbow;

import org.bukkit.configuration.file.FileConfiguration;

public class ABConfig {

    private final ArchangelsBow plugin;

    private static int startHomingTick = 1;
    private static double searchRange = 8.0;
    private static boolean enableCraft = true;
    private static boolean debug = false;

    public ABConfig(ArchangelsBow plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        startHomingTick = Math.max(0, config.getInt("startHomingTick", 1));
        searchRange = Math.max(0.0, config.getDouble("searchRange", 8.0));
        enableCraft = config.getBoolean("enableCraft", true);
        debug = config.getBoolean("debug", false);
    }

    public static int getStartHomingTick() {
        return startHomingTick;
    }

    public static double getSearchRange() {
        return searchRange;
    }

    public static boolean isEnableCraft() {
        return enableCraft;
    }

    public static boolean isDebug() {
        return debug;
    }
}
