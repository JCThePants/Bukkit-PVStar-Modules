package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.points.PointsHandler;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;

public class AbstractPointsHandler implements PointsHandler, GenericsEventListener {

    private final Arena _arena;
    private final PointsType _type;
    private final IDataNode _dataNode;
    private int _points = 1;

    public AbstractPointsHandler(Arena arena, PointsType type, IDataNode node) {
        PreCon.notNull(arena);
        PreCon.notNull(type);
        PreCon.notNull(node);

        _arena = arena;
        _type = type;
        _dataNode = node;
        _points = _dataNode.getInteger("points", _points);
    }

    @Override
    public Arena getArena() {
        return _arena;
    }

    @Override
    public PointsType getPointsType() {
        return _type;
    }

    @Override
    public int getPoints() {
        return _points;
    }

    @Override
    public void setPoints(int points) {
        _points = points;

        _dataNode.set("points", points);
        _dataNode.saveAsync(null);
    }
}
