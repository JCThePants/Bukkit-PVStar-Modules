package com.jcwhatever.bukkit.pvs.modules.chests;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.items.WeightedItems;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemSettings {

    private final Arena _arena;
    private final IDataNode _dataNode;
    private final IDataNode _itemsNode;
    private WeightedItems _chestItems;
    private boolean _isPresetContentsRandomized = false; // randomize contents of chests with preset contents
    private int _maxRandomItems = 4;


    public ItemSettings(Arena arena, IDataNode dataNode) {
        _arena = arena;
        _dataNode = dataNode;
        _itemsNode = dataNode.getNode("chest-items");

        loadItemSettings();
    }

    public Arena getArena() {
        return _arena;
    }

    public int getTotalItems() {
        return _chestItems.size();
    }

    public int getMaxRandomItems() {
        return _maxRandomItems;
    }

    public void setMaxRandomItems(int max) {
        _maxRandomItems = max;
        _dataNode.set("max-random-items", max);
        _dataNode.saveAsync(null);
    }

    public boolean isPresetContentsRandomized() {
        return _isPresetContentsRandomized;
    }

    public void setPresetContentsRandomized(boolean isRandomContents) {
        _isPresetContentsRandomized = isRandomContents;
        _dataNode.set("random-contents", isRandomContents);
        _dataNode.saveAsync(null);
    }

    public WeightedItems getItems() {
        return _chestItems;
    }

    public void clearItems() {
        _chestItems.clear();
        _itemsNode.remove("items");
    }

    public void addDefaultItems() {

        new DefaultItemHelper()
                .add("APPLE")       		.add("ARROW")
                .add("BAKED_POTATO")		.add("BLAZE_POWDER")
                .add("BOAT")        		.add("BONE")
                .add("BOWL")        		.add("BREAD")
                .add("BROWN_MUSHROOM")		.add("CAKE")
                .add("CARROT")      		.add("CHAINMAIL_BOOTS")
                .add("CHAINMAIL_CHESTPLATE").add("CHAINMAIL_HELMET")
                .add("CHAINMAIL_LEGGINGS")	.add("COAL")
                .add("COOKED_BEEF")			.add("COOKED_CHICKEN")
                .add("COOKED_FISH")			.add("COOKIE")
                .add("DIAMOND")				.add("DIAMOND_AXE")
                .add("DIAMOND_BOOTS")		.add("DIAMOND_CHESTPLATE")
                .add("DIAMOND_HELMET")		.add("DIAMOND_LEGGINGS")
                .add("DIAMOND_SWORD")		.add("EGG")
                .add("ENDER_PEARL")			.add("FEATHER")
                .add("ARROW")				.add("FLINT")
                .add("FLINT_AND_STEEL")		.add("GOLD_AXE")
                .add("GOLD_BOOTS")			.add("GOLD_CHESTPLATE")
                .add("GOLD_HELMET")			.add("GOLD_INGOT")
                .add("GOLD_LEGGINGS")		.add("GOLD_SWORD")
                .add("IRON_AXE")			.add("IRON_BOOTS")
                .add("IRON_CHESTPLATE")		.add("IRON_HELMET")
                .add("IRON_INGOT")			.add("IRON_LEGGINGS")
                .add("LEATHER_BOOTS")		.add("LEATHER_CHESTPLATE")
                .add("LEATHER_HELMET")		.add("LEATHER_LEGGINGS")
                .add("MELON")				.add("ARROW")
                .add("NETHER_WARTS")		.add("ARROW")
                .add("POISONOUS_POTATO")	.add("PORK")
                .add("POTATO_ITEM")			.add("PUMPKIN_PIE")
                .add("RAW_BEEF")			.add("RAW_CHICKEN")
                .add("RAW_FISH")			.add("ROTTEN_FLESH")
                .add("SNOW_BALL")			.add("STICK")
                .add("STONE_AXE")			.add("STONE_SWORD")
                .add("STRING")				.add("STICK")
                .add("WOOD_AXE")			.add("WOOD_SWORD")
        ;
    }

    private void loadItemSettings() {

        _isPresetContentsRandomized = _dataNode.getBoolean("is-random-contents", _isPresetContentsRandomized);
        _maxRandomItems = _dataNode.getInteger("max-random-items", _maxRandomItems);

        ItemStack[] items = _itemsNode.getItemStacks("items");
        if (items == null) {
            _chestItems = new WeightedItems();
        }
        else {
            _chestItems = new WeightedItems(items);
        }
    }

    private void saveChestItems() {
        List<ItemStack> listItems = _chestItems.getItemStacks();
        ItemStack[] items = listItems.toArray(new ItemStack[listItems.size()]);
        _itemsNode.set("items", items);
        _itemsNode.saveAsync(null);
    }

    private class DefaultItemHelper {
        public DefaultItemHelper add(String materialName) {
            ItemStack[] stacks = ItemStackHelper.parse(materialName);
            _chestItems.add(stacks);
            saveChestItems();
            return this;
        }
    }


}
