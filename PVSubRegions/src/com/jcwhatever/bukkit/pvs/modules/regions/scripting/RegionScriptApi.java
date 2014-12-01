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


package com.jcwhatever.bukkit.pvs.modules.regions.scripting;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion.RegionEventHandler;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class RegionScriptApi extends ScriptApi {

    private final SubRegionsModule _module;

    public RegionScriptApi(SubRegionsModule module) {
        _module = module;
    }

    @Override
    public String getVariableName() {
        return "pvSubRegions";
    }

    @Override
    protected IScriptApiObject onCreateApiObject(Arena arena, EvaluatedScript script) {
        return new ApiObject(_module, script);
    }

    public static class ApiObject implements IScriptApiObject {

        private final EvaluatedScript _script;
        private final SubRegionsModule _module;
        private final MultiValueMap<AbstractPVRegion, RegionEventHandler> _enterHandlers = new MultiValueMap<>(20);
        private final MultiValueMap<AbstractPVRegion, RegionEventHandler> _leaveHandlers = new MultiValueMap<>(20);

        ApiObject (SubRegionsModule module, EvaluatedScript script) {
            _module = module;
            _script = script;
        }

        @Override
        public void dispose() {

            // remove enter region handlers
            Set<AbstractPVRegion> enterRegions = _enterHandlers.keySet();
            for (AbstractPVRegion region : enterRegions) {

                List<RegionEventHandler> handlers = _enterHandlers.getValues(region);
                if (handlers == null)
                    continue;

                for (RegionEventHandler handler : handlers) {
                    region.removeEnterEventHandler(handler);
                }
            }

            // remove leave region handlers
            Set<AbstractPVRegion> leaveRegions = _leaveHandlers.keySet();
            for (AbstractPVRegion region : leaveRegions) {

                List<RegionEventHandler> handlers = _leaveHandlers.getValues(region);
                if (handlers == null)
                    continue;

                for (RegionEventHandler handler : handlers) {
                    region.removeLeaveEventHandler(handler);
                }
            }

            _enterHandlers.clear();
            _leaveHandlers.clear();
        }

        @Nullable
        public AbstractPVRegion getRegion(String regionName) {

            RegionManager manager = _module.getManager(_script.getArena());
            if (manager == null)
                return null;

            return manager.getRegion(regionName);
        }

        public boolean onEnter(String regionName, final RegionEventHandler handler) {
            AbstractPVRegion region = getRegion(regionName);
            if (region == null)
                return false;

            // wrap handler to ensure compatibility with hash maps
            RegionEventHandler wrapper = new RegionEventHandler() {
                @Override
                public void onCall(ArenaPlayer player) {
                    handler.onCall(player);
                }
            };

            _enterHandlers.put(region, wrapper);
            region.addEnterEventHandler(wrapper);
            return true;
        }

        public boolean onLeave(String regionName, final RegionEventHandler handler) {
            AbstractPVRegion region = getRegion(regionName);
            if (region == null)
                return false;

            // wrap handler to ensure compatibility with hash maps
            RegionEventHandler wrapper = new RegionEventHandler() {
                @Override
                public void onCall(ArenaPlayer player) {
                    handler.onCall(player);
                }
            };

            _leaveHandlers.put(region, wrapper);
            region.addLeaveEventHandler(wrapper);
            return true;
        }
    }
}
