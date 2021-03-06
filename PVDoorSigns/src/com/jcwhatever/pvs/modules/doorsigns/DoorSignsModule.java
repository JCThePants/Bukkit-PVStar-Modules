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


package com.jcwhatever.pvs.modules.doorsigns;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.doorsigns.signs.EconDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.ExpDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.ItemDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.PointsDoorSignHandler;

import java.util.Collection;

public class DoorSignsModule extends PVStarModule {

    private static DoorSignsModule _instance;

    public static DoorSignsModule getModule() {
        return _instance;
    }

    private DoorManager _doorManager;

    public DoorSignsModule() {
        super();

        _instance = this;
    }

    public DoorManager getDoorManager() {
        return _doorManager;
    }

    @Override
    protected void onRegisterTypes() {

        _doorManager = new DoorManager();
        Nucleus.getSignManager().registerHandler(new ItemDoorSignHandler());
        Nucleus.getSignManager().registerHandler(new PointsDoorSignHandler());
        Nucleus.getSignManager().registerHandler(new ExpDoorSignHandler());
        Nucleus.getSignManager().registerHandler(new EconDoorSignHandler());
    }

    @Override
    protected void onEnable() {
        // do nothing
    }

    @Override
    protected void onArenasLoaded() {
        loadDoors(ItemDoorSignHandler.NAME);
        loadDoors(PointsDoorSignHandler.NAME);
        loadDoors(ExpDoorSignHandler.NAME);
        loadDoors(EconDoorSignHandler.NAME);
    }

    private void loadDoors(String handlerName) {
        SignHandler handler = Nucleus.getSignManager().getSignHandler(handlerName);
        Collection<ISignContainer> signs = Nucleus.getSignManager().getSigns(handlerName);

        for (ISignContainer sign : signs) {
            DoorBlocks blocks = _doorManager.findDoors(handler, sign);
            if (blocks != null)
                blocks.setOpen(false);
        }
    }
}
