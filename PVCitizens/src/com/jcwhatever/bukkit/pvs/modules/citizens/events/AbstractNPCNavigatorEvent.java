package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import net.citizensnpcs.api.ai.Navigator;

public class AbstractNPCNavigatorEvent extends AbstractNPCEvent {

    private final Navigator _navigator;

    public AbstractNPCNavigatorEvent(ScriptNPC scriptNPC, Navigator navigator) {
        super(scriptNPC.getArena(), scriptNPC, false);

        PreCon.notNull(navigator);

        _navigator = navigator;
    }

    public Navigator getNavigator() {
        return _navigator;
    }
}
