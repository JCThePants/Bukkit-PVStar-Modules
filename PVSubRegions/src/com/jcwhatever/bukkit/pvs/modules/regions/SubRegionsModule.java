/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.regions;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.generic.events.manager.IEventListener;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.ArenaDisposeEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaLoadedEvent;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.RegionsCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.scripting.RegionScriptApi;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SubRegionsModule extends PVStarModule implements IEventListener {

    private static SubRegionsModule _module;

    public static SubRegionsModule getModule() {
        return _module;
    }

    private TypesManager _typesManager = new TypesManager();
    private Map<Arena, RegionManager> _regionManagers = new HashMap<>(30);

    public SubRegionsModule () {
        super();

        _module = this;
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onRegisterTypes() {

        GenericsLib.getScriptApiRepo().registerApiType(PVStarAPI.getPlugin(), RegionScriptApi.class);
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
    private void onArenaLoaded(ArenaLoadedEvent event) {

        // initialize arena manager
        getManager(event.getArena());
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
