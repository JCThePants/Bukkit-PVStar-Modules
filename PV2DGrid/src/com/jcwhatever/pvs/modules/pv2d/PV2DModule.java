package com.jcwhatever.pvs.modules.pv2d;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.managed.scripting.IScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi.IApiObjectCreator;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;

import org.bukkit.plugin.Plugin;

/**
 * 2D region tiles module.
 */
public class PV2DModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        IScriptApi scriptApi = new SimpleScriptApi(PVStarAPI.getPlugin(), "pv2DGrid", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new ScriptApi();
            }
        });

        Nucleus.getScriptApiRepo().registerApi(scriptApi);
    }

    @Override
    protected void onEnable() {
        PVStarAPI.getExtensionManager().registerType(PV2DExtension.class);
    }
}
