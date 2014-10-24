package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import net.citizensnpcs.api.ai.Navigator;

public class NPCNavigationCancelEvent extends AbstractNPCNavigatorEvent {

    public NPCNavigationCancelEvent(ScriptNPC scriptNPC, Navigator navigator) {
        super(scriptNPC, navigator);
    }
}
