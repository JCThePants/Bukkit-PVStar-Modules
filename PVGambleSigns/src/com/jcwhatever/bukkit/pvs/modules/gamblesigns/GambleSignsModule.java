package com.jcwhatever.bukkit.pvs.modules.gamblesigns;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;

public class GambleSignsModule extends PVStarModule {

    private static GambleSignsModule _instance;

    public static GambleSignsModule getInstance() {
        return _instance;
    }

    private GambleScriptApi _scriptApi;

    public GambleSignsModule() {
        super();

        _instance = this;
    }

    @Override
    protected void onRegisterTypes() {

        _scriptApi = new GambleScriptApi();

        PVStarAPI.getScriptManager().registerApiType(_scriptApi);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getEventManager().register(_scriptApi);

    }

}
