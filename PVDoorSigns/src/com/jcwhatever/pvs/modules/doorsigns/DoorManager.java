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


package com.jcwhatever.pvs.modules.doorsigns;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.signs.ISignContainer;
import com.jcwhatever.nucleus.utils.signs.SignHandler;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.events.ArenaStartedEvent;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class DoorManager implements IEventListener {

    private Map<String, DoorBlocks> _doorsBySign = new HashMap<>(20);
    private Multimap<Arena, DoorBlocks> _doorsByArena =
            MultimapBuilder.hashKeys(35).hashSetValues(10).build();

    public DoorManager() {
        PVStarAPI.getEventManager().register(this);
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    public void addArenaDoorBlocks(Arena arena, DoorBlocks doorBlocks) {
        _doorsByArena.put(arena, doorBlocks);
    }

    public void removeArenaDoorBlocks(String doorBlocksId) {

        DoorBlocks doorBlocks = _doorsBySign.remove(doorBlocksId);
        if (doorBlocks == null)
            return;

        doorBlocks.setOpen(false);

        _doorsByArena.remove(doorBlocks.getArena(), doorBlocks);
    }

    @Nullable
    public DoorBlocks findDoors(SignHandler handler, ISignContainer signContainer) {
        PreCon.notNull(handler);
        PreCon.notNull(signContainer);

        Sign sign = signContainer.getSign();
        assert sign != null;

        Arena arena = PVStarAPI.getArenaManager().getArena(sign.getLocation());
        if (arena == null)
            return null;

        int locationX = sign.getX() - 3;
        int locationY = sign.getY() - 3;
        int locationZ = sign.getZ() - 3;
        World world = sign.getWorld();
        ArrayList<Block> doorBlocks = new ArrayList<>(4);

        int xEnd = locationX + 6;
        for (int x = locationX; x < xEnd; x++) {

            int yEnd = locationY + 6;
            for (int y = locationY; y < yEnd; y++) {

                int zEnd = locationZ + 6;
                for (int z = locationZ; z < zEnd; z++) {

                    Block searchBlock = world.getBlockAt(x, y, z);

                    if (searchBlock.getType() != Material.IRON_DOOR_BLOCK)
                        continue;

                    doorBlocks.add(searchBlock);
                }
            }
        }

        if (doorBlocks.size() == 0) {
            return null;
        }

        return getDoorBlocks(arena, handler, signContainer, doorBlocks);
    }

    private DoorBlocks getDoorBlocks(Arena arena, SignHandler handler, ISignContainer sign, List<Block> doorBlocks) {

        String doorId = LocationUtils.serialize(sign.getLocation());

        if (_doorsBySign.containsKey(doorId)) {
            return _doorsBySign.get(doorId);
        }

        DoorBlocks door = new DoorBlocks(arena, handler, sign, doorBlocks);

        _doorsBySign.put(doorId, door);
        _doorsByArena.put(arena, door);
        return door;
    }

    private void closeDoors(Arena arena) {

        Collection<DoorBlocks> doorBlocks = _doorsByArena.removeAll(arena);
        if (doorBlocks == null)
            return;

        for (DoorBlocks doorBlock : doorBlocks) {
            doorBlock.setOpen(false);

            Nucleus.getSignManager().restoreSign(
                    doorBlock.getSignHandler().getName(),
                    doorBlock.getSignContainer().getLocation());
        }
    }

    @EventMethod
    private void onArenaStarted(ArenaStartedEvent event) {
        closeDoors(event.getArena());
    }

    @EventMethod
    private void onArenaEnded(ArenaEndedEvent event) {
        closeDoors(event.getArena());
    }

}
