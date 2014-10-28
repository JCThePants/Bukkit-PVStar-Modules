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

public class CitizensModule extends PVStarModule {

    private static CitizensModule _module;
    private KitManager _kitManager;
    private CitizensScriptApi _scriptApi;

    public static CitizensModule getModule() {
        return _module;
    }

    public CitizensModule() {
        super();

        _module = this;
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

        /** TODO: Use reflection
        ContextFactory factory = ContextFactory.getGlobal();
        ClassLoader loader = Citizens.class.getClassLoader();
        factory.initApplicationClassLoader(loader);
         **/
    }

    private void registerEvent(Class<? extends AbstractNPCEvent> eventClass) {

        PVStarAPI.getScriptManager().registerEventType(this, eventClass);
        ScriptNPC.registerNPCEvent(eventClass);
    }
}
