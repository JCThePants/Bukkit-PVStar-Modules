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


package com.jcwhatever.bukkit.pvs.modules.citizens.kits;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * Base class for NPC item equipper implementations.
 */
public abstract class Equipper {

    private static final Equipper DEFAULT_EQUIPPER = new DefaultEquipper();
    private static final Map<EntityType, Equipper> _equippers = new EnumMap<>(EntityType.class);


    /**
     * Register an equipper instance.
     *
     * @param type      The entity type the equipper is for.
     * @param equipper  The equipper.
     */
    public static void registerEquipper(EntityType type, Equipper equipper) {
        PreCon.notNull(type);
        PreCon.notNull(equipper);

        _equippers.put(type, equipper);
    }

    /**
     * Get an equipper for the specified entity type.
     *
     * @param type  The entity type.
     */
    public static Equipper getEquipper(EntityType type) {
        PreCon.notNull(type);

        Equipper equipper = _equippers.get(type);
        return equipper == null ? DEFAULT_EQUIPPER : equipper;
    }


    /**
     * Equip an NPC with the specified {@code ItemStack}.
     *
     * @param npc   The npc to equip.
     * @param item  The item to equip the npc with.
     */
    public abstract boolean equip(ScriptNPC npc, ItemStack item);
}
