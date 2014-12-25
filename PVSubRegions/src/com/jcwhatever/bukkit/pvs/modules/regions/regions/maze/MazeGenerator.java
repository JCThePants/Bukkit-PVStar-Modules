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


package com.jcwhatever.bukkit.pvs.modules.regions.regions.maze;

import com.jcwhatever.generic.utils.Rand;

public class MazeGenerator {

    private long iterations = 0;
    private int max = 3;

    public enum Orientation {
        NONE,
        X,
        Z,
        DOORX,
        DOORX_WALLZ,
        DOORZ,
        DOORZ_WALLX,
        BOTH
    }

    public Orientation[][] get2DMaze(int xLength, int zLength, int maxIterations) {
        iterations = 0;
        max = maxIterations;
        Orientation[][] grid = new Orientation[zLength][xLength];

        divide(grid, 0, 0, xLength, zLength, getOrientation(xLength, zLength));

        return grid;
    }

    private Orientation getOrientation(int width, int height) {
        if (width < height)
            return Orientation.X;
        else if (height < width)
            return Orientation.Z;
        else
            return Rand.getInt(2) == 0
                    ? Orientation.X
                    : Orientation.Z;
    }


    /**
     * Begin recursive division
     */
    private void divide(Orientation[][] grid, int x, int z, int xWidth, int zWidth, Orientation orientation) {

        if (orientation == Orientation.X) {
            divideX(grid, x, z, xWidth, zWidth);
        }
        else {
            divideZ(grid, x, z, xWidth, zWidth);
        }
    }

    /**
     * divide grid with an X axis Wall
     */
    private void divideX(Orientation[][] grid, int xStart, int zStart, int xWidth, int zWidth) {
        if (xWidth < 2 || zWidth < 2) return;

        int xWall = randomInt(xWidth - 1) + 1;
        int door = randomInt(zWidth - 1);

        for (int z = zStart; z < zStart + zWidth; z++) {
            if (z != zStart + door)
                setWall(grid, xStart + xWall, z, z == zStart + door ? Orientation.DOORZ : Orientation.Z);
        }

        iterations++;
        if (max > -1 && iterations >= max) return;

        int x = xStart + xWall;
        int xW = xWidth - xWall;

        divideZ(grid, x, zStart, xW, zWidth);

        if (max > -1 && iterations >= max) return;

        divideZ(grid, xStart, zStart, xWidth - (xWidth - xWall), zWidth);
    }

    /**
     * divide grid with a Z axis Wall
     */
    private void divideZ(Orientation[][] grid, int xStart, int zStart, int xWidth, int zWidth) {

        if (xWidth < 2 || zWidth < 2) return;

        int zWall =  randomInt(zWidth - 1) + 1;
        int door = randomInt(xWidth - 1);

        for (int x = xStart; x < xStart + xWidth; x++) {
            setWall(grid, x, zStart + zWall, x == xStart + door ? Orientation.DOORX : Orientation.X);
        }

        iterations++;
        if (max > -1 && iterations >= max) return;

        int z = zStart + zWall;//sto + 1;
        int zW = zWidth - zWall;


        divideX(grid, xStart, z, xWidth, zW);

        if (max > -1 && iterations >= max) return;

        //if (z > 0)
        divideX(grid, xStart, zStart, xWidth, zWidth - (zWidth - zWall)); //(zWidth - zWall)

    }

    /**
     * Set wall orientation in grid
     */
    private void setWall(Orientation[][] grid, int xWall, int zWall, Orientation orientation) {

        Orientation current = grid[zWall][xWall];

        if (current == null) {
            current = Orientation.NONE;
            grid[zWall][xWall] = current;
        }

        switch(current) {
            case DOORX:
                if (orientation == Orientation.Z)
                    grid[zWall][xWall] = Orientation.DOORX_WALLZ;
                break;
            case DOORZ:
                if (orientation == Orientation.X)
                    grid[zWall][xWall] = Orientation.DOORZ_WALLX;
                break;

            case X:
            case Z:
                if (orientation == Orientation.DOORX)
                    grid[zWall][xWall] = Orientation.DOORX_WALLZ;
                else if (orientation != current)
                    grid[zWall][xWall] = Orientation.BOTH;
                break;

            case BOTH:
            case DOORX_WALLZ:
            case DOORZ_WALLX:
                break;

            default:
                grid[zWall][xWall] = orientation;
                break;
        }

    }

    /**
     * Get a random integer
     */
    private int randomInt(int len) {
        if (len == 0)
            return 0;
        return Rand.getInt(len);
    }

}
