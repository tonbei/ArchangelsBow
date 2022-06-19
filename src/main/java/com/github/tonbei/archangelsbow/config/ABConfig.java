package com.github.tonbei.archangelsbow.config;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import org.bukkit.configuration.file.FileConfiguration;

public class ABConfig {

    private final ArchangelsBow plugin;

    private int startHomingTick = 1;
    private double searchRange = 8.0;
    private boolean enableCraft = true;
    private boolean debug = false;

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

    public int getStartHomingTick() {
        return startHomingTick;
    }

    public double getSearchRange() {
        return searchRange;
    }

    public boolean isEnableCraft() {
        return enableCraft;
    }

    public boolean isDebug() {
        return debug;
    }
}
