/* This file is part of PV-Star Modules: PVChests for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.pvs.modules.chests;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaPreStartEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.ArenaBlockDamagePreventEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ArenaExtensionInfo(
        name = "PVChests",
        description = "Add randomized chests and chest contents to an arena.")

public class ChestExtension extends ArenaExtension implements GenericsEventListener, Listener {

    public enum ClearChestRestore {
        NONE,
        PRESET_CONTENTS
    }

    private ChestSettings _chestSettings;
    private ItemSettings _itemSettings;
    private Set<ChestInfo> _openedChests = new HashSet<>(35);


    public ChestSettings getChestSettings() {
        return _chestSettings;
    }

    public ItemSettings getItemSettings() {
        return _itemSettings;
    }


    @Override
    protected void onEnable() {

        _chestSettings = new ChestSettings(getArena(), getDataNode());
        _itemSettings = new ItemSettings(getArena(), getDataNode());

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);

        restoreChests();
    }


    public boolean isChestOpened(Location loc) {
        PreCon.notNull(loc);

        return _openedChests.contains(loc);
    }

    public boolean setIsChestOpened(Location loc) {
        PreCon.notNull(loc);

        ChestInfo chestInfo = _chestSettings.getChestInfo(loc);
        return chestInfo != null && _openedChests.add(chestInfo);
    }


    public int getTotalChestsWithPresetContents() {

        if (_chestSettings.getTotalChests() == 0)
            return 0;

        int total = 0;
        for (ChestInfo chestInfo : _chestSettings.getChestInfo()) {
            if (chestInfo.getPresetContents() != null)
                total++;
        }
        return total;
    }

    public void clearChestContents(ClearChestRestore restore) {

        if (_chestSettings.getTotalChests() == 0)
            return;

        for (ChestInfo chestInfo : _chestSettings.getChestInfo()) {
            Chest chest = chestInfo.getChest();
            if (chest == null)
                continue;

            if (restore == ClearChestRestore.PRESET_CONTENTS && chestInfo.getPresetContents() != null) {
                chest.getInventory().setContents(chestInfo.getPresetContents().clone());
            }
            else {
                chest.getInventory().clear();
            }

            chest.update(true);
        }
    }



    public void randomHideChests() {

        if (_chestSettings.getTotalChests() == 0)
            return;

        getArena().getLobbyManager().tell("{YELLOW}Randomizing chest availability.");

        List<ChestInfo> chestInfoList = _chestSettings.getChestInfo();

        // no chests
        if (_chestSettings.getMaxChests() == 0) {
            for (ChestInfo chestInfo : chestInfoList) {
                chestInfo.getLocation().getBlock().setType(Material.AIR);
            }
        }
        // chests limited
        else if (_chestSettings.getMaxChests() > 0) {// simple randomize

            Set<ChestInfo> visible = new HashSet<>(chestInfoList.size());
            int maxChests = _chestSettings.getMaxChests();

            for (int i=0; i < maxChests; i++) {
                ChestInfo chest = Rand.get(chestInfoList);
                visible.add(chest);
            }

            for (ChestInfo chestInfo : chestInfoList) {
                if (!visible.contains(chestInfo)) {
                    Chest chest = chestInfo.getChest();
                    if (chest == null)
                        continue;

                    chest.getInventory().clear();
                    chestInfo.getLocation().getBlock().setType(Material.AIR);
                }
            }
        }
        // any amount of chests
        else {

            for (ChestInfo chestInfo : chestInfoList) {
                Chest chest = chestInfo.getChest();
                if (chest == null)
                    continue;

                // 1 in 4 chance of being removed
                if (Rand.getInt(3) == 0) {
                    chest.getInventory().clear();
                    chest.setType(Material.AIR);
                    chest.update(true);
                }
            }
        }
    }


    public void fillChest(Location chestLocation) {
        PreCon.notNull(chestLocation);

        // check if there are any scanned chests
        if (_chestSettings.getTotalChests() == 0)
            return;

        // get info about the chest at the chest location
        ChestInfo chestInfo = _chestSettings.getChestInfo(chestLocation);
        if (chestInfo == null)
            return;

        // determine if the chest has already been opened
        if (_openedChests.contains(chestInfo))
            return;

        // mark the chest as being opened.
        _openedChests.add(chestInfo);

        // make sure there are items to put in the chest
        if (chestInfo.getPresetContents() == null && _itemSettings.getTotalItems() == 0)
            return;

        // get the chest at the chest location
        Chest chest = chestInfo.getChest();
        if (chest == null)
            return;


        if (chestInfo.getPresetContents() == null) {
            fillRandomGlobalItems(chest, _itemSettings.getMaxRandomItems());
        }
        else if (!_itemSettings.isPresetContentsRandomized()) {
            fillContents(chest, chestInfo.getPresetContents());
        }
        else {
            fillRandomContents(chest, chestInfo.getPresetContents(), _itemSettings.getMaxRandomItems());
        }
    }


    public void restoreChests() {

        if (_chestSettings.getTotalChests() == 0)
            return;

        final Collection<ChestInfo> chests = _chestSettings.getChestInfo();

        for (ChestInfo chestInfo : chests) {
            chestInfo.getLocation().getBlock().setType(Material.CHEST);
        }

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), 1, new Runnable() {

            @Override
            public void run() {
                for (ChestInfo chestInfo : chests) {
                    Chest chest = chestInfo.getChest();
                    if (chest == null)
                        continue;

                    if (chestInfo.getPresetContents() != null) {
                        chest.getInventory().setContents(chestInfo.getPresetContents().clone());
                    } else {
                        chest.getInventory().clear();
                    }

                    chest.update(true);
                }
            }
        });
    }


    // fill chest that has no preset contents
    private void fillRandomGlobalItems(Chest chest, int maxSlots) {

        // get the chests current inventory
        Inventory inventory = chest.getBlockInventory();

        Set<ItemStack> itemsAdded = new HashSet<>(27);

        // iterate inventory slots and add items
        for (int slot = 0; slot < 27; slot++) {

            // if already reached max items to add, clear slot and continue
            if (itemsAdded.size() >= maxSlots) {
                inventory.clear(slot);
                continue;
            }

            // Determine if an item should be added based on chance
            if (Rand.getInt(7) == 0) { // TODO : Make chance editable

                // get a random item
                ItemStack item = _itemSettings.getItems().getRandom();

                // don't add item if it has already been added.
                if (itemsAdded.contains(item)) {
                    inventory.clear(slot);
                    continue;
                }

                // add item and set inventory slot
                itemsAdded.add(item);
                inventory.setItem(slot, item.clone());
            }
            else {
                inventory.clear(slot);
            }
        }

        chest.update(true);
    }


    // fill chest with random items from preset contents
    private void fillRandomContents(Chest chest, ItemStack[] source, int maxSlots) {

        Inventory inventory = chest.getBlockInventory();

        Set<ItemStack> itemsAdded = new HashSet<>(source.length);

        ItemStack[] items = spreadItems(source);

        for (int slot = 0; slot < 27; slot++) {

            if (itemsAdded.size() >= maxSlots) {
                inventory.clear(slot);
                continue;
            }

            if (Rand.getInt(7) == 0) {
                ItemStack item = Rand.get(items);

                if (itemsAdded.contains(item)) {
                    inventory.clear(slot);
                    continue;
                }

                itemsAdded.add(item);
                inventory.setItem(slot, item.clone());
                continue;
            }

            inventory.clear(slot);
        }

        chest.update(true);
    }

    // fill chest with preset contents
    private void fillContents(Chest chest, ItemStack[] source) {

        Inventory inventory = chest.getBlockInventory();

        inventory.setContents(source);

        chest.update(true);
    }


    // remove empty slots from an item stack array and
    // spreads item stack's, 1 item to a slot in the returned
    // array.
    private ItemStack[] spreadItems(ItemStack[] stacks) {
        PreCon.notNull(stacks);

        int total = countItems(stacks);
        ItemStack[] results = new ItemStack[total];

        for (int i=0, j=0; i < stacks.length && j < total; i++) {
            ItemStack stack = stacks[i];
            if (stack == null || stack.getType() == Material.AIR)
                continue;

            for (int k=0; k < stack.getAmount(); k++, j++) {
                results[j] = stack;
            }
        }

        return results;
    }

    // count the number of non empty slots in an item stack array
    // including the amount of items in each stack
    private int countItems(ItemStack[] stacks) {

        int total = 0;
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType() == Material.AIR)
                continue;

            total += stack.getAmount();
        }

        return total;
    }



    @GenericsEventHandler(priority = GenericsEventPriority.LAST)
    private void onArenaPreStart(ArenaPreStartEvent event) {

        if (_chestSettings.isChestsRandomized())
            randomHideChests();
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {

        if (_chestSettings.isChestsRandomized())
            restoreChests();

        _openedChests.clear();

        // TODO clear items ?
    }

    @GenericsEventHandler
    private void onChestInteractPrevented(ArenaBlockDamagePreventEvent event) {

        // only allow right clicking chests
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        BlockState state = event.getBlock().getState();

        // allow interacting with chests
        if (state instanceof Chest || state instanceof DoubleChest) {
            event.setCancelled(true);
        }
    }

    @GenericsEventHandler
    private void onChestInteract(PlayerInteractEvent event) {

        if (!event.hasBlock())
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getPlayer());

        // check that player is in an arena game
        if (player.getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        // determine if the player clicked a chest block
        BlockState blockState = event.getClickedBlock().getState();
        if (blockState instanceof Chest || blockState instanceof DoubleChest) {

            // fill chest with items
            fillChest(blockState.getLocation());
        }
    }


}
