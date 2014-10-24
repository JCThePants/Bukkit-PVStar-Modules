package com.jcwhatever.bukkit.pvs.modules.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.ArenaDisposeEvent;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.RegionsCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.scripting.RegionScriptApi;

import java.util.HashMap;
import java.util.Map;

public class SubRegionsModule extends PVStarModule implements GenericsEventListener {

    private static SubRegionsModule _instance;

    public static SubRegionsModule getInstance() {
        return _instance;
    }

    private RegionScriptApi _scriptApi;
    private TypesManager _typesManager = new TypesManager();
    private Map<Arena, RegionManager> _regionManagers = new HashMap<>(30);

    public SubRegionsModule () {
        super();

        _instance = this;
    }

    @Override
    protected void onRegisterTypes() {

        if (_scriptApi != null)
            _scriptApi.reset();

        _scriptApi = new RegionScriptApi(this);

        PVStarAPI.getScriptManager().registerApiType(_scriptApi);
    }

    public TypesManager getTypesManager() {
        return _typesManager;
    }

    public RegionManager getManager(Arena arena) {
        RegionManager manager = _regionManagers.get(arena);
        if (manager == null) {
            manager = new RegionManager(arena, this);
            _regionManagers.put(arena, manager);
        }
        return manager;
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getEventManager().register(this);
        PVStarAPI.getCommandHandler().registerCommand(RegionsCommand.class);

    }

    @GenericsEventHandler
    private void onArenaDispose(ArenaDisposeEvent event) {

        Arena arena = event.getArena();
        RegionManager manager = _regionManagers.remove(arena);
        if (manager != null) {
            manager.dispose();
        }
    }

}
