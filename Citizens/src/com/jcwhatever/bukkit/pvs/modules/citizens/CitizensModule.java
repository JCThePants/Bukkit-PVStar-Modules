package com.jcwhatever.bukkit.pvs.modules.citizens;

import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.citizens.commands.NpcCommand;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.AbstractNPCEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.EntityTargetNPCEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCClickEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustByBlockEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustByEntityEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDamageEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDeathEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDespawnEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCLeftClickEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationBeginEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationCancelEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationCompleteEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationReplaceEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCRightClickEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCSpawnEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.CitizensScriptApi;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import net.citizensnpcs.Citizens;
import sun.org.mozilla.javascript.internal.ContextFactory;

public class CitizensModule extends PVStarModule {

    private static CitizensModule _instance;
    private KitManager _kitManager;
    private CitizensScriptApi _scriptApi;

    public static CitizensModule getInstance() {
        return _instance;
    }

    public CitizensModule() {
        super();

        _instance = this;
    }

    public CitizensScriptApi getScriptApi() {
        return _scriptApi;
    }

    public KitManager getKitManager() {
        return _kitManager;
    }

    @Override
    protected void onRegisterTypes() {

        _scriptApi = new CitizensScriptApi();

        PVStarAPI.getScriptManager().registerApiType(_scriptApi);

        registerEvent(EntityTargetNPCEvent.class);
        registerEvent(NPCClickEvent.class);
        registerEvent(NPCCombustByBlockEvent.class);
        registerEvent(NPCCombustByEntityEvent.class);
        registerEvent(NPCCombustEvent.class);
        registerEvent(NPCDamageEvent.class);
        registerEvent(NPCDeathEvent.class);
        registerEvent(NPCDespawnEvent.class);
        registerEvent(NPCLeftClickEvent.class);
        registerEvent(NPCRightClickEvent.class);
        registerEvent(NPCSpawnEvent.class);

        registerEvent(NPCNavigationBeginEvent.class);
        registerEvent(NPCNavigationCancelEvent.class);
        registerEvent(NPCNavigationCompleteEvent.class);
        registerEvent(NPCNavigationReplaceEvent.class);
    }

    @Override
    protected void onEnable() {

        _kitManager = new KitManager(PVStarAPI.getPlugin(), getDataNode("kits"));
        PVStarAPI.getCommandHandler().registerCommand(NpcCommand.class);

        ContextFactory factory = ContextFactory.getGlobal();
        ClassLoader loader = Citizens.class.getClassLoader();
        factory.initApplicationClassLoader(loader);
    }

    private void registerEvent(Class<? extends AbstractNPCEvent> eventClass) {

        PVStarAPI.getScriptManager().registerEventType(this, eventClass);
        ScriptNPC.registerNPCEvent(eventClass);
    }
}
