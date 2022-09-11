package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;
import com.github.tonbei.archangelsbow.util.ABUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.Set;

public class InventoryUpdateTask implements Runnable {

    @Override
    public void run() {
        Set<Player> updatePlayers = InventoryUpdateManager.getUpdatePlayers();

        if (!updatePlayers.isEmpty()) {
            for (Player player : updatePlayers) {
                int abLevel = Arrays.stream(player.getInventory().getContents()).mapToInt(ABUtil::getABLevel).max().orElse(0);
                player.setMetadata(InventoryUpdateManager.AB_LEVEL_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), abLevel));

                if (abLevel >= 5) {
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

            InventoryUpdateManager.clear();
        }
    }
}
