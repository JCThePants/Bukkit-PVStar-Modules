package com.jcwhatever.pvs.modules.randombox.events;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.views.chest.ChestView;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.players.AbstractPlayerEvent;

/**
 * Called after a random box view is opened.
 */
public class RandomBoxOpenedEvent extends AbstractPlayerEvent {

    private final ChestView _view;

    /**
     * Constructor.
     *
     * @param player  The event player.
     */
    public RandomBoxOpenedEvent(IArenaPlayer player, ChestView view) {
        super(player.getArena(), player, player.getContextManager());

        PreCon.notNull(view);

        _view = view;
    }

    /**
     * Get the random box view.
     */
    public ChestView getView() {
        return _view;
    }
}
