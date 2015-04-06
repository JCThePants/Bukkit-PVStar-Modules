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


package com.jcwhatever.pvs.modules.points.pointstypes;

import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.points.IPointsHandler;
import com.jcwhatever.pvs.api.points.PointsType;

import org.bukkit.plugin.Plugin;

public class AbstractPointsHandler implements IPointsHandler, IEventListener {

    private final IArena _arena;
    private final PointsType _type;
    private final IDataNode _dataNode;
    private int _points = 1;

    public AbstractPointsHandler(IArena arena, PointsType type, IDataNode node) {
        PreCon.notNull(arena);
        PreCon.notNull(type);
        PreCon.notNull(node);

        _arena = arena;
        _type = type;
        _dataNode = node;
        _points = _dataNode.getInteger("points", _points);
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public IArena getArena() {
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
        _dataNode.save();
    }
}
