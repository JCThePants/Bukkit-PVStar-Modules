/* This file is part of PV-Star Modules: PVSubRegions for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.regions.MultiSnapshotRegion;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.SettingsManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPVRegion extends MultiSnapshotRegion {

    private boolean _isEnabled = true;
    private boolean _isInitialized = false;

    private Arena _arena;
    private RegionTypeInfo _typeInfo;
    private SettingsManager _settingsManager;
    private SubRegionsModule _module;

    private List<RegionEventHandler> _onEnter;
    private List<RegionEventHandler> _onLeave;

    public AbstractPVRegion() {
        super(PVStarAPI.getPlugin());
    }

    public void init(String name, RegionTypeInfo typeInfo, Arena arena, IDataNode dataNode, SubRegionsModule module) {
        PreCon.notNull(typeInfo);
        PreCon.notNullOrEmpty(name);

        if (_isInitialized)
            throw new RuntimeException("Region can only be initialized once.");

        _isInitialized = true;

        _typeInfo = typeInfo;
        _name = name;
        _searchName = name.toLowerCase();
        _arena = arena;
        _dataNode = dataNode;
        _module = module;

        _settingsManager = new SettingsManager(_dataNode.getNode("extra"), getSettingDefinitions());
        _settingsManager.addOnSettingsChanged(new Runnable() {
            @Override
            public void run() {
                _isEnabled = _dataNode.getBoolean("enabled", _isEnabled);

                onLoadSettings(_dataNode);

                if (_isEnabled)
                    onEnable();
            }
        }, true);

        onInit();
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

        getDataNode().set("enabled", isEnabled);
        getDataNode().saveAsync(null);
    }

    public final IDataNode getDataNode() {
        return _dataNode;
    }

    @Override
    public final void dispose() {
        super.dispose();

        if (_isEnabled)
            onDisable();

        onDispose();
    }

    public final SettingsManager getSettingsManager() {
        return _settingsManager;
    }

    public final Future restoreData(BuildMethod buildMethod, boolean forceRestore) throws IOException {

        // TODO: Add Restore Event

        /*
        if (!forceRestore &&
                _arena.getSettings().isAutoRestoreEnabled() &&
                _arena.getRegion().canRestore()) {

            QueueProject cancelledProject = new QueueProject(_plugin);

            return cancelledProject.cancel("Restore cancelled to prevent redundancy.");
        }
        */

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

        setIsPlayerWatcher(true);
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

        setIsPlayerWatcher(true);
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
    protected final void onPlayerEnter(Player p) {
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        if (!getArena().equals(player.getArena()))
            return;

        onPlayerEnter(player);

        if (_onEnter != null) {
            for (RegionEventHandler handler : _onEnter) {
                handler.onCall(player);
            }
        }
    }

    @Override
    protected final void onPlayerLeave(Player p) {
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        if (!getArena().equals(player.getArena()))
            return;

        onPlayerLeave(player);

        if (_onEnter != null) {
            for (RegionEventHandler handler : _onLeave) {
                handler.onCall(player);
            }
        }
    }

    @Override
    protected final void setIsPlayerWatcher(boolean isWatcher) {
        super.setIsPlayerWatcher(isWatcher ||
                (_onEnter != null && _onEnter.size() > 0) ||
                (_onLeave != null && _onLeave.size() > 0));
    }

    protected void onInit() {}
    protected void onDispose() {}

    protected abstract void onPlayerEnter(ArenaPlayer player);
    protected abstract void onPlayerLeave(ArenaPlayer player);
    protected abstract boolean onTrigger();
    protected abstract boolean onUntrigger();
    protected abstract void onEnable();
    protected abstract void onDisable();
    protected abstract void onLoadSettings(IDataNode dataNode);
    protected abstract SettingDefinitions getSettingDefinitions();


    public static interface RegionEventHandler {

        public void onCall(ArenaPlayer player);
    }

}
