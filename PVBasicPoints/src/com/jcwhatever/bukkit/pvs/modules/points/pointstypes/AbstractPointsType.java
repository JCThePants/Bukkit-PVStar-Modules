package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.points.PointsHandler;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;

public abstract class AbstractPointsType<T extends AbstractPointsHandler> extends PointsType {


    protected final T getNewHandler(Arena arena, IDataNode node) {
        T handler = onGetNewHandler(arena, node);

        arena.getEventManager().register(handler);

        return handler;
    }

    @Override
    protected void onRemove(Arena arena, PointsHandler handler) {

        arena.getEventManager().unregister((AbstractPointsHandler)handler);
    }

    protected abstract T onGetNewHandler(Arena arena, IDataNode node);

}
