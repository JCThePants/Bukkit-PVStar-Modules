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


package com.jcwhatever.pvs.modules.randombox;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3D;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.mixins.IArenaOwned;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores info about chests in an arena.
 */
public class ArenaChests implements IArenaOwned {

    private final IArena _arena;
    private final IDataNode _chestNode;
    private final IDataNode _dataNode;

    private Map<Location, ChestInfo> _chests;
    private int _maxChests = -1;
    private boolean _hasRandomizedChests = false;

    /**
     * Constructor.
     *
     * @param arena     The owning arena.
     * @param dataNode  The data node where the arenas chest data is stored.
     */
    public ArenaChests(IArena arena, IDataNode dataNode) {
        PreCon.notNull(arena);
        PreCon.notNull(dataNode);

        _arena = arena;
        _dataNode = dataNode;
        _chestNode = dataNode.getNode("chest-data");

        load();
    }

    @Override
    public IArena getArena() {
        return _arena;
    }

    /**
     * Get the max number of chests to keep when randomizing the presence
     * of chests in the arena.
     */
    public int getMax() {
        return _maxChests;
    }

    /**
     * Set the max number of chests to allow when randomizing the presence
     * of chests in the arena.
     *
     * @param max  The max number of chests.
     */
    public void setMax(int max) {
        _maxChests = max;
        _dataNode.set("max-chests", max);
        _dataNode.save();
    }

    /**
     * Determine if the presence of chests is randomized.
     */
    public final boolean isRandomized() {
        return _hasRandomizedChests;
    }

    /**
     * Change the chest randomizing setting.
     *
     * @param isRandom  True to randomize the presence of chests, otherwise false.
     */
    public void setRandomized(boolean isRandom) {
        _hasRandomizedChests = isRandom;
        _dataNode.set("randomize-chests", isRandom);
        _dataNode.save();
    }

    /**
     * Get the total number of chests found in the last scan for chests.
     *
     * <p>Represents the total number of chests known to exist in the arena.</p>
     *
     * @return  The number of chests. 0 indicates either no chests or a scan has not been done.
     */
    public int size() {
        return _chests != null ? _chests.size() : 0;
    }

    /**
     * Get info about a chest at the specified block location.
     *
     * @param chestLocation  The block location of the chest.
     *
     * @return  The {@link ChestInfo} or null if a chest is not known to exist
     * in the specified location.
     */
    @Nullable
    public ChestInfo getInfo(Location chestLocation) {
        PreCon.notNull(chestLocation);

        if (_chests == null)
            return null;

        return _chests.get(chestLocation);
    }

    /**
     * Get info for all chests known to exist in the arena.
     */
    public List<ChestInfo> getInfo() {
        if (_chests == null)
            return new ArrayList<>(0);

        return new ArrayList<>(_chests.values());
    }

    /**
     * Scan for and update the collection of chests that are known to exist.
     */
    public void scan() {
        Deque<Location> chestLocations = _arena.getRegion().find(Material.CHEST);
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

    /*
     * Load chest info and settings from the data node.
     */
    private void load() {

        _maxChests = _dataNode.getInteger("max-chests", _maxChests);
        _hasRandomizedChests = _dataNode.getBoolean("randomize-chests", _hasRandomizedChests);

        _chests = new HashMap<>(_chestNode.size());

        for (IDataNode chestNode : _chestNode) {

            SyncLocation location = chestNode.getLocation("location");
            if (location == null)
                continue;

            ItemStack[] contents = chestNode.getItemStacks("contents", (ItemStack[])null);

            Location bukkitLocation = location.getBukkitLocation();

            _chests.put(bukkitLocation, new ChestInfo(bukkitLocation, contents));
        }
    }

    /**
     * Contains info about a single chest in the arena.
     */
    public static class ChestInfo {

        private final World world;
        private final Coords3D coords;
        private final ItemStack[] contents;

        /**
         * Constructor.
         *
         * @param location  The location of the chest.
         * @param contents  The contents of the chest.
         */
        public ChestInfo (Location location, @Nullable ItemStack[] contents) {
            this.world = location.getWorld();
            this.coords = Coords3D.fromLocation(location);
            this.contents = contents;
        }

        /**
         * Get the world the chest is in.
         * @return
         */
        public World getWorld() {
            return world;
        }

        /**
         * Get the location of the chest.
         */
        public Location getLocation() {
            return coords.toLocation(world);
        }

        /**
         * Get the preset contents of the chest.
         *
         * @return  Null if the chest has no preset contents.
         */
        @Nullable
        public ItemStack[] getPresetContents() {
            return contents;
        }

        /**
         * Get the chest at the location.
         *
         * @return  Null if the location of the chest info is not a chest.
         */
        @Nullable
        public Chest getChest() {
            BlockState state = coords.getBlock(world).getState();

            if (state instanceof Chest)
                return (Chest)state;

            return null;
        }

        @Override
        public int hashCode() {
            return coords.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ChestInfo &&
                    ((ChestInfo) obj).coords.equals(coords) &&
                    ((ChestInfo) obj).world.equals(world);
        }
    }
}
