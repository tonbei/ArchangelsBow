package com.github.tonbei.archangelsbow.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ABUtil {

    public static final int BOW_MAX_LEVEL = 5;

    private static final String AB_NAMESPACE = "ArchangelsBow".toLowerCase(Locale.ROOT);

    private static final NamespacedKey BLESSING = new NamespacedKey(AB_NAMESPACE, "blessing");
    private static final NamespacedKey HOMING = new NamespacedKey(AB_NAMESPACE, "homing");

    @NotNull
    public static NamespacedKey getBlessing() {
        return BLESSING;
    }

    @NotNull
    public static NamespacedKey getHoming() {
        return HOMING;
    }

    @NotNull
    public static ItemStack getArchangelsBow(int level) {
        ItemStack bow = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("Archangel's Bow");
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        level = Math.max(1, Math.min(level, BOW_MAX_LEVEL));
        meta.getPersistentDataContainer().set(BLESSING, PersistentDataType.INTEGER, level);
        ((CrossbowMeta) meta).addChargedProjectile(new ItemStack(Material.ARROW));
        bow.setItemMeta(meta);
        return bow;
    }

    public static boolean isArchangelsBow(@Nullable ItemStack item) {
        return item != null
                && item.getType() == Material.CROSSBOW
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("Archangel's Bow")
                && item.getItemMeta().isUnbreakable()
                && item.getEnchantmentLevel(Enchantment.BINDING_CURSE) == 1
                && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)
                && item.getItemMeta().getPersistentDataContainer().has(BLESSING, PersistentDataType.INTEGER);
    }

    public static boolean isHomingArrow(@Nullable Entity entity) {
        return entity instanceof Arrow
                && !entity.isDead()
                && entity.getPersistentDataContainer().has(HOMING, PersistentDataType.INTEGER);
    }

    @NotNull
    public static ItemStack getPacketElytra() {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemMeta meta = elytra.getItemMeta();
        meta.setDisplayName("Archangel's Elytra");
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        elytra.setItemMeta(meta);
        return elytra;
    }

    public static boolean isPacketElytra(@Nullable ItemStack item) {
        return item != null
                && item.getType() == Material.ELYTRA
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("Archangel's Elytra")
                && item.getItemMeta().isUnbreakable()
                && item.getEnchantmentLevel(Enchantment.BINDING_CURSE) == 1
                && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @NotNull
    public static ItemStack getEnchantedBook(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        ItemStack enchantBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantBook.getItemMeta();
        meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        enchantBook.setItemMeta(meta);
        return enchantBook;
    }
}
