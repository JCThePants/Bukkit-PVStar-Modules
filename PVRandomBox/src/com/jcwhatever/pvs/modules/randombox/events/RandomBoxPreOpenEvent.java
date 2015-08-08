package com.jcwhatever.pvs.modules.randombox.events;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.players.AbstractPlayerEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

/**
 * Called when a player clicks a random box.
 */
public class RandomBoxPreOpenEvent extends AbstractPlayerEvent implements Cancellable {

    private final IArenaPlayer _arenaPlayer;
    private final Block _block;
    private int _expCost;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param who      The player that clicked the box.
     * @param box      The clicked box block.
     * @param expCost  The cost to open the box.
     */
    public RandomBoxPreOpenEvent(IArenaPlayer who, Block box, int expCost) {
        super(who.getArena(), who, who.getContextManager());

        PreCon.notNull(box);

        _arenaPlayer = who;
        _block = box;
        _expCost = expCost;
    }

    /**
     * Get the arena player.
     */
    public IArenaPlayer getArenaPlayer() {
        return _arenaPlayer;
    }

    /**
     * Get the clicked random box block.
     */
    public Block getBlock() {
        return _block;
    }

    /**
     * Get the exp cost.
     */
    public int getExpCost() {
        return _expCost;
    }

    /**
     * Set the exp cost.
     *
     * @param cost  The cost.
     */
    public void setExpCost(int cost) {
        _expCost = cost;
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
