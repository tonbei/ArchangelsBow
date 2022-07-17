package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.ABRecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ABCraftListener implements Listener {

    private final List<NamespacedKey> recipeKeys;

    public ABCraftListener(ABRecipeManager manager) {
        recipeKeys = manager.recipeKeys;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlaceItem(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();

        if (e.isRepair()) return;
        if (inv.getMatrix().length < 9) return;

        if (e.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe recipe = (ShapedRecipe) e.getRecipe();
            if (recipeKeys.stream().anyMatch(recipe.getKey()::equals)) {
                List<List<ItemStack>> recipeMatrix = getRecipeMatrix(recipe);
                ItemStack[] matrix = inv.getMatrix();
                boolean cancelFlag = false;

                for (int index = 0; index < 9; index++) {
                    if (cancelFlag) break;

                    ItemStack item = matrix[index];
                    List<ItemStack> itemList = recipeMatrix.get(index);

                    if (item == null) continue;

                    for (ItemStack recipeItem : itemList) {
                        if (recipeItem == null) continue;

                        if (item.getType() == recipeItem.getType()) {
                            if (item.getAmount() < recipeItem.getAmount()) {
                                cancelFlag = true;
                            }
                            break;
                        }
                    }
                }

                if (cancelFlag) inv.setResult(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickUpCraftItem(CraftItemEvent e) {
        CraftingInventory inv = e.getInventory();

        if (inv.getMatrix().length < 9) return;

        if (e.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe recipe = (ShapedRecipe) e.getRecipe();
            if (recipeKeys.stream().anyMatch(recipe.getKey()::equals)) {
                List<List<ItemStack>> recipeMatrix = getRecipeMatrix(recipe);
                ItemStack[] matrix = inv.getMatrix();
                ItemStack[] afterMatrix = new ItemStack[9];
                boolean cancelFlag = false;

                for (int index = 0; index < 9; index++) {
                    if (cancelFlag) break;

                    ItemStack item = matrix[index];
                    List<ItemStack> itemList = recipeMatrix.get(index);

                    if (item == null) {
                        afterMatrix[index] = null;
                        continue;
                    }

                    for (ItemStack recipeItem : itemList) {
                        if (recipeItem == null) continue;

                        if (item.getType() == recipeItem.getType()) {
                            int amount = item.getAmount() - recipeItem.getAmount();
                            if (amount < 0) {
                                cancelFlag = true;
                            } else if (amount == 0) {
                                afterMatrix[index] = null;
                            } else {
                                ItemStack afterItem = item.clone();
                                afterItem.setAmount(amount);
                                afterMatrix[index] = afterItem;
                            }
                            break;
                        }
                    }
                }

                if (cancelFlag) {
                    inv.setResult(null);
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            inv.setMatrix(afterMatrix);
                        }
                    }.runTask(ArchangelsBow.getInstance());

                    inv.setResult(recipe.getResult());
                }
            }
        }
    }

    public List<List<ItemStack>> getRecipeMatrix(ShapedRecipe recipe) {
        List<List<ItemStack>> matrix = new ArrayList<>();
        Map<Character, RecipeChoice> recipeChoices = recipe.getChoiceMap();
        for (String row : recipe.getShape()) {
            for (Character c : row.toCharArray()) {
                RecipeChoice choice = recipeChoices.get(c);
                if (choice instanceof RecipeChoice.MaterialChoice)
                    matrix.add(((RecipeChoice.MaterialChoice) choice).getChoices().stream().map(ItemStack::new).collect(Collectors.toList()));
                else if (choice instanceof RecipeChoice.ExactChoice)
                    matrix.add(((RecipeChoice.ExactChoice) choice).getChoices());
                else
                    matrix.add(null);
            }

            for (int i = 0; i < 3 - row.toCharArray().length; i++)
                matrix.add(null);
        }

        for (int i = 0; i < 3 - recipe.getShape().length; i++)
            for (int j = 0; j < 3; j++)
                matrix.add(null);

        return matrix;
    }
}
