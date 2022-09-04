package com.github.tonbei.archangelsbow.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.destroystokyo.paper.MaterialTags;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.listener.PlayerGlideListener;
import com.github.tonbei.archangelsbow.util.PacketUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GlidingInventoryPacketListener extends PacketAdapter {

    public GlidingInventoryPacketListener(ArchangelsBow plugin) {
        super(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.SET_CREATIVE_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        PacketType packetType = packet.getType();
        Player player = e.getPlayer();

        if (!player.hasMetadata(PlayerGlideListener.AB_GLIDE_META_KEY)) return;

        if (packetType == PacketType.Play.Client.WINDOW_CLICK) { //TODO InventoryClickEvent
            if (packet.getIntegers().read(0) == 0) { //Window ID : 0 = player inventory
                if (packet.getIntegers().read(2) == 6) { //Clicked Slot Number : 6 = Chestplate Slot
                    //Map of slots to be changed : <Slot Number, ItemStack after change>
                    Map<Integer, ItemStack> changeSlots = packet.getMaps(Converters.passthrough(Integer.TYPE), BukkitConverters.getItemStackConverter()).read(0);
                    ItemStack chestplateItem = changeSlots.get(6);
                    if (chestplateItem != null && (chestplateItem.getType().isAir() || MaterialTags.CHEST_EQUIPPABLE.isTagged(chestplateItem))) {
                        //player.sendMessage(packet.getModifier().getFields().toString());

                        //Inventory operation mode
                        InventoryClickType clickType = packet.getEnumModifier(InventoryClickType.class, 4).read(0);

                        if (clickType == InventoryClickType.SWAP || clickType == InventoryClickType.THROW || clickType == InventoryClickType.QUICK_MOVE) {
                            PacketUtil.sendEquipmentPacket(player, new ItemStack(Material.ELYTRA), Collections.singletonList(player)); //TODO SET_SLOT
                        }
                    }
                }
            }
        } else if (packetType == PacketType.Play.Client.SET_CREATIVE_SLOT) { //TODO InventoryCreativeEvent
            if (packet.getIntegers().read(0) == 6) { //Clicked Slot Number : 6 = Chestplate Slot
                ItemStack setItem = packet.getItemModifier().read(0); //Clicked Item
                if (setItem != null) {
                    if (setItem.getType() == Material.ELYTRA) {
                        e.setCancelled(true);
                    } else if (setItem.getType().isAir()) {
                        PacketUtil.sendEquipmentPacket(player, new ItemStack(Material.ELYTRA), Collections.singletonList(player)); //TODO SET_SLOT
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
                        if (pair.getSecond().getType() != Material.ELYTRA) {
                            equipmentList.set(index, new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.ELYTRA)));
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
                if (chestplateSlot != null && chestplateSlot.getType() != Material.ELYTRA) {
                    slotData.set(6, new ItemStack(Material.ELYTRA));
                    packet.getItemListModifier().write(0, slotData);
                }
            } else if (packetType == PacketType.Play.Server.SET_SLOT) {
                ItemStack setItem = packet.getItemModifier().read(0);
                if (packet.getIntegers().read(2) == 6 && (setItem == null || setItem.getType() != Material.ELYTRA)) {
                    packet.getItemModifier().write(0, new ItemStack(Material.ELYTRA));
                }
            }
        }
    }

    public enum InventoryClickType {
        PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL
    }
}
