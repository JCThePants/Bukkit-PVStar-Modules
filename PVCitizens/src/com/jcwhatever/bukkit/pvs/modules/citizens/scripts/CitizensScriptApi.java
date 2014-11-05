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


package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import com.jcwhatever.bukkit.generic.events.EventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;
import com.jcwhatever.bukkit.pvs.modules.citizens.CitizensModule;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides scripts with api access to citizens npc's.
 */
public class CitizensScriptApi extends ScriptApi {

    static {

        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), PVStarAPI.getPlugin());
        Bukkit.getPluginManager().registerEvents(new BukkitNavigationListener(), PVStarAPI.getPlugin());
    }

    @Override
    public String getVariableName() {
        return "citizens";
    }

    @Override
    protected IScriptApiObject onCreateApiObject(Arena arena, EvaluatedScript script) {

        return new ApiObject(arena);
    }

    /**
     * Citizens scripting api.
     */
    public static class ApiObject implements IScriptApiObject {

        private final Arena _arena;
        private final NPCDataStore _dataStore;
        private final NPCRegistry _npcRegistry;
        private final EventHandler _arenaEndHandler;
        private final Map<NPC, ScriptNPC> _npcs = new WeakHashMap<>(35);

        /**
         * Constructor.
         *
         * @param arena  The owning arena.
         */
        ApiObject(Arena arena) {

            _arena = arena;
            _dataStore = new BlackHoleNPCDataStore();
            _npcRegistry = CitizensAPI.createAnonymousNPCRegistry(_dataStore);

            _arenaEndHandler = new EventHandler() {
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
        public void reset() {

            for (ScriptNPC npc : _npcs.values())
                npc.dispose();

            _npcs.clear();

            _arena.getEventManager().unregister(ArenaEndedEvent.class, _arenaEndHandler);
        }

        /**
         * Create a new npc and return script npc wrapper.
         *
         * @param name  The name of the npc.
         * @param type  The entity type name.
         */
        public ScriptNPC createNPC(String name, String type) {
            PreCon.notNullOrEmpty(name);
            PreCon.notNullOrEmpty(type);

            type = type.toUpperCase();
            EntityType entityType = EntityType.valueOf(type);

            ScriptNPC npc = new ScriptNPC(_arena, _npcRegistry, name, entityType);

            _npcs.put(npc.getHandle(), npc);

            return npc;
        }

        /**
         * Get an NPC kit by name.
         *
         * @param kitName  The name of the kit
         */
        @Nullable
        public ScriptKit getNPCKit(String kitName) {
            PreCon.notNullOrEmpty(kitName);

            Kit kit = CitizensModule.getModule().getKitManager().getKitByName(kitName);
            if (kit == null)
                return null;

            return new ScriptKit(kit);
        }

    }
}
