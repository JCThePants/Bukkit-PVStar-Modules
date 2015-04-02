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


package com.jcwhatever.pvs.modules.kitsigns;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.kitsigns.signs.EconKitSignHandler;
import com.jcwhatever.pvs.modules.kitsigns.signs.ExpKitSignHandler;
import com.jcwhatever.pvs.modules.kitsigns.signs.ItemKitSignHandler;
import com.jcwhatever.pvs.modules.kitsigns.signs.PointsKitSignHandler;

public class KitSignsModule extends PVStarModule {

    private static KitSignsModule _module;

    public static KitSignsModule getModule() {
        return _module;
    }

    public KitSignsModule() {
        super();

        _module = this;
    }

    @Override
    protected void onRegisterTypes() {

        Nucleus.getSignManager().registerHandler(new ExpKitSignHandler());
        Nucleus.getSignManager().registerHandler(new ItemKitSignHandler());
        Nucleus.getSignManager().registerHandler(new PointsKitSignHandler());
        Nucleus.getSignManager().registerHandler(new EconKitSignHandler());
    }

    @Override
    protected void onEnable() {
        // do nothing
    }
}
