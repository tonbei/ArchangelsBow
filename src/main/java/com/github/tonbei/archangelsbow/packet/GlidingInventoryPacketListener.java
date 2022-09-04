package com.github.tonbei.archangelsbow.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.listener.PlayerGlideListener;
import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.util.PacketUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GlidingInventoryPacketListener extends PacketAdapter {

    public GlidingInventoryPacketListener(ArchangelsBow plugin) {
        super(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Client.SET_CREATIVE_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        PacketType packetType = packet.getType();
        Player player = e.getPlayer();

        if (!player.hasMetadata(PlayerGlideListener.AB_GLIDE_META_KEY)) return;

        if (packetType == PacketType.Play.Client.SET_CREATIVE_SLOT) { //TODO InventoryCreativeEvent
            if (packet.getIntegers().read(0) == 6) { //Clicked Slot Number : 6 = Chestplate Slot
                ItemStack setItem = packet.getItemModifier().read(0); //Clicked Item
                if (setItem != null) {
                    if (ABUtil.isPacketElytra(setItem)) {
                        e.setCancelled(true);
                    } else if (setItem.getType().isAir()) {
                        PacketUtil.sendEquipmentPacket(player, ABUtil.getPacketElytra(), Collections.singletonList(player)); //TODO SET_SLOT
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        PacketType packetType = packet.getType();
        Player player = e.getPlayer();

        if (packetType == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            Entity entity = packet.getEntityModifier(e).read(0);
            if (entity.hasMetadata(PlayerGlideListener.AB_GLIDE_META_KEY)) {
                List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = packet.getSlotStackPairLists().read(0);
                int index = 0;
                for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : equipmentList) {
                    if (pair.getFirst() == EnumWrappers.ItemSlot.CHEST) {
                        if (!ABUtil.isPacketElytra(pair.getSecond())) {
                            equipmentList.set(index, new Pair<>(EnumWrappers.ItemSlot.CHEST, ABUtil.getPacketElytra()));
                            packet.getSlotStackPairLists().write(0, equipmentList);
                        }
                        break;
                    }
                    index++;
                }
            }
        } else if (player.hasMetadata(PlayerGlideListener.AB_GLIDE_META_KEY) && packet.getIntegers().read(0) == 0) {
            if (packetType == PacketType.Play.Server.WINDOW_ITEMS) {
                List<ItemStack> slotData = packet.getItemListModifier().read(0);
                ItemStack chestplateSlot = slotData.get(6);
                if (!ABUtil.isPacketElytra(chestplateSlot)) {
                    slotData.set(6, ABUtil.getPacketElytra());
                    packet.getItemListModifier().write(0, slotData);
                }
            } else if (packetType == PacketType.Play.Server.SET_SLOT) {
                ItemStack setItem = packet.getItemModifier().read(0);
                if (packet.getIntegers().read(2) == 6 && !ABUtil.isPacketElytra(setItem)) {
                    packet.getItemModifier().write(0, ABUtil.getPacketElytra());
                }
            }
        }
    }
}
