package com.github.tonbei.archangelsbow.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.InventoryUpdateManager;

public class InventoryUpdatePacketListener extends PacketAdapter {

    public InventoryUpdatePacketListener(ArchangelsBow plugin) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        InventoryUpdateManager.add(e.getPlayer());
    }
}
