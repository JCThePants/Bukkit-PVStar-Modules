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

package com.jcwhatever.bukkit.pvs.modules.citizens.traits;

import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.trait.Saddle;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * Trait that puts npc rider on a horse
 */
public class HorseRiderTrait extends Trait {

    private WeakReference<NPC> _horseReference;
    private Horse.Color _color = Color.WHITE;
    private Horse.Style _style = Style.NONE;
    private boolean _isCarryingChest = false;

    protected HorseRiderTrait() {
        super("HorseRider");
    }

    @Nullable
    public NPC getHorseNPC() {
        if (_horseReference == null)
            return null;

        return _horseReference.get();
    }

    public Color getHorseColor() {
        if (_horseReference != null) {
            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {
                Horse horse = (Horse)horseNPC.getEntity();
                _color = horse.getColor();
            }
        }
        return _color;
    }

    public void setHorseColor(Color color) {
        _color = color;

        if (_horseReference != null) {

            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {

                Horse horse = (Horse)horseNPC.getEntity();

                horse.setColor(color);
            }
        }
    }

    public Style getHorseStyle() {
        if (_horseReference != null) {
            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {
                Horse horse = (Horse)horseNPC.getEntity();
                _style = horse.getStyle();
            }
        }
        return _style;
    }

    public void setHorseStyle(Style style) {
        _style = style;

        if (_horseReference != null) {

            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {

                Horse horse = (Horse)horseNPC.getEntity();

                horse.setStyle(style);
            }
        }
    }

    public boolean isCarryingChest() {
        if (_horseReference != null) {
            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {
                Horse horse = (Horse)horseNPC.getEntity();
                _isCarryingChest = horse.isCarryingChest();
            }
        }
        return _isCarryingChest;
    }

    public void setCarryingChest(boolean isCarryingChest) {
        _isCarryingChest = isCarryingChest;

        if (_horseReference != null) {

            NPC horseNPC = _horseReference.get();
            if (horseNPC != null) {

                Horse horse = (Horse)horseNPC.getEntity();

                horse.setCarryingChest(isCarryingChest);
            }
        }
    }

    @Override
    public void onAttach() {
        mount();
    }

    @Override
    public void onDespawn() {
        dismount();
    }

    @Override
    public void onRemove() {
        dismount();
    }

    @Override
    public void onSpawn() {
        mount();
    }

    private void mount() {
        NPC npc = getNPC();

        if (!npc.isSpawned())
            return;

        if (_horseReference != null && _horseReference.get() != null)
            return;

        final NPC horseNPC = npc.getOwningRegistry().createNPC(EntityType.HORSE, npc.getName() + "_Horse");

        horseNPC.getTrait(MobType.class).setType(org.bukkit.entity.EntityType.HORSE);
        horseNPC.getTrait(Saddle.class).toggle();

        _horseReference = new WeakReference<NPC>(horseNPC);

        horseNPC.spawn(npc.getEntity().getLocation());

        Horse horse = ((Horse)horseNPC.getEntity());
        horse.setDomestication(horse.getMaxDomestication());
        horse.setColor(_color);
        horse.setStyle(_style);
        horse.setCarryingChest(_isCarryingChest);

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), new Runnable() {
            @Override
            public void run() {
                horseNPC.getEntity().setPassenger(getNPC().getEntity());
            }
        });
    }

    private void dismount() {

        if (_horseReference == null)
            return;

        NPC horseNPC = _horseReference.get();
        if (horseNPC == null)
            return;

        horseNPC.destroy();
        _horseReference = null;
    }

}
