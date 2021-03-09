/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package automatone.api.utils;

import com.google.gson.annotations.SerializedName;

/**
 * Need a non obfed chunkpos that we can load using GSON
 */
public class MyChunkPos {

    @SerializedName("x")
    public int x;

    @SerializedName("z")
    public int z;

    @Override
    public String toString() {
        return x + ", " + z;
    }
}