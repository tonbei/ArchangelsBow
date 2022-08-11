package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.util.ABUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class PlayerFlyTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Arrays.stream(player.getInventory().getContents()).anyMatch(ABUtil::isArchangelsBow)) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                }
            } else {
                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    if (player.getAllowFlight()) {
                        player.setAllowFlight(false);
                    }
                }
            }
        }
    }
}
