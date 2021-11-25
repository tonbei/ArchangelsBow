package com.tonbei.archangelsbow;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static Logger logger = Bukkit.getLogger();

    static void setLogger(@NotNull Logger l) {
        logger = Objects.requireNonNull(l);
    }

    public static void debug(String s) {
        if (ArchangelsBow.isDebug)
            logger.log(Level.INFO, s);
    }

    public static void info(String s) {
        logger.log(Level.INFO, s);
    }

    public static void infoSenders(String s, CommandSender... senders) {
        if (senders == null) return;
        for (CommandSender sender : senders) sender.sendMessage(s);
    }

    public static void infoSenders(String s, Collection<? extends CommandSender> senders) {
        if (senders == null) return;
        senders.forEach(sender -> sender.sendMessage(s));
    }

    public static void infoAll(String s) {
        Bukkit.broadcastMessage(s);
    }

    public static void warning(String s) {
        logger.log(Level.WARNING, s);
    }

    public static void warning(Throwable ex) {
        exception(Level.WARNING, ex);
    }

    public static void error(String s) {
        logger.log(Level.SEVERE, s);
    }

    public static void error(Throwable ex) {
        exception(Level.SEVERE, ex);
    }

    private static void exception(Level level, Throwable ex) {
        if (ex == null) return;

        logger.log(level, ex.toString());
        for (StackTraceElement element : ex.getStackTrace())
            logger.log(level, "\tat " + element.toString());
    }
}
