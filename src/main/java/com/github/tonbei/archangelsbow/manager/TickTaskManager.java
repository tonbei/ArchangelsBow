package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.task.InventoryUpdateTask;
import com.github.tonbei.archangelsbow.manager.task.PlayerOnLiquidTask;
import com.github.tonbei.archangelsbow.manager.task.TickArrowTask;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TickTaskManager {

    private static final List<Runnable> tasks = new ArrayList<>();
    private static final List<Runnable> onetimeTasks = new ArrayList<>();
    private static PlayerOnLiquidTask playerOnLiquidTask; //Works only with BukkitRunnable.

    public static void init(ArchangelsBow plugin) {
        clear();

        tasks.add(new TickArrowTask());
        tasks.add(new InventoryUpdateTask());

        playerOnLiquidTask = new PlayerOnLiquidTask();
        playerOnLiquidTask.runTaskTimer(plugin, 0L, 1L);
    }

    public static void register(@NotNull Runnable task) {
        Validate.notNull(task, "TickTask must not be null.");

        onetimeTasks.add(task);
    }

    @NotNull
    public static List<Runnable> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    @NotNull
    public static List<Runnable> getOnetimeTasks() {
        return Collections.unmodifiableList(onetimeTasks);
    }

    public static void clearOnetimeTasks() {
        onetimeTasks.clear();
    }

    public static void clear() {
        tasks.clear();
        onetimeTasks.clear();

        if (playerOnLiquidTask != null) {
            playerOnLiquidTask.cancel();
            playerOnLiquidTask = null;
        }
    }
}
