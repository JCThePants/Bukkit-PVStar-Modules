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

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;
import com.jcwhatever.nucleus.utils.performance.queued.QueueWorker;
import com.jcwhatever.nucleus.utils.performance.queued.TaskConcurrency;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;

import java.util.Map;

@RegionTypeInfo(
        name="maze",
        description="Randomly generated maze region."
)
public class MazeRegion extends AbstractPVRegion implements IEventListener {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("wall-materials", PropertyValueType.ITEM_STACK_ARRAY,
                        "Set the materials used to construct the maze walls.")

                .set("block-size", PropertyValueType.INTEGER,
                        "Set the size of the maze blocks. Determines size of passageways.")

                .set("run-on-arena-end", PropertyValueType.BOOLEAN, true,
                        "Set true to rebuild maze when the owning arena ends.")

                .buildDefinitions()
        ;
    }

    private MazeBuilder _builder;
    private boolean _runOnArenaEnd = true;

    public MazeRegion(String name) {
        super(name);
    }

    @EventMethod
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
    protected void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason) {
        // do nothing
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
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
    protected Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }
}
