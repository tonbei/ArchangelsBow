package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ABRecipeManager {

    private boolean isRecipeRegistered;
    public final Map<ItemStack, List<List<ItemStack>>> stackRecipeList;

    public ABRecipeManager(ArchangelsBow plugin) {
        isRecipeRegistered = false;

        Map<ItemStack, List<List<ItemStack>>> tempStackRecipe = new HashMap<>();
        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++)
            tempStackRecipe.put(ABUtil.getArchangelsBow(level), getArchangelsBowRecipe(level));

        stackRecipeList = Collections.unmodifiableMap(tempStackRecipe);
    }

    public void addRecipe() {
        /*if (isRecipeRegistered) return;

        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++) {
            if (!Bukkit.addRecipe(getArchangelsBowRecipe(level))) {
                Log.warning("Failed to register Archangel's Bow Recipe.");
                removeRecipe(true);
                return;
            }
        }

        Log.info("Archangel's Bow Recipes are registered.");
        isRecipeRegistered = true;*/
    }

    public void removeRecipe(boolean force) {
        /*if (!force && !isRecipeRegistered) return;

        for (NamespacedKey key : recipeKeys)
            Bukkit.removeRecipe(key);

        Log.info("Archangel's Bow Recipes are removed.");
        isRecipeRegistered = false;*/
    }

    @NotNull
    private List<List<ItemStack>> getArchangelsBowRecipe(int level) {
        level = Math.max(1, Math.min(level, ABUtil.BOW_MAX_LEVEL));
        List<List<ItemStack>> recipe = new ArrayList<>();
        switch (level) {
            case 1:
                recipe.add(Arrays.asList(new ItemStack(Material.EXPERIENCE_BOTTLE, 32)));
                recipe.add(Arrays.asList(new ItemStack(Material.TRIDENT)));
                recipe.add(Arrays.asList(new ItemStack(Material.EXPERIENCE_BOTTLE, 32)));
                recipe.add(Arrays.asList(ABUtil.getEnchantedBook(Enchantment.MENDING, 1, false)));
                recipe.add(Arrays.asList(new ItemStack(Material.BOW)));
                recipe.add(Arrays.asList(ABUtil.getEnchantedBook(Enchantment.LOYALTY, 3, false)));
                recipe.add(Arrays.asList(new ItemStack(Material.END_CRYSTAL)));
                recipe.add(Arrays.asList(new ItemStack(Material.NETHER_STAR)));
                recipe.add(Arrays.asList(new ItemStack(Material.END_CRYSTAL)));
                break;
        }
        return Collections.unmodifiableList(recipe);
    }
}
