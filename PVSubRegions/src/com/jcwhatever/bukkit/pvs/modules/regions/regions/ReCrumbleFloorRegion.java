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


package com.jcwhatever.bukkit.pvs.modules.regions.regions;


import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.events.manager.IEventListener;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.PropertyDefinition;
import com.jcwhatever.bukkit.generic.storage.settings.PropertyValueType;
import com.jcwhatever.bukkit.generic.storage.settings.SettingsBuilder;
import com.jcwhatever.bukkit.generic.utils.BlockUtils;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@RegionTypeInfo(
        name="recrumble",
        description="A region of blocks that are removed moments after being walked on by players. Blocks are restored moments later.")
public class ReCrumbleFloorRegion extends AbstractPVRegion implements IEventListener {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("affected-blocks", PropertyValueType.ITEM_STACK_ARRAY,
                        "Set the blocks affected by the region. Null or air affects all blocks.")

                .set("crumble-delay-ticks", PropertyValueType.INTEGER, 0,
                        "The delay time in ticks that a block crumbles after being stepped on.")

                .set("rebuild-delay-ticks", PropertyValueType.INTEGER, 40,
                        "The delay time in ticks before a block is placed back after crumbling.")
                .buildDefinitions()
        ;
    }

    private Set<Location> _dropped = new HashSet<Location>((int)getVolume());
    private int _crumbleDelayTicks = 0;
    private int _rebuildDelayTicks = 40;
    private ItemStack[] _affectedBlocks;

    private boolean _hasRestored;

    public ReCrumbleFloorRegion(String name) {
        super(name);
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason) {
        // do nothing
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        return false;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);

        if (canRestore()) {

            // restore in case server was shut down before the
            // re-crumble region finished rebuilding itself.
            if (!_hasRestored) {
                try {
                    restoreData(BuildMethod.PERFORMANCE);
                    _hasRestored = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {

            // save region data

            Future result;

            try {
                result = this.saveData();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            result.onCancel(new CancelHandler() {
                @Override
                public void run(String reason) {
                    // disable to prevent loss of build
                    setEnabled(false);

                }
            }).onFail(new FailHandler() {
                @Override
                public void run(String reason) {
                    // disable to prevent loss of build
                    setEnabled(false);
                }
            });
        }
    }

    @Override
    protected void onDisable() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _affectedBlocks = dataNode.getItemStacks("affected-blocks");
        _crumbleDelayTicks = dataNode.getInteger("crumble-delay-ticks", _crumbleDelayTicks);
        _rebuildDelayTicks = dataNode.getInteger("rebuild-delay-ticks", _rebuildDelayTicks);
    }

    @Nullable
    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) {
        super.onCoordsChanged(p1, p2);

        if (!canRestore()) {
            try {
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GenericsEventHandler(priority = GenericsEventPriority.LAST)
    private void onArenaEnd(@SuppressWarnings("unused") ArenaEndedEvent event) {

        if (!isEnabled())
            return;

        _dropped.clear();
    }

    @GenericsEventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        if (!isEnabled())
            return;

        Location loc = event.getPlayer().getLocation();

        Block block = loc.getBlock().getRelative(0, -1, 0);
        Location adjusted = block.getLocation();

        if (_dropped.contains(adjusted))
            return;

        if (!contains(adjusted))
            return;

        if (block.getType() == Material.AIR ||
                block.getType() == Material.WATER ||
                block.getType() == Material.LAVA)
            return;

        _dropped.add(adjusted);

        scheduleBlockBreak(block);
    }

    private void scheduleBlockBreak(final Block block) {

        if (_crumbleDelayTicks <= 0) {
            breakBlock(block);
        }
        else {
            Scheduler.runTaskLater(PVStarAPI.getPlugin(), _crumbleDelayTicks, new Runnable() {
                @Override
                public void run() {
                    breakBlock(block);
                }
            });
        }
    }

    private void breakBlock(final Block block) {

        ItemStack[] affected = _affectedBlocks;

        if (affected != null && affected.length > 0) {
            boolean isFound = false;
            for (ItemStack item : affected) {
                if (block.getState().getData().equals(item.getData())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
                return;
        }

        Block below = block.getRelative(0, -1, 0);
        final BlockState blockState = block.getState();

        if (below.getType() == Material.AIR) {
            BlockUtils.dropRemoveBlock(block, 20);
        } else {
            block.setType(Material.AIR);
        }

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), _rebuildDelayTicks, new Runnable() {

            @Override
            public void run() {
                blockState.update(true);
                _dropped.remove(block.getLocation());
            }
        });
    }
}
