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


package com.jcwhatever.pvs.modules.borders;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.borders.commands.OutOfBoundsSubCommand;
import com.jcwhatever.pvs.modules.borders.commands.OutsidersSubCommand;

public class BordersModule extends PVStarModule{

    private static BordersModule _module;

    public static BordersModule getModule() {
        return _module;
    }

    public BordersModule() {
        super();

        _module = this;
    }

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getExtensionManager().registerType(BordersExtension.class);
    }

    @Override
    protected void onEnable() {

        AbstractCommand command = PVStarAPI.getCommandHandler().getCommand("arena");
        if (command != null) {
            command.registerCommand(OutOfBoundsSubCommand.class);
            command.registerCommand(OutsidersSubCommand.class);
        }
    }
}
