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


package com.jcwhatever.bukkit.pvs.modules.citizens;

import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.citizens.commands.NpcCommand;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.CitizensScriptApi;

import net.citizensnpcs.Citizens;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CitizensModule extends PVStarModule {

    private static CitizensModule _module;
    private KitManager _kitManager;
    private CitizensScriptApi _scriptApi;

    public static CitizensModule getModule() {
        return _module;
    }

    public CitizensModule() {
        super();

        _module = this;
    }

    public CitizensScriptApi getScriptApi() {
        return _scriptApi;
    }

    public KitManager getKitManager() {
        return _kitManager;
    }

    @Override
    protected void onRegisterTypes() {

        _scriptApi = new CitizensScriptApi();

        PVStarAPI.getScriptManager().registerApiType(_scriptApi);
    }

    @Override
    protected void onEnable() {

        _kitManager = new KitManager(PVStarAPI.getPlugin(), getDataNode("kits"));
        PVStarAPI.getCommandHandler().registerCommand(NpcCommand.class);

        try {
            initClassLoader();
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*
     * Init Citizens class loader into global factory.
     * Reflection used to prevent compile issues with "internal" package.
     *
     * Equivalent code:
     *
     * ContextFactory factory = ContextFactory.getGlobal();
     * ClassLoader loader = Citizens.class.getClassLoader();
     * factory.initApplicationClassLoader(loader);
     */
    private void initClassLoader() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> contextFactoryClass = Class.forName("sun.org.mozilla.javascript.internal.ContextFactory");

        Method getGlobal = contextFactoryClass.getDeclaredMethod("getGlobal");
        getGlobal.setAccessible(true);

        ClassLoader citizensLoader = Citizens.class.getClassLoader();

        Method initApplicationClassLoader = contextFactoryClass.getDeclaredMethod(
                "initApplicationClassLoader", ClassLoader.class);
        initApplicationClassLoader.setAccessible(true);

        Object contextFactory = getGlobal.invoke(null);

        initApplicationClassLoader.invoke(contextFactory, citizensLoader);
    }
}
