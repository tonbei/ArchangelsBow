package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ABRecipeManager {

    private boolean isRecipeRegistered;
    public final List<NamespacedKey> recipeKeys;

    public ABRecipeManager(ArchangelsBow plugin) {
        isRecipeRegistered = false;

        List<NamespacedKey> tempKeys = new ArrayList<>();
        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++)
            tempKeys.add(new NamespacedKey(plugin, "bow_level_" + level));

        recipeKeys = Collections.unmodifiableList(tempKeys);
    }

    public void addRecipe() {
        if (isRecipeRegistered) return;

        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++) {
            if (!Bukkit.addRecipe(getArchangelsBowRecipe(level))) {
                Log.warning("Failed to register Archangel's Bow Recipe.");
                removeRecipe(true);
                return;
            }
        }

        Log.info("Archangel's Bow Recipes are registered.");
        isRecipeRegistered = true;
    }

    public void removeRecipe(boolean force) {
        if (!force && !isRecipeRegistered) return;

        for (NamespacedKey key : recipeKeys)
            Bukkit.removeRecipe(key);

        Log.info("Archangel's Bow Recipes are removed.");
        isRecipeRegistered = false;
    }

    @NotNull
    private ShapedRecipe getArchangelsBowRecipe(int level) {
        level = Math.max(1, Math.min(level, ABUtil.BOW_MAX_LEVEL));
        ShapedRecipe recipe = new ShapedRecipe(recipeKeys.get(level - 1), ABUtil.getArchangelsBow(level));
        switch (level) {
            case 1:
                recipe.shape("XTX", "MBI", "CSC")
                        .setIngredient('X', new ItemStack(Material.EXPERIENCE_BOTTLE, 32))
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('M', ABUtil.getEnchantedBook(Enchantment.MENDING, 1, false))
                        .setIngredient('B', Material.BOW)
                        .setIngredient('I', ABUtil.getEnchantedBook(Enchantment.LOYALTY, 3, false))
                        .setIngredient('C', new ItemStack(Material.END_CRYSTAL, 16))
                        .setIngredient('S', Material.NETHER_STAR);
                break;
            case 2:
                recipe.shape("SSS", "HAH", "SSS")
                        .setIngredient('S', new ItemStack(Material.NAUTILUS_SHELL, 4))
                        .setIngredient('H', new ItemStack(Material.HEART_OF_THE_SEA, 2))
                        .setIngredient('A', ABUtil.getArchangelsBow(1));
                break;
            case 3:
                recipe.shape("GNG", "NAN", "GNG")
                        .setIngredient('G', Material.ENCHANTED_GOLDEN_APPLE)
                        .setIngredient('N', new ItemStack(Material.NETHERITE_INGOT, 2))
                        .setIngredient('A', ABUtil.getArchangelsBow(2));
                break;
            case 4:
                recipe.shape("BNB", "SAS", "PEP")
                        .setIngredient('B', new ItemStack(Material.DRAGON_BREATH, 32))
                        .setIngredient('N', new ItemStack(Material.NETHER_STAR, 2))
                        .setIngredient('S', new ItemStack(Material.SHULKER_SHELL, 32))
                        .setIngredient('A', ABUtil.getArchangelsBow(3))
                        .setIngredient('P', new ItemStack(Material.PHANTOM_MEMBRANE, 64))
                        .setIngredient('E', Material.ELYTRA);
                break;
            case 5:
                recipe.shape("NTN", "SAS", "BUB")
                        .setIngredient('N', new ItemStack(Material.NETHER_STAR, 8))
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('S', new ItemStack(Material.HEART_OF_THE_SEA, 8))
                        .setIngredient('A', ABUtil.getArchangelsBow(4))
                        .setIngredient('B', new ItemStack(Material.DRAGON_BREATH, 64))
                        .setIngredient('U', Material.TOTEM_OF_UNDYING);
                break;
        }
        return recipe;
    }
}
