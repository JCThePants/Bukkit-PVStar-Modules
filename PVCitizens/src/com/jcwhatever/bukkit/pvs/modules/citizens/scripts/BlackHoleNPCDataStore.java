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
