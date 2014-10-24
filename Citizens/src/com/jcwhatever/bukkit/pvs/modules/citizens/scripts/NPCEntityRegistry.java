package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

public class NPCEntityRegistry {

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
