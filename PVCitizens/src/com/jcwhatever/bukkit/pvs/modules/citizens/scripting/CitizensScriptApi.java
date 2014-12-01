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


package com.jcwhatever.bukkit.pvs.modules.citizens.scripting;

import com.jcwhatever.bukkit.generic.citizens.CitizensScriptApiObject;
import com.jcwhatever.bukkit.generic.citizens.storage.TransientNPCStore;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.events.IEventHandler;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

/**
 * Provides scripts with api access to citizens npc's.
 */
public class CitizensScriptApi extends ScriptApi {

    @Override
    public String getVariableName() {
        return "pvCitizens";
    }

    @Override
    protected IScriptApiObject onCreateApiObject(Arena arena, EvaluatedScript script) {

        NPCRegistry registry = CitizensAPI.createAnonymousNPCRegistry(new TransientNPCStore());
        ArenaScriptNPCRegistry scriptRegistry = new ArenaScriptNPCRegistry(PVStarAPI.getPlugin(), registry, arena);

        return new ApiObject(arena, scriptRegistry);
    }

    /**
     * Citizens scripting api.
     */
    public static class ApiObject extends CitizensScriptApiObject {

        private final Arena _arena;
        private final ArenaScriptNPCRegistry _npcRegistry;
        private final IEventHandler _arenaEndHandler;

        /**
         * Constructor.
         *
         * @param arena  The owning arena.
         */
        ApiObject(Arena arena, ArenaScriptNPCRegistry registry) {
            super(registry);

            _arena = arena;
            _npcRegistry = registry;

            _arenaEndHandler = new IEventHandler() {
                @Override
                public void call(Object event) {
                    _npcRegistry.deregisterAll();
                }
            };

            _arena.getEventManager().register(
                    ArenaEndedEvent.class, GenericsEventPriority.NORMAL, _arenaEndHandler);
        }

        /**
         * Reset and release resources.
         */
        @Override
        public void dispose() {

            super.dispose();

            _npcRegistry.deregisterAll();

            _arena.getEventManager().unregister(ArenaEndedEvent.class, _arenaEndHandler);
        }
    }
}
