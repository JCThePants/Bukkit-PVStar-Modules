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

import com.jcwhatever.nucleus.utils.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.nucleus.utils.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.nucleus.utils.performance.queued.QueueResult.Future;
import com.jcwhatever.nucleus.regions.BuildChunkSnapshot;
import com.jcwhatever.nucleus.regions.BuildMethod;
import com.jcwhatever.nucleus.regions.data.ChunkInfo;
import com.jcwhatever.nucleus.regions.data.RegionChunkSection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegionTypeInfo(
        name="delete",
        description="Deletes blocks in region when triggered by a script. Restores on arena end.")

public class DeleteRegion extends AbstractPVRegion {

    private boolean _isTriggered;

    public DeleteRegion(String name) {
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
        if (!canRestore() || _isTriggered)
            return false;

        _isTriggered = true;

        ItemStack[][][] regionData = this.getBuildArray();
        List<ChunkInfo> chunks = this.getChunks();
        List<BuildChunkSnapshot> snapshots = new ArrayList<>(chunks.size());

        for (ChunkInfo chunk : chunks) {
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
    protected Map<String, PropertyDefinition> getDefinitions() {
        return null;
    }
}
