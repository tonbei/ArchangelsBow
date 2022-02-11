package com.github.tonbei.archangelsbow;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ABUtil {

    public static final int BOW_MAX_LEVEL = 1;

    private static boolean isRecipeRegistered = false;

    private static NamespacedKey BLESSING;
    private static NamespacedKey HOMING;
    private static final List<NamespacedKey> recipeKeys = new ArrayList<>();

    static void init(Plugin plugin) {
        BLESSING = new NamespacedKey(plugin, "blessing");
        HOMING = new NamespacedKey(plugin, "homing");

        recipeKeys.clear();
        for (int level = 1; level <= BOW_MAX_LEVEL; level++)
            recipeKeys.add(new NamespacedKey(plugin, "bow_level_" + level));
    }

    public static NamespacedKey getBlessing() {
        return BLESSING;
    }

    public static NamespacedKey getHoming() {
        return HOMING;
    }

    @NotNull
    public static ItemStack getArchangelsBow(int level) {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("Archangel's Bow");
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        level = Math.max(1, Math.min(level, BOW_MAX_LEVEL));
        meta.getPersistentDataContainer().set(BLESSING, PersistentDataType.INTEGER, level);
        bow.setItemMeta(meta);
        return bow;
    }

    public static boolean isArchangelsBow(@Nullable ItemStack item) {
        return item != null
                && item.getType() == Material.BOW
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("Archangel's Bow")
                && item.getItemMeta().isUnbreakable()
                && item.getEnchantmentLevel(Enchantment.QUICK_CHARGE) == 1
                && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)
                && item.getItemMeta().getPersistentDataContainer().has(BLESSING, PersistentDataType.INTEGER);
    }

    public static boolean isHomingArrow(@Nullable Entity entity) {
        return entity instanceof Arrow
                && !entity.isDead()
                && entity.getPersistentDataContainer().has(HOMING, PersistentDataType.INTEGER);
    }

    @NotNull
    public static ItemStack getEnchantedBook(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        ItemStack enchantBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantBook.getItemMeta();
        meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        enchantBook.setItemMeta(meta);
        return enchantBook;
    }

    static void addRecipe() {
        if (isRecipeRegistered) return;

        for (int level = 1; level <= BOW_MAX_LEVEL; level++) {
            if (!Bukkit.addRecipe(getArchangelsBowRecipe(level))) {
                Log.warning("Failed to register Archangel's Bow Recipe.");
                removeRecipe(true);
                return;
            }
        }

        Log.info("Archangel's Bow Recipes are registered.");
        isRecipeRegistered = true;
    }

    static void removeRecipe(boolean force) {
        if (!force && !isRecipeRegistered) return;

        for (NamespacedKey key : recipeKeys)
            Bukkit.removeRecipe(key);

        Log.info("Archangel's Bow Recipes are removed.");
        isRecipeRegistered = false;
    }

    static ShapedRecipe getArchangelsBowRecipe(int level) {
        level = Math.max(1, Math.min(level, BOW_MAX_LEVEL));
        ShapedRecipe recipe = new ShapedRecipe(recipeKeys.get(level - 1), getArchangelsBow(level));
        switch (level) {
            case 1:
                recipe.shape("FTF", "MBI", "CSC")
                        .setIngredient('F', Material.FEATHER)
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('M', new RecipeChoice.ExactChoice(getEnchantedBook(Enchantment.MENDING, 1, false)))
                        .setIngredient('B', Material.BOW)
                        .setIngredient('I', new RecipeChoice.ExactChoice(getEnchantedBook(Enchantment.ARROW_INFINITE, 1, false)))
                        .setIngredient('C', Material.END_CRYSTAL)
                        .setIngredient('S', Material.NETHER_STAR);
                break;
        }
        return recipe;
    }
}
