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

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;
import com.jcwhatever.bukkit.pvs.modules.gamblesigns.events.GambleTriggeredEvent;

import java.util.List;

public class GambleScriptApi extends ScriptApi implements IGenericsEventListener {

    @Override
    public String getVariableName() {
        return "_gamble";
    }

    @Override
    protected IScriptApiObject onCreateApiObject(Arena arena, EvaluatedScript script) {

        ApiObject apiObject = new ApiObject();

        arena.getEventManager().register(apiObject);

        return apiObject;
    }

    public static class ApiObject implements IScriptApiObject, IGenericsEventListener {

        private MultiValueMap<String, GambleHandler> _gambleHandlers = new MultiValueMap<>(25);

        public void addWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.put(eventName, handler);
        }

        public void removeWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.removeValue(eventName, handler);
        }

        @Override
        public void reset() {
            _gambleHandlers.clear();
        }

        @GenericsEventHandler
        private void onGambleTriggered(GambleTriggeredEvent event) {
            String eventName = event.getEventName();

            List<GambleHandler> handlers = _gambleHandlers.getValues(eventName);
            if (handlers == null)
                return;

            for (GambleHandler handler : handlers) {
                handler.onCall(event.getPlayer(), eventName, event.getSignContainer());
            }
        }
    }

    public static interface GambleHandler {

        public void onCall(ArenaPlayer signClicker, String eventName, SignContainer sign);

    }
}
