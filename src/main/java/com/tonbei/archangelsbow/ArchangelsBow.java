package com.tonbei.archangelsbow;

import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ArchangelsBow extends JavaPlugin implements Listener {

    private boolean isDebug = true;

    static Logger logger = null;

    private boolean isPaperMC = false;
    private Method isTicking = null;

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
