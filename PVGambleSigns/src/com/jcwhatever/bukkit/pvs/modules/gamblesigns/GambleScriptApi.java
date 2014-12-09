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


package com.jcwhatever.bukkit.pvs.modules.gamblesigns;

import com.jcwhatever.bukkit.generic.collections.HashSetMap;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.GenericsScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.gamblesigns.events.GambleTriggeredEvent;

import org.bukkit.plugin.Plugin;

import java.util.Set;

@ScriptApiInfo(
        variableName = "pvGamble",
        description = "Provide scripts with api access to PV-Star PVGambleSigns module.")
public class GambleScriptApi extends GenericsScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public GambleScriptApi(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject, IGenericsEventListener {

        private HashSetMap<String, GambleHandler> _gambleHandlers = new HashSetMap<>(25);
        private boolean _isDisposed;

        ApiObject() {
            PVStarAPI.getEventManager().register(this);
        }

        public void addWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.put(eventName, handler);
        }

        public void removeWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.removeValue(eventName, handler);
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

        @GenericsEventHandler
        private void onGambleTriggered(GambleTriggeredEvent event) {
            String eventName = event.getEventName();

            Set<GambleHandler> handlers = _gambleHandlers.getAll(eventName);
            if (handlers == null)
                return;

            for (GambleHandler handler : handlers) {
                handler.onCall(event.getArena(), event.getPlayer(), eventName, event.getSignContainer());
            }
        }
    }

    public static interface GambleHandler {

        public void onCall(Arena arena, ArenaPlayer signClicker, String eventName, SignContainer sign);

    }
}
