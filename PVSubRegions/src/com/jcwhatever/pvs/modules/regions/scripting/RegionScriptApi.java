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


package com.jcwhatever.pvs.modules.regions.scripting;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.modules.regions.RegionManager;
import com.jcwhatever.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.pvs.modules.regions.regions.AbstractPVRegion.RegionEventHandler;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

public class RegionScriptApi implements IDisposable {

    private final SubRegionsModule _module = SubRegionsModule.getModule();

    private final Multimap<AbstractPVRegion, RegionEventHandler> _enterHandlers =
            MultimapBuilder.hashKeys(20).hashSetValues(15).build();

    private final Multimap<AbstractPVRegion, RegionEventHandler> _leaveHandlers =
            MultimapBuilder.hashKeys(20).hashSetValues(15).build();

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        // remove enter region handlers
        Set<AbstractPVRegion> enterRegions = _enterHandlers.keySet();
        for (AbstractPVRegion region : enterRegions) {

            Collection<RegionEventHandler> handlers = _enterHandlers.get(region);
            if (handlers == null)
                continue;

            for (RegionEventHandler handler : handlers) {
                region.removeEnterEventHandler(handler);
            }
        }

        // remove leave region handlers
        Set<AbstractPVRegion> leaveRegions = _leaveHandlers.keySet();
        for (AbstractPVRegion region : leaveRegions) {

            Collection<RegionEventHandler> handlers = _leaveHandlers.get(region);
            if (handlers == null)
                continue;

            for (RegionEventHandler handler : handlers) {
                region.removeLeaveEventHandler(handler);
            }
        }

        _enterHandlers.clear();
        _leaveHandlers.clear();

        _isDisposed = true;
    }

    @Nullable
    public AbstractPVRegion getRegion(IArena arena, String regionName) {
        PreCon.notNull(arena);
        PreCon.notNullOrEmpty(regionName);

        RegionManager manager = _module.getManager(arena);
        if (manager == null)
            return null;

        return manager.getRegion(regionName);
    }

    public boolean onEnter(IArena arena, String regionName, final RegionEventHandler handler) {
        PreCon.notNull(arena);
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNull(handler);

        AbstractPVRegion region = getRegion(arena, regionName);
        if (region == null)
            return false;

        // wrap handler to ensure compatibility with hash maps
        RegionEventHandler wrapper = new RegionEventHandler() {
            @Override
            public void onCall(IArenaPlayer player) {
                handler.onCall(player);
            }
        };

        _enterHandlers.put(region, wrapper);
        region.addEnterEventHandler(wrapper);
        return true;
    }

    public boolean onLeave(IArena arena, String regionName, final RegionEventHandler handler) {
        PreCon.notNull(arena);
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNull(handler);

        AbstractPVRegion region = getRegion(arena, regionName);
        if (region == null)
            return false;

        // wrap handler to ensure compatibility with hash maps
        RegionEventHandler wrapper = new RegionEventHandler() {
            @Override
            public void onCall(IArenaPlayer player) {
                handler.onCall(player);
            }
        };

        _leaveHandlers.put(region, wrapper);
        region.addLeaveEventHandler(wrapper);
        return true;
    }
}
