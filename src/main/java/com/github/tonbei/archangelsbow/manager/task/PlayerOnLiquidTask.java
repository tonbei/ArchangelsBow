package com.github.tonbei.archangelsbow.manager.task;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.ABUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerOnLiquidTask extends BukkitRunnable {

    private static final String AB_FLY_META_KEY = "ArchangelsBow:Fly";
    private static final AttributeModifier FLUID_SPEED_BOOST = new AttributeModifier(UUID.fromString("e29dde3a-3aab-a9f8-7f6a-5bbb7b4c4d25"), "Walk on water/lava speed boost", 0.075, AttributeModifier.Operation.ADD_NUMBER);
    private static final List<Material> liquidBlocks = Arrays.asList(Material.WATER, Material.LAVA, Material.BUBBLE_COLUMN, Material.KELP, Material.KELP_PLANT, Material.SEAGRASS, Material.TALL_SEAGRASS);

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean applySpeed = false;
            boolean setFlyFlag = false;

            if (player.getGameMode() != GameMode.SPECTATOR && Arrays.stream(player.getInventory().getContents()).anyMatch(ABUtil::isArchangelsBow)) {
                Location location = player.getLocation().toBlockLocation();
                Block block = location.getBlock();
                Block below = player.getLocation().add(0.0, -0.35, 0.0).toBlockLocation().getBlock();
                if (player.hasMetadata(AB_FLY_META_KEY) || !player.isFlying()) {
                    if (block.getType().isAir() && (liquidBlocks.stream().anyMatch(below.getType()::equals) || (below.getBlockData() instanceof Waterlogged && ((Waterlogged) below.getBlockData()).isWaterlogged()))) { //TODO Check　BlockData(Slab, Stairs)
                        if (!player.isSneaking()) {
                            if (player.getVelocity().getY() != 0) {
                                player.setVelocity(player.getVelocity().multiply(new Vector(1, 0, 1)));
                            }

                            player.setFallDistance(0.0F);
                            setFlyFlag = true;
                        }
                        applySpeed = true;
                    }
                }

                if (player.isInWater()) {
                    player.setRemainingAir(player.getMaximumAir());
                }
            }

            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (attribute != null) {
                if (applySpeed) {
                    if (attribute.getModifiers().stream().noneMatch(FLUID_SPEED_BOOST::equals)) {
                        attribute.addModifier(FLUID_SPEED_BOOST);
                    }
                } else if (attribute.getModifiers().stream().anyMatch(FLUID_SPEED_BOOST::equals)) {
                    attribute.removeModifier(FLUID_SPEED_BOOST);
                }
            }

            if (setFlyFlag) {
                if (!player.hasMetadata(AB_FLY_META_KEY) || !player.getAllowFlight()) {
                    player.setMetadata(AB_FLY_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), true));
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }
            } else {
                if (player.hasMetadata(AB_FLY_META_KEY)) {
                    player.removeMetadata(AB_FLY_META_KEY, ArchangelsBow.getInstance());
                    player.setFlying(false);
                    if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                        player.setAllowFlight(false);
                    }
                }
            }
        }
    }
}
