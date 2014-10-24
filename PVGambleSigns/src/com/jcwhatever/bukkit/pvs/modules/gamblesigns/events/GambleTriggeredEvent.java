package com.jcwhatever.bukkit.pvs.modules.gamblesigns.events;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.players.AbstractPlayerEvent;

public class GambleTriggeredEvent extends AbstractPlayerEvent {

    private final String _eventName;
    private final SignContainer _signContainer;

    public GambleTriggeredEvent(Arena arena, ArenaPlayer player, String eventName, SignContainer sign) {
        super(arena, player, false);

        PreCon.notNullOrEmpty(eventName);
        PreCon.notNull(sign);

        _eventName = eventName;
        _signContainer = sign;
    }

    public String getEventName() {
        return _eventName;
    }

    public SignContainer getSignContainer() {
        return _signContainer;
    }
}
