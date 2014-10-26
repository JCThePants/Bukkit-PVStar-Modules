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

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChestSettings {

    private final Arena _arena;
    private final IDataNode _chestNode;
    private final IDataNode _dataNode;

    private Map<Location, ChestInfo> _chests;
    private int _maxChests = -1;
    private boolean _hasRandomizedChests = false;

    public ChestSettings(Arena arena, IDataNode dataNode) {
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
        _dataNode.saveAsync(null);
    }

    public final boolean isChestsRandomized() {
        return _hasRandomizedChests;
    }

    public void setIsChestsRandomized(boolean isRandom) {
        _hasRandomizedChests = isRandom;
        _dataNode.set("randomize-chests", isRandom);
        _dataNode.saveAsync(null);
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
        Set<Location> chestLocations = _arena.getRegion().find(Material.CHEST);
        Map<Location, ChestInfo> chestInfo = new HashMap<>(chestLocations.size());

        _chestNode.clear();

        int count = 0;
        for (Location loc : chestLocations) {

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
        _chestNode.saveAsync(null);
    }


    private void loadChests() {

        _maxChests = _dataNode.getInteger("max-chests", _maxChests);
        _hasRandomizedChests = _dataNode.getBoolean("randomize-chests", _hasRandomizedChests);

        Set<String> chestNames = _chestNode.getSubNodeNames();
        if (chestNames != null && !chestNames.isEmpty()) {

            _chests = new HashMap<>(chestNames.size());

            for (String chestName : chestNames) {

                Location location = _chestNode.getLocation(chestName + ".location");
                if (location == null)
                    continue;

                ItemStack[] contents = _chestNode.getItemStacks(chestName + ".contents", (ItemStack[])null);

                _chests.put(location, new ChestInfo(location, contents));
            }
        }
        else {
            _chests = new HashMap<>(10);
        }
    }
}
