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


package com.jcwhatever.pvs.modules.regions.regions;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.nucleus.regions.BuildMethod;
import com.jcwhatever.nucleus.regions.MultiSnapshotRegion;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.SettingsManager;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;
import com.jcwhatever.nucleus.utils.performance.queued.QueueProject;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractPVRegion extends MultiSnapshotRegion {

    private static final boolean DEFAULT_ENABLE = true;

    private boolean _isEnabled = false;
    private boolean _isInitialized = false;

    private Arena _arena;
    private RegionTypeInfo _typeInfo;
    private SettingsManager _settingsManager;
    private SubRegionsModule _module;
    private IDataNode _dataNode;
    private IDataNode _extraNode;

    private List<RegionEventHandler> _onEnter;
    private List<RegionEventHandler> _onLeave;

    public AbstractPVRegion(String name) {
        super(PVStarAPI.getPlugin(), name);
    }

    public void init(RegionTypeInfo typeInfo, Arena arena, IDataNode dataNode, SubRegionsModule module) {
        PreCon.notNull(typeInfo);
        PreCon.notNull(arena);
        PreCon.notNull(dataNode);
        PreCon.notNull(module);

        if (_isInitialized)
            throw new RuntimeException("Region can only be initialized once.");

        _isInitialized = true;

        loadSettings(dataNode);

        _typeInfo = typeInfo;
        _arena = arena;
        _module = module;
        _dataNode = dataNode;
        _extraNode = dataNode.getNode("extra");

        //noinspection ConstantConditions
        _settingsManager = new SettingsManager(_extraNode, getDefinitions());

        Runnable onSettingsChanged = new Runnable() {
            @Override
            public void run() {

                boolean prevEnabled = _isEnabled;

                _isEnabled = getDataNode().getBoolean("enabled", DEFAULT_ENABLE);

                onLoadSettings(_extraNode);

                if (_isEnabled && !prevEnabled)
                    onEnable();
            }
        };

        _settingsManager.onSettingsChanged(onSettingsChanged);
        onSettingsChanged.run();

        onInit();
    }

    @Override
    @Nonnull
    public IDataNode getDataNode() {
        return _dataNode;
    }

    public final Arena getArena() {
        return _arena;
    }

    public final SubRegionsModule getModule() {
        return _module;
    }

    public final String getTypeName() {
        return _typeInfo.name();
    }

    public final String getTypeDescription() {
        return _typeInfo.description();
    }

    public final boolean isEnabled() {
        return _isEnabled;
    }

    public final void setEnabled(boolean isEnabled) {
        if (_isEnabled == isEnabled)
            return;

        _isEnabled = isEnabled;

        if (isEnabled)
            onEnable();
        else
            onDisable();

        //noinspection ConstantConditions
        getDataNode().set("enabled", isEnabled);
        getDataNode().save();
    }

    public final SettingsManager getSettingsManager() {
        return _settingsManager;
    }

    public final Future<QueueTask> restoreData(BuildMethod buildMethod, boolean forceRestore) throws IOException {

        if (!forceRestore &&
                _arena.getRegion().isRestoring()) {

            QueueProject cancelledProject = new QueueProject(getPlugin());

            return cancelledProject.cancel("Restore cancelled to prevent redundancy.");
        }

        return restoreData(buildMethod);
    }

    public final boolean trigger() {
        return onTrigger();
    }

    public final boolean untrigger() {
        return onUntrigger();
    }

    public void addEnterEventHandler(RegionEventHandler handler) {
        if (_onEnter == null)
            _onEnter = new ArrayList<>(25);

        _onEnter.add(handler);

        setEventListener(true);
    }

    public void removeEnterEventHandler(RegionEventHandler handler) {
        if (_onEnter == null)
            return;

        _onEnter.remove(handler);
    }

    public void addLeaveEventHandler(RegionEventHandler handler) {
        if (_onLeave == null)
            _onLeave = new ArrayList<>(25);

        _onLeave.add(handler);

        setEventListener(true);
    }

    public void removeLeaveEventHandler(RegionEventHandler handler) {
        if (_onLeave == null)
            return;

        _onLeave.remove(handler);
    }

    public void clearEventHandlers() {
        if (_onEnter != null)
            _onEnter.clear();

        if (_onLeave != null)
            _onLeave.clear();
    }

    @Override
    public final String toString() {
        return getName();
    }

    @Override
    protected String getFilePrefix() {
        return "subregion." + getName() + '.' + getTypeName();
    }

    @Override
    protected final void onSave() {
        getArena().setBusy();
    }

    @Override
    protected final void onSaveComplete() {
        getArena().setIdle();
    }

    @Override
    protected final void onRestore() {
        getArena().setBusy();
    }

    @Override
    protected final void onRestoreComplete() {
        getArena().setIdle();
    }

    @Override
    protected final void onPlayerEnter(Player p, EnterRegionReason reason) {
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        if (!getArena().equals(player.getArena()))
            return;

        onPlayerEnter(player, reason);

        if (_onEnter != null) {
            for (RegionEventHandler handler : _onEnter) {
                handler.onCall(player);
            }
        }
    }

    @Override
    protected final void onPlayerLeave(Player p, LeaveRegionReason reason) {
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        if (!getArena().equals(player.getArena()))
            return;

        onPlayerLeave(player, reason);

        if (_onLeave != null) {
            for (RegionEventHandler handler : _onLeave) {
                handler.onCall(player);
            }
        }
    }

    @Override
    protected final void setEventListener(boolean isEventListener) {
        super.setEventListener(isEventListener ||
                (_onEnter != null && _onEnter.size() > 0) ||
                (_onLeave != null && _onLeave.size() > 0));
    }

    protected void onInit() {}

    @Override
    protected void onDispose() {

        if (_isEnabled)
            onDisable();
    }


    protected abstract void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason);
    protected abstract void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason);
    protected abstract boolean onTrigger();
    protected abstract boolean onUntrigger();
    protected abstract void onEnable();
    protected abstract void onDisable();
    protected abstract void onLoadSettings(IDataNode dataNode);

    @Nullable
    protected abstract Map<String, PropertyDefinition> getDefinitions();

    public static interface RegionEventHandler {

        public void onCall(ArenaPlayer player);
    }

}
