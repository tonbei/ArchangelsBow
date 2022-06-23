package com.github.tonbei.archangelsbow.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExpUtil {

    public static int getExp(@NotNull Player player) {
        int exp = getExpFromLevel(player.getLevel());
        exp += Math.round(getExpToNextLevel(player.getLevel()) * player.getExp());
        if (exp < 0) exp = Integer.MAX_VALUE;
        return exp;
    }

    public static void setExp(@NotNull Player player, int exp) {
        int level = getLevelFromExp(exp);
        exp -= getExpFromLevel(level);
        float progress = 1.0F / getExpToNextLevel(level) * exp;
        player.setLevel(level);
        player.setExp(progress);
    }

    private static int getExpToNextLevel(final int level) {
        if (level <= 15)
            return 2 * level + 7;
        if (level <= 30)
            return 5 * level - 38;
        return 9 * level - 158;
    }

    private static int getExpFromLevel(final int level) {
        int exp = 0;
        for (int i = 0; i < level; i++)
            exp += getExpToNextLevel(i);
        return exp;
    }

    private static int getLevelFromExp(int exp) {
        int level = 0;
        while (exp > 0) {
            exp -= getExpToNextLevel(level);
            if (exp >= 0)
                level++;
        }
        return level;
    }
}
