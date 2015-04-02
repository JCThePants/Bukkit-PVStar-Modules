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


package com.jcwhatever.pvs.modules.notesigns;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.notesigns.commands.NotesCommand;
import com.jcwhatever.pvs.modules.notesigns.signs.NoteSignHandler;

public class NoteSignsModule extends PVStarModule {

    private static NoteSignsModule _module;
    private NoteSignHandler _handler;

    public static NoteSignsModule getModule() {
        return _module;
    }

    public NoteSignsModule() {
        _module = this;
    }

    public NoteSignHandler getNoteHandler() {
        return _handler;
    }

    @Override
    protected void onRegisterTypes() {

        _handler = new NoteSignHandler();

        if (Nucleus.getSignManager().registerHandler(_handler)) {

            PVStarAPI.getCommandHandler().registerCommand(NotesCommand.class);
        }
    }

    @Override
    protected void onEnable() {

    }
}
