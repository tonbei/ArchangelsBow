package com.tonbei.archangelsbow;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
