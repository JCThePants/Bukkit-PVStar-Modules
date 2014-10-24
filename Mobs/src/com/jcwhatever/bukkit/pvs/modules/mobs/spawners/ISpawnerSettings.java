package com.jcwhatever.bukkit.pvs.modules.mobs.spawners;

import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;

/**
 * Created by John on 10/5/2014.
 */
public interface ISpawnerSettings {

    public SettingDefinitions getDefinitions();

    public ISettingsManager getManager();

}
