package com.jcwhatever.bukkit.pvs.modules.mobs;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.mobs.commands.MobsCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.AngryWolfSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.BabyZombieSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.BlazeSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.CaveSpiderSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.ChickenJockeySpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.CreeperSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.EnderDragonSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.EndermanSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.GhastSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.HostileMobSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.MagmaCubeSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.SilverFishSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.SkeletonSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.SlimeSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.SpiderJockeySpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.SpiderSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.WitchSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.WitherSkeletonSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.WitherSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.ZombiePigSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.ZombieSpawn;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile.ZombieVillagerSpawn;

public class MobsModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getSpawnTypeManager().registerType(new AngryWolfSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new BabyZombieSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new BlazeSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new CaveSpiderSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new ChickenJockeySpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new CreeperSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new EnderDragonSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new EndermanSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new GhastSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new HostileMobSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new MagmaCubeSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new SilverFishSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new SkeletonSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new SlimeSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new SpiderJockeySpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new SpiderSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new WitchSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new WitherSkeletonSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new WitherSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new ZombiePigSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new ZombieSpawn());
        PVStarAPI.getSpawnTypeManager().registerType(new ZombieVillagerSpawn());

        PVStarAPI.getExtensionManager().registerType(MobArenaExtension.class);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getCommandHandler().registerCommand(MobsCommand.class);
    }

}
