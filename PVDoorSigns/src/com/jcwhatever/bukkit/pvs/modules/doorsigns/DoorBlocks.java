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


package com.jcwhatever.bukkit.pvs.modules.doorsigns;

import com.jcwhatever.generic.signs.SignContainer;
import com.jcwhatever.generic.signs.SignHandler;
import com.jcwhatever.generic.signs.SignManager;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import java.util.List;

/**
 * Stores door blocks detected by a door sign handler.
 */
public class DoorBlocks {

    private final Arena _arena;
    private final SignHandler _handler;
    private final SignContainer _sign;
    private List<Block> _doorBlocks;
    private String _id;

    public DoorBlocks(Arena arena, SignHandler handler, SignContainer sign, List<Block> doorBlocks) {
        PreCon.notNull(arena);
        PreCon.notNull(handler);
        PreCon.notNull(sign);
        PreCon.notNull(doorBlocks);

        _arena = arena;
        _handler = handler;
        _sign = sign;
        _doorBlocks = doorBlocks;

        _id = SignManager.getSignNodeName(sign.getLocation());
    }

    public String getId() {
        return _id;
    }

    public Arena getArena() {
        return _arena;
    }

    public SignHandler getSignHandler() {
        return _handler;
    }

    public SignContainer getSignContainer() {
        return _sign;
    }

    public int totalDoors() {
        return _doorBlocks.size() / 2;
    }

    public boolean setOpen(boolean isOpen) {
        int interacted = 0;
        Block worldBlock = null;

        for (Block block : _doorBlocks) {

            if (setDoorBlock(block, isOpen)) {
                worldBlock = block;
                interacted++;
            }
        }

        if (interacted > 0 && worldBlock != null) {

            worldBlock.getWorld().playSound(worldBlock.getLocation(),
                    isOpen
                            ? Sound.DOOR_OPEN
                            : Sound.DOOR_CLOSE, 1.0f, 0.1f);

            return true;
        }
        return false;
    }

    private boolean setDoorBlock(Block block, boolean isOpen) {
        BlockState state = block.getState();

        // only looking for the lowest door block
        Block lowerBlock = block.getRelative(BlockFace.DOWN);
        if (lowerBlock.getType() == Material.IRON_DOOR_BLOCK)
            return false;

        MaterialData data = state.getData();
        Openable opn = (Openable) data;

        if (opn.isOpen() == isOpen)
            return false;

        opn.setOpen(isOpen);

        state.setData(data);
        state.update(true);

        return true;
    }

    public boolean isOpen() {

        if (_doorBlocks.size() == 0) {
            return false;
        }

        for (Block doorBlock : _doorBlocks) {
            BlockState doorState = doorBlock.getState();
            MaterialData data = doorState.getData();
            Openable opn = (Openable) data;
            if (!opn.isOpen())
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof DoorBlocks && ((DoorBlocks) obj)._id.equals(_id);
    }
}
