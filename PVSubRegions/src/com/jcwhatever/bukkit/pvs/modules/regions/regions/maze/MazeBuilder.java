/* This file is part of PV-Star Modules: PVSubRegions for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.regions.regions.maze;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.regions.BuildChunkSnapshot;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.regions.BuildableRegion;
import com.jcwhatever.bukkit.generic.regions.RegionChunkSection;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.maze.MazeGenerator.Orientation;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MazeBuilder {

    private static final int BLOCK_SIZE = 3; // used as default value only
    private static MazeGenerator _generator;
    private IDataNode _settings;
    private BuildableRegion _region;
    private Plugin _plugin;
    private ItemStack[][][] _map;
    private ItemStack[] _wallMaterials;

    public MazeBuilder (Plugin plugin, BuildableRegion region, IDataNode settings) {
        _plugin = plugin;
        _region = region;
        _settings = settings;
        _generator = new MazeGenerator();
    }

    public int getBlockSize() {
        return _settings.getInteger("block-size", BLOCK_SIZE);
    }

    public boolean isRendering() {
        return _region.isBuilding();
    }

    public void render() {
        if (isRendering())
            return;

        render(getBlockSize(), -1);
    }

    public void render(int blockSize) {
        if (isRendering())
            return;

        render(blockSize, -1);
    }

    private int _xOffset;
    private int _zOffset;
    private int _xBlockCount;
    private int _zBlockCount;
    private int _height;

    public void render(int blockSize, int maxIterations) {

        if (isRendering())
            return;

        if (!_plugin.isEnabled())
            return;

        initRender(blockSize);

        Orientation[][] mazeGrid = _generator.get2DMaze(_xBlockCount, _zBlockCount, maxIterations);

        for (int z = 0; z < _zBlockCount; z++) {
            for (int x = 0; x < _xBlockCount; x++) {
                for (int y = 0; y < _height; y++) {

                    mapMazeBlock(blockSize, x, y, z, mazeGrid[z][x]);
                }
            }
        }

        List<Chunk> chunks = _region.getChunks();
        List<BuildChunkSnapshot> snapshots = new ArrayList<BuildChunkSnapshot>(chunks.size());
        for (Chunk chunk : chunks) {
            RegionChunkSection section = new RegionChunkSection(_region, chunk);
            BuildChunkSnapshot mazeSnap = new BuildChunkSnapshot(_map, section);
            snapshots.add(mazeSnap);
        }

        _region.build(BuildMethod.PERFORMANCE, snapshots);
    }


    public void test() {
        test(getBlockSize());
    }

    public void test(int blockSize) {

        initRender(blockSize);

        for (int z = 0; z < _zBlockCount; z++) {
            for (int x = 0; x < _xBlockCount; x++) {
                for (int y = 0; y < _height; y++) {

                    mapMazeBlock(blockSize, x, y, z, null);
                }
            }
        }
    }


    private static boolean omitColumn(Orientation orientation, int xStart, int zStart) {
        return  (zStart == 0 && !hasZWall(orientation)) ||
                (xStart == 0 && !hasXWall(orientation));
    }

    private static boolean hasZWall(Orientation orientation) {
        return orientation == Orientation.Z ||
                orientation == Orientation.BOTH ||
                orientation == Orientation.DOORX_WALLZ;
    }

    private static boolean hasXWall(Orientation orientation) {
        return orientation == Orientation.X ||
                orientation == Orientation.BOTH ||
                orientation == Orientation.DOORZ_WALLX;
    }

    private void initRender(int blockSize) {
        _xBlockCount = (int)Math.floor((double)_region.getXBlockWidth() / blockSize);
        _zBlockCount = (int)Math.floor((double)_region.getZBlockWidth() / blockSize);
        _height = _region.getYBlockHeight();

        // center in region
        _xOffset = (int) Math.floor(((double)_region.getXBlockWidth() - (_xBlockCount * blockSize)) / 2);
        _zOffset = (int) Math.floor(((double)_region.getZBlockWidth() - (_zBlockCount * blockSize)) / 2);

        _map = _region.getBuildArray();
        _wallMaterials = getWallMaterials();
    }

    private void mapMazeBlock(int size, int xStart, int y, int zStart, Orientation orientation) {

        int xSize = size;
        int zSize = size;

        int xOffset = _xOffset;
        int zOffset = _zOffset;

        // note: region sizes that are not multiples of the block size leave gaps
        // The maze is centered in the region and the block sizes around the outer
        // perimeter are increased to fill the gap.

        if (xStart == 0) { // expand starting x blocks to fill gap
            xSize = _xOffset + size;
            xOffset = 0;
        }
        else if (xStart == _xBlockCount - 1) { // expand ending x blocks to fill gap
            int start = (xStart * size) + _xOffset;
            xSize = (_region.getXBlockWidth() - start);
        }



        if (zStart == 0) {// expand starting z blocks to fill gap
            zSize = _zOffset + size;
            zOffset = 0;
        }
        else if (zStart == _zBlockCount - 1) { // expand ending z blocks to fill gap
            int start = (zStart * size) + _zOffset;
            zSize = (_region.getZBlockWidth() - start);
        }


        //xStart == 0 ? _xStart + size : size;

        for (int x = 0; x < xSize; x++) {
            for (int z = 0; z < zSize; z++) {

                //Location loc = new Location(_region.start.getWorld(), start.getX() + x, start.getY(), start.getZ() + z);

                int mapx = (xStart * size) + x + xOffset;
                int mapz = (zStart * size) + z + zOffset;



                ItemStack wall = Rand.get(_wallMaterials);

                if (x == 0 && z == 0 && !omitColumn(orientation, xStart, zStart)) {
                    _map[mapx][y][mapz] = wall;
                }
                else if (x == 0 && hasZWall(orientation)) {
                    _map[mapx][y][mapz] = wall;
                }
                else if (z == 0 && hasXWall(orientation)) {
                    _map[mapx][y][mapz] = wall;
                }
                else {
                    _map[mapx][y][mapz] = ItemStackHelper.AIR;
                }

            }
        }
    }


    public ItemStack[] getWallMaterials() {
        ItemStack[] materials = _settings.getItemStacks("wall-materials");

        if (materials == null || materials.length == 0) {
            materials = new ItemStack[] { new ItemStack(Material.SMOOTH_BRICK) };
        }

        return materials;
    }


}
