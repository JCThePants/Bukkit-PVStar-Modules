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


package com.jcwhatever.pvs.modules.gamblesigns.events;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.signs.ISignContainer;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.events.players.AbstractPlayerEvent;

public class GambleTriggeredEvent extends AbstractPlayerEvent {

    private final String _eventName;
    private final ISignContainer _signContainer;

    public GambleTriggeredEvent(Arena arena, ArenaPlayer player, String eventName, ISignContainer sign) {
        //noinspection ConstantConditions
        super(arena, player, player.getRelatedManager());

        PreCon.notNullOrEmpty(eventName);
        PreCon.notNull(sign);

        _eventName = eventName;
        _signContainer = sign;
    }

    public String getEventName() {
        return _eventName;
    }

    public ISignContainer getSignContainer() {
        return _signContainer;
    }
}
