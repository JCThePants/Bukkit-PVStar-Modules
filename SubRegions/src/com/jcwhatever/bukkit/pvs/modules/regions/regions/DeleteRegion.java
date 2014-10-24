package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildChunkSnapshot;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.regions.RegionChunkSection;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Chunk;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RegionTypeInfo(
        name="delete",
        description="Deletes blocks in region when triggered by a script. Restores on arena end.")

public class DeleteRegion extends AbstractPVRegion {

    private boolean _isTriggered;


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
        if (!canRestore() || _isTriggered)
            return false;

        _isTriggered = true;

        ItemStack[][][] regionData = this.getBuildArray();
        List<Chunk> chunks = this.getChunks();
        List<BuildChunkSnapshot> snapshots = new ArrayList<>(chunks.size());

        for (Chunk chunk : chunks) {
            RegionChunkSection section = new RegionChunkSection(this, chunk);
            BuildChunkSnapshot snapshot = new BuildChunkSnapshot(regionData, section);
            snapshots.add(snapshot);
        }

        this.build(BuildMethod.FAST, snapshots);

        return true;
    }

    @Override
    protected boolean onUntrigger() {
        try {

            restoreData(BuildMethod.FAST).onComplete(new Runnable() {

                @Override
                public void run() {
                    _isTriggered = false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onEnable() {

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
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        // do nothing
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return null;
    }

}
