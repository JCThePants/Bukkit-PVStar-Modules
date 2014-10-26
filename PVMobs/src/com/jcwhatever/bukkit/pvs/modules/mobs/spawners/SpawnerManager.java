/* This file is part of PV-Star Modules: PVMobs for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.mobs.spawners;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.proximity.ProximitySpawner;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpawnerManager {

    private SpawnerManager() {}

    private final static Map<String, Class<? extends ISpawner>> _spawners = new HashMap<>(10);

    static {
        register(ProximitySpawner.class);
    }

    public static void register(Class<? extends ISpawner> spawnerClass) {
        PreCon.notNull(spawnerClass);

        SpawnerInfo info = spawnerClass.getAnnotation(SpawnerInfo.class);
        if (info == null)
            throw new RuntimeException("Tried to register spawner without SpawnerInfo annotation: " + spawnerClass.getName());

        _spawners.put(info.name().toLowerCase(), spawnerClass);
    }

    @Nullable
    public static Class<? extends ISpawner> getSpawnerClass(String spawnerName) {
        PreCon.notNullOrEmpty(spawnerName);

        return _spawners.get(spawnerName.toLowerCase());
    }

    public static List<Class<? extends ISpawner>> getSpawnerClasses() {
        return new ArrayList<>(_spawners.values());
    }

}
