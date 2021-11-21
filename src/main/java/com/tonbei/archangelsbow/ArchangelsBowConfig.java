package com.tonbei.archangelsbow;

import org.bukkit.configuration.file.FileConfiguration;

public class ArchangelsBowConfig {

    private ArchangelsBow plugin;

    private int startHomingTick = 1;
    private double searchRange = 8.0;
    private boolean enableCraft = true;

    public ArchangelsBowConfig(ArchangelsBow plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        startHomingTick = Math.min(ArchangelsBowUtil.MAX_LEVEL, Math.max(0, config.getInt("startHomingTick", 1)));
        searchRange = config.getDouble("searchRange", 8.0);
        enableCraft = config.getBoolean("searchRange", true);
    }

    public int getStartHomingTick() {
        return startHomingTick;
    }

    public double getSearchRange() {
        return searchRange;
    }

    public boolean isEnableCraft() {
        return enableCraft;
    }
}
