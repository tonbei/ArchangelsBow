package com.github.tonbei.archangelsbow.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketUtil {

    public static void sendEquipmentPacket(@NotNull Player target, ItemStack equipment, @NotNull Collection<Player> receivers) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, target.getEntityId());

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, equipment));
        packet.getSlotStackPairLists().write(0, equipmentList);

        for (Player receiver : receivers) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
            } catch (InvocationTargetException ex) {
                Log.error(ex);
            }
        }
    }
}
