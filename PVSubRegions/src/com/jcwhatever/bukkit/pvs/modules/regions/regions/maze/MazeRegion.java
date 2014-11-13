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


package com.jcwhatever.bukkit.pvs.modules.regions.regions.maze;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.performance.queued.QueueTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueWorker;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;

@RegionTypeInfo(
        name="maze",
        description="Randomly generated maze region."
)
public class MazeRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("wall-materials", ValueType.ITEMSTACK, "Set the materials used to construct the maze walls.")
                .set("block-size", ValueType.INTEGER, "Set the size of the maze blocks. Determines size of passageways.")
                .set("run-on-arena-end", true, ValueType.BOOLEAN, "Set true to rebuild maze when the owning arena ends.")
        ;
    }

    private MazeBuilder _builder;
    private boolean _runOnArenaEnd = true;

    public MazeRegion(String name) {
        super(name);
    }

    @GenericsEventHandler
    private void onArenaEnd(@SuppressWarnings("unused") ArenaEndedEvent event) {

        if (!_runOnArenaEnd || !isEnabled())
            return;


        QueueTask task = new QueueTask(PVStarAPI.getPlugin(), TaskConcurrency.MAIN_THREAD) {

            @Override
            protected void onRun() {
                _builder.render();
                complete();
            }

        };

        QueueWorker.get().addTask(task);
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        // do nothing
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        _builder.render();
        return true;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }


    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _builder = new MazeBuilder(PVStarAPI.getPlugin(), this, dataNode);
        _runOnArenaEnd = dataNode.getBoolean("run-on-arena-end", _runOnArenaEnd);
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }
}
