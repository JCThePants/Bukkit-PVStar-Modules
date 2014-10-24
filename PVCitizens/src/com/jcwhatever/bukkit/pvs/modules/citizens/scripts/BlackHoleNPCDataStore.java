package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

/**
 * NPC Data store that does not save NPC's
 */
public class BlackHoleNPCDataStore implements NPCDataStore {

    int _currentId = 0;

    @Override
    public void clearData(NPC npc) {
        // do nothing
    }

    @Override
    public int createUniqueNPCId(NPCRegistry npcs) {

        // ensure an infinite supply of temporary id's
        if (_currentId == Integer.MAX_VALUE)
            _currentId = 0;

        _currentId++;
        return _currentId;
    }

    @Override
    public void loadInto(NPCRegistry npcs) {
        // do nothing
    }

    @Override
    public void saveToDisk() {
        // do nothing
    }

    @Override
    public void saveToDiskImmediate() {
        // do nothing
    }

    @Override
    public void store(NPC npc) {
        // do nothing
    }

    @Override
    public void storeAll(NPCRegistry npcs) {
        // do nothing
    }
}
