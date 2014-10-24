package com.jcwhatever.bukkit.pvs.modules.mobs.spawners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpawnerInfo {

    /**
     * Get the spawner name.
     */
    public String name();

    /**
     * Get the spawner description.
     */
    public String description();
}
