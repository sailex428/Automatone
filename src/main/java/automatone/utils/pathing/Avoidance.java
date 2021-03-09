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

package automatone.utils.pathing;

import automatone.Baritone;
import automatone.api.utils.BetterBlockPos;
import automatone.api.utils.IEntityContext;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Avoidance {

    private final int centerX;
    private final int centerY;
    private final int centerZ;
    private final double coefficient;
    private final int radius;
    private final int radiusSq;

    public Avoidance(BlockPos center, double coefficient, int radius) {
        this(center.getX(), center.getY(), center.getZ(), coefficient, radius);
    }

    public Avoidance(int centerX, int centerY, int centerZ, double coefficient, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.coefficient = coefficient;
        this.radius = radius;
        this.radiusSq = radius * radius;
    }

    public double coefficient(int x, int y, int z) {
        int xDiff = x - centerX;
        int yDiff = y - centerY;
        int zDiff = z - centerZ;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff <= radiusSq ? coefficient : 1.0D;
    }

    public static List<Avoidance> create(IEntityContext ctx) {
        if (!Baritone.settings().avoidance.value) {
            return Collections.emptyList();
        }

        List<Avoidance> res = new ArrayList<>();
        double mobCoeff = Baritone.settings().mobAvoidanceCoefficient.value;

        if (mobCoeff != 1.0D) {
            ctx.worldEntitiesStream()
                    .filter(entity -> entity instanceof MobEntity)
                    .filter(entity -> (!(entity instanceof SpiderEntity)) || ctx.entity().getBrightnessAtEyes() < 0.5)
                    .filter(entity -> !(entity instanceof ZombifiedPiglinEntity) || ((ZombifiedPiglinEntity) entity).getAttacker() != null)
                    .filter(entity -> !(entity instanceof EndermanEntity) || ((EndermanEntity) entity).isAngry())
                    .forEach(entity -> res.add(new Avoidance(entity.getBlockPos(), mobCoeff, Baritone.settings().mobAvoidanceRadius.value)));
        }

        return res;
    }

    public void applySpherical(Long2DoubleOpenHashMap map) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        long hash = BetterBlockPos.longHash(centerX + x, centerY + y, centerZ + z);
                        map.put(hash, map.get(hash) * coefficient);
                    }
                }
            }
        }
    }
}