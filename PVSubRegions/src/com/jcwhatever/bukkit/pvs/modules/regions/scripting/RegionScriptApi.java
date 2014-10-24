package com.jcwhatever.bukkit.pvs.modules.regions.scripting;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion.RegionEventHandler;

import java.util.List;
import java.util.Set;

public class RegionScriptApi extends ScriptApi {

    private final SubRegionsModule _module;

    public RegionScriptApi(SubRegionsModule module) {
        _module = module;
    }

    @Override
    public String getVariableName() {
        return "_subRegions";
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
        public void reset() {

            Set<AbstractPVRegion> enterRegions = _enterHandlers.keySet();
            for (AbstractPVRegion region : enterRegions) {

                List<RegionEventHandler> handlers = _enterHandlers.getValues(region);
                if (handlers == null)
                    continue;

                for (RegionEventHandler handler : handlers) {
                    region.removeEnterEventHandler(handler);
                }
            }

            Set<AbstractPVRegion> leaveRegions = _enterHandlers.keySet();
            for (AbstractPVRegion region : leaveRegions) {

                List<RegionEventHandler> handlers = _enterHandlers.getValues(region);
                if (handlers == null)
                    continue;

                for (RegionEventHandler handler : handlers) {
                    region.removeLeaveEventHandler(handler);
                }
            }

            _enterHandlers.clear();
            _leaveHandlers.clear();
        }

        public AbstractPVRegion getRegion(String regionName) {

            RegionManager manager = _module.getManager(_script.getArena());
            if (manager == null)
                return null;

            return manager.getRegion(regionName);
        }

        public boolean onEnter(String regionName, RegionEventHandler handler) {
            AbstractPVRegion region = getRegion(regionName);
            if (region == null)
                return false;

            _enterHandlers.put(region, handler);
            region.addEnterEventHandler(handler);
            return true;
        }

        public boolean onLeave(String regionName, RegionEventHandler handler) {
            AbstractPVRegion region = getRegion(regionName);
            if (region == null)
                return false;

            _leaveHandlers.put(region, handler);
            region.addLeaveEventHandler(handler);
            return true;
        }
    }
}
