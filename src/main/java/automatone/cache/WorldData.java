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

package automatone.cache;

import automatone.Baritone;
import automatone.api.cache.ICachedWorld;
import automatone.api.cache.IContainerMemory;
import automatone.api.cache.IWaypointCollection;
import automatone.api.cache.IWorldData;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Data about a world, from baritone's point of view. Includes cached chunks, waypoints, and map data.
 *
 * @author leijurv
 */
public class WorldData implements IWorldData {

    private final WaypointCollection waypoints;
    private final ContainerMemory containerMemory;
    //public final MapData map;
    public final Path directory;
    public final RegistryKey<World> dimension;

    WorldData(Path directory, RegistryKey<World> dimension) {
        this.directory = directory;
        this.waypoints = new WaypointCollection(directory.resolve("waypoints"));
        this.containerMemory = new ContainerMemory(directory.resolve("containers"));
        this.dimension = dimension;
    }

    public void onClose() {
        Baritone.getExecutor().execute(() -> {
            System.out.println("Started saving saved containers in a new thread");
            try {
                containerMemory.save();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save saved containers");
            }
        });
    }

    @Override
    public ICachedWorld getCachedWorld() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IWaypointCollection getWaypoints() {
        return this.waypoints;
    }

    @Override
    public IContainerMemory getContainerMemory() {
        return this.containerMemory;
    }
}