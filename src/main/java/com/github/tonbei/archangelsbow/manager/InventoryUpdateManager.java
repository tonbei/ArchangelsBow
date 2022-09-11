package com.github.tonbei.archangelsbow.manager;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InventoryUpdateManager {

    public static final String AB_LEVEL_META_KEY = "ArchangelsBow:Level";
    private static final Set<Player> updatePlayers = new HashSet<>();

    public static void add(@NotNull Player player) {
        Validate.notNull(player, "UpdatePlayer must not be null.");

        updatePlayers.add(player);
    }

    @NotNull
    public static Set<Player> getUpdatePlayers() {
        return Collections.unmodifiableSet(updatePlayers);
    }

    public static void clear() {
        updatePlayers.clear();
    }
}
