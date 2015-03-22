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


package com.jcwhatever.pvs.modules.mobs;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.mobs.commands.MobsCommand;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.AngryWolfSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.BabyZombieSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.BlazeSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.CaveSpiderSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.ChickenJockeySpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.CreeperSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.EnderDragonSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.EndermanSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.GhastSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.HostileMobSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.MagmaCubeSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.SilverFishSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.SkeletonSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.SlimeSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.SpiderJockeySpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.SpiderSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.WitchSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.WitherSkeletonSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.WitherSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.ZombiePigSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.ZombieSpawn;
import com.jcwhatever.pvs.modules.mobs.spawntypes.hostile.ZombieVillagerSpawn;

public class MobsModule extends PVStarModule {

    private static MobsModule _module;

    public static MobsModule getModule() {
        return _module;
    }

    public MobsModule() {
        super();

        _module = this;
    }

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
