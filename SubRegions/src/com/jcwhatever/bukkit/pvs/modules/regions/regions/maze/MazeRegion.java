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
        ;
    }

    protected MazeBuilder _builder;

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {

        if (!isEnabled())
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
        _builder = new MazeBuilder(PVStarAPI.getPlugin(), this, getDataNode());
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }
}
