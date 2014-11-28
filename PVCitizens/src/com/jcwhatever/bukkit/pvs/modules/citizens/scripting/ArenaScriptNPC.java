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

import com.jcwhatever.bukkit.generic.citizens.npc.ScriptNPC;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.traits.PVStarNPCTrait;

import net.citizensnpcs.api.npc.NPC;

/**
 * Script NPC for an arena.
 */
public class ArenaScriptNPC extends ScriptNPC {

    private final Arena _arena;

    public ArenaScriptNPC(Arena arena, ArenaScriptNPCRegistry registry, NPC npc) {
        super(registry, npc);

        PreCon.notNull(arena);

        _arena = arena;
    }

    public Arena getArena() {
        return _arena;
    }

    /**
     * Add core script NPC traits.
     */
    @Override
    protected void addCoreTraits() {
        super.addCoreTraits();

        NPC npc = getHandle();
        npc.addTrait(new PVStarNPCTrait(this));
    }
}
