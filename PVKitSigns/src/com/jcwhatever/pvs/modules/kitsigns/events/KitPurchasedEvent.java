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

package com.jcwhatever.pvs.modules.kitsigns.events;

import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.players.AbstractPlayerEvent;
import org.bukkit.event.Cancellable;

/**
 * Called whenever a player purchases a kit
 */
public class KitPurchasedEvent extends AbstractPlayerEvent implements Cancellable {

    private final IArenaPlayer _arenaPlayer;
    private IKit _kit;
    private double _cost;
    private final CurrencyType _currencyType;
    private boolean _isCancelled;

    public enum CurrencyType {
        ECONOMY,
        EXP,
        ITEM,
        POINTS,
        OTHER
    }

    /**
     * Constructor.
     *
     * @param who  The player purchasing the kit.
     * @param kit  The kit.
     */
    public KitPurchasedEvent(IArenaPlayer who, IKit kit, double cost, CurrencyType currencyType) {
        super(who.getArena(), who, who.getContextManager());

        PreCon.notNull(who);
        PreCon.notNull(kit);

        _kit = kit;
        _cost = cost;
        _currencyType = currencyType;
        _arenaPlayer = who;
    }

    /**
     * Get the arena player.
     */
    public IArenaPlayer getArenaPlayer() {
        return _arenaPlayer;
    }

    /**
     * Get the purchased kit.
     */
    public IKit getKit() {
        return _kit;
    }

    /**
     * Set the purchased kit.
     *
     * @param kit  The kit.
     */
    public void setKit(IKit kit) {
        PreCon.notNull(kit);

        _kit = kit;
    }

    /**
     * Get the cost of the kit.
     */
    public double getCost() {
        return _cost;
    }

    /**
     * Set the cost of the kit.
     *
     * @param cost  The cost.
     */
    public void setCost(double cost) {
        _cost = cost;
    }

    /**
     * Get the currency type of the cost.
     */
    public CurrencyType getCurrencyType() {
        return _currencyType;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }
}
