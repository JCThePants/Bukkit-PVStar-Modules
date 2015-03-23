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

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.doorsigns.signs.EconDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.ExpDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.ItemDoorSignHandler;
import com.jcwhatever.pvs.modules.doorsigns.signs.PointsDoorSignHandler;

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
        PVStarAPI.getSignManager().registerSignType(new ItemDoorSignHandler());
        PVStarAPI.getSignManager().registerSignType(new PointsDoorSignHandler());
        PVStarAPI.getSignManager().registerSignType(new ExpDoorSignHandler());
        PVStarAPI.getSignManager().registerSignType(new EconDoorSignHandler());
    }

    @Override
    protected void onEnable() {
        // do nothing
    }

}
