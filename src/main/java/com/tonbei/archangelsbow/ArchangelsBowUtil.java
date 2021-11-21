package com.tonbei.archangelsbow;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class ArchangelsBowUtil {

    public static final int MAX_LEVEL = 1;

    @NotNull
    public static ItemStack getArchangelsBow(int level) {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("Archangel's Bow");
        meta.setUnbreakable(true);
        bow.setItemMeta(meta);
        bow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, Math.max(1, Math.min(level, MAX_LEVEL)));
        return bow;
    }

    public static boolean isArchangelsBow(@Nullable ItemStack item) {
        return item != null
                && item.getType() == Material.BOW
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals("Archangel's Bow")
                && item.getItemMeta().isUnbreakable()
                && item.getEnchantmentLevel(Enchantment.QUICK_CHARGE) >= 1;
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
        Bukkit.addRecipe(getArchangelsBowRecipe(1));
        ArchangelsBow.isRecipeRegistered = true;
    }

    static void removeRecipe() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext())
            if (isArchangelsBow(iterator.next().getResult()))
                iterator.remove();

        ArchangelsBow.isRecipeRegistered = false;
    }

    static ShapedRecipe getArchangelsBowRecipe(int level) {
        level = Math.max(1, Math.min(level, MAX_LEVEL));
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ArchangelsBow.getPlugin(ArchangelsBow.class), "ArchangelsBow_" + level),
                                                getArchangelsBow(level));
        switch (level) {
            case 1:
                recipe.shape("FTF", "MBI", "CSC")
                        .setIngredient('F', Material.FEATHER)
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('M', new RecipeChoice.ExactChoice(getEnchantedBook(Enchantment.MENDING, 1, false)))
                        .setIngredient('I', new RecipeChoice.ExactChoice(getEnchantedBook(Enchantment.ARROW_INFINITE, 1, false)))
                        .setIngredient('C', Material.END_CRYSTAL)
                        .setIngredient('S', Material.NETHER_STAR);
                break;
        }
        return recipe;
    }
}
