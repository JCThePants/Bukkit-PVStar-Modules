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

import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.pvs.api.stats.IPlayerStats;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.block.Sign;

import java.util.UUID;

/**
 * Leaderboard anchor column.
 */
public class AnchorColumn extends AbstractColumn {

    private final String[] _lineFormats;

    /**
     * Constructor.
     *
     * @param leaderboard  The owning leaderboard.
     * @param anchorSign   The leaderboard anchor sign (top-left)
     * @param lineFormats  Formats to prepend to text lines in the signs.
     */
    public AnchorColumn(Leaderboard leaderboard, Sign anchorSign, String[] lineFormats) {
        super(leaderboard, anchorSign);

        _lineFormats = lineFormats;
    }

    /**
     * Get the anchor sign (top-left)
     */
    public Sign getAnchorSign() {
        return getHeaderSign();
    }

    @Override
    protected String getPlayerStatDisplay(int signLine, IPlayerStats playerStats) {

        UUID playerId = playerStats.getPlayerId();
        String format = _lineFormats[signLine];

        String playerName = PlayerUtils.getPlayerName(playerId);
        if (playerName == null)
            playerName = "?";

        return (format != null ? format : "") + playerName;
    }
}
