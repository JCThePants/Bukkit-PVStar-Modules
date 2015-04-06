/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.pvs.modules.chests;

import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class ChestSettings {

    private final IArena _arena;
    private final IDataNode _chestNode;
    private final IDataNode _dataNode;

    private Map<Location, ChestInfo> _chests;
    private int _maxChests = -1;
    private boolean _hasRandomizedChests = false;

    public ChestSettings(IArena arena, IDataNode dataNode) {
        PreCon.notNull(arena);
        PreCon.notNull(dataNode);

        _arena = arena;
        _dataNode = dataNode;
        _chestNode = dataNode.getNode("chest-data");

        loadChests();
    }

    public int getMaxChests() {
        return _maxChests;
    }

    public void setMaxChests(int max) {
        _maxChests = max;
        _dataNode.set("max-chests", max);
        _dataNode.save();
    }

    public final boolean isChestsRandomized() {
        return _hasRandomizedChests;
    }

    public void setIsChestsRandomized(boolean isRandom) {
        _hasRandomizedChests = isRandom;
        _dataNode.set("randomize-chests", isRandom);
        _dataNode.save();
    }


    public int getTotalChests() {
        return _chests != null ? _chests.size() : 0;
    }

    @Nullable
    public ChestInfo getChestInfo(Location chestLocation) {
        PreCon.notNull(chestLocation);

        if (_chests == null)
            return null;

        return _chests.get(chestLocation);
    }

    public List<ChestInfo> getChestInfo() {
        if (_chests == null)
            return new ArrayList<>(0);

        return new ArrayList<>(_chests.values());
    }

    public void scanChests() {
        LinkedList<Location> chestLocations = _arena.getRegion().find(Material.CHEST);
        Map<Location, ChestInfo> chestInfo = new HashMap<>(chestLocations.size());

        _chestNode.clear();

        int count = 0;
        while (!chestLocations.isEmpty()) {

            Location loc = chestLocations.remove();

            BlockState state = loc.getBlock().getState();
            if (!(state instanceof Chest))
                continue;

            count++;

            String chestName = "c" + count;

            // add chest location

            _chestNode.set(chestName + ".location", loc);

            // add contents, if any
            Chest chest = (Chest)state;
            ItemStack[] contents = chest.getInventory().getContents();

            boolean hasContents = false;
            for (ItemStack content : contents) {
                if (content != null && content.getType() != Material.AIR) {
                    hasContents = true;
                    break;
                }
            }

            if (hasContents)
                _chestNode.set(chestName + ".contents", contents);

            ChestInfo info = new ChestInfo(loc, hasContents ? contents : null);
            chestInfo.put(loc, info);
        }
        _chests = chestInfo;
        _chestNode.save();
    }


    private void loadChests() {

        _maxChests = _dataNode.getInteger("max-chests", _maxChests);
        _hasRandomizedChests = _dataNode.getBoolean("randomize-chests", _hasRandomizedChests);

        _chests = new HashMap<>(_chestNode.size());

        for (IDataNode chestNode : _chestNode) {

            Location location = chestNode.getLocation("location");
            if (location == null)
                continue;

            ItemStack[] contents = chestNode.getItemStacks("contents", (ItemStack[])null);

            _chests.put(location, new ChestInfo(location, contents));
        }
    }
}
