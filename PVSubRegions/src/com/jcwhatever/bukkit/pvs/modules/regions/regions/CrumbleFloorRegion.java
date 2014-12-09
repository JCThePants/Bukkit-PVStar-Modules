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
import com.jcwhatever.bukkit.generic.events.manager.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.BlockUtils;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RegionTypeInfo(
        name="crumble",
        description="A region of blocks that are removed moments after being walked on by players.")
public class CrumbleFloorRegion extends AbstractPVRegion implements IGenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("affected-blocks", ValueType.ITEMSTACK,
                        "Set the blocks affected by the region. Null or air affects all blocks.")
                .set("crumble-delay-ticks", 0, ValueType.INTEGER,
                        "The delay time in ticks that a block crumbles after being stepped on.")
        ;
    }

    private int _crumbleDelayTicks = 0;
    private ItemStack[] _affectedBlocks;
    private Set<Location> _dropped;

    public CrumbleFloorRegion(String name) {
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

        if (!canRestore()) {

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
                    setEnabled(false);

                }
            }).onFail(new FailHandler() {
                @Override
                public void run(String reason) {
                    setEnabled(false);
                }
            });
        }
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _crumbleDelayTicks = dataNode.getInteger("crumble-delay-ticks", _crumbleDelayTicks);
        _affectedBlocks = dataNode.getItemStacks("affected-blocks");
        _dropped = new HashSet<Location>((int)getVolume());
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) {
        super.onCoordsChanged(p1, p2);

        if (!this.canRestore()) {
            try {
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GenericsEventHandler(priority = GenericsEventPriority.LAST)
    private void onArenaEnd(ArenaEndedEvent event) {

        if (!isEnabled())
            return;

        try {
            restoreData(BuildMethod.PERFORMANCE, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _dropped.clear();
    }

    @GenericsEventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        if (!isEnabled())
            return;

        Location loc = event.getPlayer().getLocation();

        Location adjusted = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

        if (_dropped.contains(adjusted))
            return;

        if (!contains(adjusted))
            return;

        Block block = adjusted.getBlock();
        if (block.getType() == Material.AIR ||
                block.getType() == Material.WATER ||
                block.getType() == Material.LAVA)
            return;

        _dropped.add(adjusted);

        breakBlock(block);
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

        ArenaScheduler.runTaskLater(getArena(), 5, new Runnable() {

            @Override
            public void run() {

                Block below = block.getLocation().clone().add(0, -1, 0).getBlock();

                if (below.getType() == Material.AIR) {
                    BlockUtils.dropRemoveBlock(block.getLocation(), 20);
                } else {
                    block.setType(Material.AIR);
                }
            }

        });
    }

}
