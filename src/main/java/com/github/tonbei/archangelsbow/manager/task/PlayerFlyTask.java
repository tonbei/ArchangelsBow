package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.manager.PlayerFlyManager;
import com.github.tonbei.archangelsbow.util.ABUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class PlayerFlyTask implements Runnable {

    @Override
    public void run() {
        Set<Player> updatePlayers = PlayerFlyManager.getUpdatePlayers();

        if (!updatePlayers.isEmpty()) {
            for (Player player : updatePlayers) {
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

            PlayerFlyManager.clear();
        }
    }
}
