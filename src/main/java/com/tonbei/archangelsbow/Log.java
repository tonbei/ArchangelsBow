package com.tonbei.archangelsbow;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static Logger logger = Bukkit.getLogger();

    static void setLogger(@NotNull Logger l) {
        logger = l;
    }

    public static void info(String s) {
        logger.log(Level.INFO, s);
    }

    public static void info(String s, Object... o) {
        logger.log(Level.INFO, s, o);
    }

    public static void warning(String s) {
        logger.log(Level.WARNING, s);
    }

    public static void warning(String s, Object... o) {
        logger.log(Level.WARNING, s, o);
    }

    public static void error(String s) {
        logger.log(Level.SEVERE, s);
    }

    public static void error(String s, Object... o) {
        logger.log(Level.SEVERE, s, o);
    }
}
