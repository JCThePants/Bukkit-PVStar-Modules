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


package com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns;

import com.jcwhatever.nucleus.utils.SignUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractColumn {

    private final Leaderboard _leaderboard;
    private final Sign _headerSign;
    protected List<Sign> _signs;


    public AbstractColumn (Leaderboard leaderboard, Sign columnHeader) {
        PreCon.notNull(leaderboard);
        PreCon.notNull(columnHeader);

        _leaderboard = leaderboard;
        _headerSign = columnHeader;

        _signs = new ArrayList<>(10);

        addColumnSigns(columnHeader);
    }

    /**
     * Update column signs using the supplied player stats
     * @param playerIds
     */
    public void update(Collection<String> playerIds) {

        Iterator<String> playerIterator = playerIds.iterator();

        for (Sign archive : _signs) {

            Sign sign = SignUtils.getRecent(archive);

            if (sign == null) {
                Msg.debug("Null sign in leaderboard column");
                continue;
            }

            // add 4 lines to the sign
            for (int i = 0; i < 4; ++i) {

                // add player info
                String msg = playerIterator.hasNext()
                        ? getPlayerStatDisplay(i, playerIterator.next())
                        : "";

                sign.setLine(i, msg);
            }
            sign.update(true);
        }
    }

    /**
     * Clear all signs except the header.
     */
    public void clear() {
        for (Sign sign : getSigns()) {
            sign.setLine(0, "");
            sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");
            sign.update(true);
        }
    }

    /**
     * Get the column header sign.
     */
    public Sign getHeaderSign() {
        return _headerSign;
    }

    /**
     * Get all signs in the column, excluding the header.
     */
    public List<Sign> getSigns() {
        ArrayList<Sign> signs = new ArrayList<Sign>(_signs.size());
        for (Sign archive : _signs) {

            Sign sign = SignUtils.getRecent(archive);
            if (sign == null)
                continue;

            signs.add(sign);
        }
        return signs;
    }

    public Leaderboard getLeaderboard() {
        return _leaderboard;
    }

    public int getColumnHeight() {
        return _signs.size();
    }

    public int getTotalLines() {
        return _signs.size() * 4;
    }

    public abstract double getPlayerStatValue(String playerId);

    /**
     * Get blocks that the column signs are attached to.
     */
    public List<Block> getAttachedBlocks() {
        List<Block> blocks = new ArrayList<>(_signs.size());

        if (getHeaderSign() != null) {
            blocks.add(getHeaderSign().getBlock());
            blocks.add(SignUtils.getAttachedBlock(getHeaderSign()));
        }

        for (Sign archive : _signs) {

            Sign sign = SignUtils.getRecent(archive);
            if (sign == null)
                continue;

            blocks.add(sign.getBlock());
            Block attached = SignUtils.getAttachedBlock(sign);
            if (attached != null)
                blocks.add(attached);
        }
        return blocks;
    }

    protected abstract String getPlayerStatDisplay(int signLine, String playerId);

    // get signs below the column header sign
    protected void addColumnSigns(Sign headerSign) {
        for (Sign sign : SignUtils.getAllAdjacent(headerSign.getBlock(), BlockFace.DOWN)) {
            _signs.add(sign);
        }
    }
}
