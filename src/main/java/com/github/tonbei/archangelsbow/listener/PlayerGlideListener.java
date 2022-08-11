package com.github.tonbei.archangelsbow.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerGlideListener implements Listener {

    public static final String AB_GLIDE_META_KEY = "ArchangelsBow:Glide";

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        boolean cancelGlideFlag = false;

        if (player.getMetadata(AB_GLIDE_META_KEY).stream().anyMatch(MetadataValue::asBoolean)) {
            if (player.isOnGround() || player.isFlying() || player.isInWaterOrBubbleColumn() || player.isInLava()) {
                cancelGlideFlag = true;
            }
        } else if (player.getFallDistance() > 2.0f) {
            if (!player.hasMetadata(AB_GLIDE_META_KEY)) {
                sendEquipmentPacket(player, new ItemStack(Material.ELYTRA), player.getWorld().getPlayers());
                player.setMetadata(AB_GLIDE_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), false));
            }
        } else {
            if (player.hasMetadata(AB_GLIDE_META_KEY)) {
                cancelGlideFlag = true;
            }
        }

        if (cancelGlideFlag) {
            sendEquipmentPacket(player, player.getInventory().getArmorContents()[2], player.getWorld().getPlayers());
            player.removeMetadata(AB_GLIDE_META_KEY, ArchangelsBow.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerGlide(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if (player.hasMetadata(AB_GLIDE_META_KEY)) {
            if (player.getMetadata(AB_GLIDE_META_KEY).stream().noneMatch(MetadataValue::asBoolean)) {
                player.setMetadata(AB_GLIDE_META_KEY, new FixedMetadataValue(ArchangelsBow.getInstance(), true));
                player.setGliding(true);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBoost(PlayerElytraBoostEvent e) {
        Player player = e.getPlayer();

        if (player.hasMetadata(AB_GLIDE_META_KEY)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendEquipmentPacket(player, new ItemStack(Material.ELYTRA), player.getWorld().getPlayers()); //TODO Check receivers
                }
            }.runTask(ArchangelsBow.getInstance());
        }
    }

    private void sendEquipmentPacket(Player target, ItemStack equipment, Collection<Player> receivers) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, target.getEntityId());

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, equipment));
        packet.getSlotStackPairLists().write(0, equipmentList);

        for (Player receiver : receivers) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    }
}
