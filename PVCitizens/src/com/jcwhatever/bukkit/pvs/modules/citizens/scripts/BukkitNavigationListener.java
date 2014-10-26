/* This file is part of PV-Star Modules: PVCitizens for Bukkit, licensed under the MIT License (MIT).
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
