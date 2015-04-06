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


package com.jcwhatever.pvs.modules.regions.regions;

import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;

import java.util.Map;

@RegionTypeInfo(
        name="basic",
        description="Region that does nothing. Used for scripts.")

public class BasicRegion extends AbstractPVRegion {

    public BasicRegion(String name) {
        super(name);
    }

    @Override
    protected void onPlayerEnter(IArenaPlayer player, EnterRegionReason reason) {
        // do nothing
    }

    @Override
    protected void onPlayerLeave(IArenaPlayer player, LeaveRegionReason reason) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        return false;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        // do nothing
    }

    @Override
    protected void onDisable() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        // do nothing
    }

    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return null;
    }
}
