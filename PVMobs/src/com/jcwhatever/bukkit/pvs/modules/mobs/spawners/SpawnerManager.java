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
