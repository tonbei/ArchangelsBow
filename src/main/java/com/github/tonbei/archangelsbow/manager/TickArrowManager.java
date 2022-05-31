package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.Log;
import com.github.tonbei.archangelsbow.entity.TickArrow;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TickArrowManager {

    private static final Map<UUID, TickArrow> tickArrows = new HashMap<>();

    public static void register(@NotNull TickArrow arrow) {
        Validate.notNull(arrow, "TickArrow must not be null.");

        if (tickArrows.putIfAbsent(arrow.getArrow().getUniqueId(), arrow) == null)
            Log.debug("TickArrow is registered.");
    }

    @Nullable
    public static TickArrow remove(UUID key) {
        return tickArrows.remove(key);
    }

    public static boolean isRegistered(UUID key) {
        return tickArrows.containsKey(key);
    }

    @Nullable
    public static TickArrow getTickArrow(UUID key) {
        return tickArrows.get(key);
    }

    @NotNull
    public static Iterator<Map.Entry<UUID, TickArrow>> getIterator() {
        return tickArrows.entrySet().iterator();
    }

    public static void unload() {
        tickArrows.clear();
    }
}
