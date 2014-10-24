package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationBeginEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationCancelEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationCompleteEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCNavigationReplaceEvent;
import net.citizensnpcs.api.ai.event.NavigationBeginEvent;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationReplaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BukkitNavigationListener implements Listener {

    @EventHandler
    private void onNavBegin(NavigationBeginEvent event) {

        ScriptNPC scriptNPC = ScriptNPC.get(event.getNPC());
        if (scriptNPC == null)
            return;

        NPCNavigationBeginEvent npcEvent = new NPCNavigationBeginEvent(scriptNPC, event.getNavigator());

        scriptNPC.getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNavCancelEvent(NavigationCancelEvent event) {

        ScriptNPC scriptNPC = ScriptNPC.get(event.getNPC());
        if (scriptNPC == null)
            return;

        NPCNavigationCancelEvent npcEvent = new NPCNavigationCancelEvent(scriptNPC, event.getNavigator());

        scriptNPC.getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNavReplace(NavigationReplaceEvent event) {

        ScriptNPC scriptNPC = ScriptNPC.get(event.getNPC());
        if (scriptNPC == null)
            return;

        NPCNavigationReplaceEvent npcEvent = new NPCNavigationReplaceEvent(scriptNPC, event.getNavigator());

        scriptNPC.getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNavComplete(NavigationCompleteEvent event) {

        ScriptNPC scriptNPC = ScriptNPC.get(event.getNPC());
        if (scriptNPC == null)
            return;

        NPCNavigationCompleteEvent npcEvent = new NPCNavigationCompleteEvent(scriptNPC, event.getNavigator());

        scriptNPC.getEventManager().call(npcEvent);
    }


}
