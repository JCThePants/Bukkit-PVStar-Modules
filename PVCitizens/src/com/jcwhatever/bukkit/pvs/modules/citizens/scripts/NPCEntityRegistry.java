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

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;

import org.bukkit.entity.Entity;

import net.citizensnpcs.api.npc.NPC;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public class NPCEntityRegistry {

    private NPCEntityRegistry() {}

    private static Map<Entity, RegisteredNPC> _entityMap = new WeakHashMap<>(150);

    public static void registerNPCEntity(Arena arena, Entity entity, NPC npc) {
        PreCon.notNull(arena);
        PreCon.notNull(entity);
        PreCon.notNull(npc);

        RegisteredNPC registered = new RegisteredNPC(arena, npc);

        _entityMap.put(entity, registered);
    }

    public static void unregisterNPCEntity(Entity entity) {
        PreCon.notNull(entity);

        _entityMap.remove(entity);
    }

    @Nullable
    public static RegisteredNPC getNPCInfo(Entity entity) {
         return _entityMap.get(entity);
    }

    public static class RegisteredNPC {
        private Arena _arena;
        private NPC _npc;
        private ScriptNPC _scriptNPC;

        RegisteredNPC(Arena arena, NPC npc) {
            _arena = arena;
            _npc = npc;
            _scriptNPC = ScriptNPC.get(npc);
        }

        public Arena getArena() {
            return _arena;
        }

        public NPC getNPC() {
            return _npc;
        }

        public ScriptNPC getScriptNPC() {
            return _scriptNPC;
        }
    }
}
