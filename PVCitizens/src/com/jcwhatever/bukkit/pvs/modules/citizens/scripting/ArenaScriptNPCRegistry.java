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

import com.jcwhatever.bukkit.generic.citizens.npc.AbstractScriptNPCRegistry;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;

import org.bukkit.plugin.Plugin;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

/**
 * Script NPC Registry for an arena.
 */
public class ArenaScriptNPCRegistry extends AbstractScriptNPCRegistry<ArenaScriptNPC> {

    private final Arena _arena;

    public ArenaScriptNPCRegistry(Plugin plugin, NPCRegistry registry, Arena arena) {
        super(plugin, registry, arena.getEventManager());

        _arena = arena;
    }

    public Arena getArena() {
        return _arena;
    }

    @Override
    protected ArenaScriptNPC instantiateScriptNPC(NPC npc) {
        return new ArenaScriptNPC(_arena, this, npc);
    }
}
