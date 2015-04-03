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


package com.jcwhatever.pvs.modules.gamblesigns;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.modules.gamblesigns.events.GambleTriggeredEvent;

import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class GambleScriptApi implements IDisposable, IEventListener {

    private final Plugin _plugin;

    private Multimap<String, GambleHandler> _gambleHandlers =
            MultimapBuilder.hashKeys(25).hashSetValues(10).build();

    private boolean _isDisposed;

    public GambleScriptApi(Plugin plugin) {
        _plugin = plugin;
        PVStarAPI.getEventManager().register(this);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    public void addWinHandler(String eventName, GambleHandler handler) {
        _gambleHandlers.put(eventName, handler);
    }

    public void removeWinHandler(String eventName, GambleHandler handler) {
        _gambleHandlers.remove(eventName, handler);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _gambleHandlers.clear();
        PVStarAPI.getEventManager().unregister(this);
        _isDisposed = true;
    }

    @EventMethod
    private void onGambleTriggered(GambleTriggeredEvent event) {
        String eventName = event.getEventName();

        Collection<GambleHandler> handlers = _gambleHandlers.get(eventName);
        if (handlers == null)
            return;

        for (GambleHandler handler : handlers) {
            handler.onCall(event.getArena(), event.getPlayer(), eventName, event.getSignContainer());
        }
    }


    public static interface GambleHandler {

        public void onCall(Arena arena, ArenaPlayer signClicker, String eventName, ISignContainer sign);
    }
}
