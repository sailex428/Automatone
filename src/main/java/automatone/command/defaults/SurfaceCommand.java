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

package automatone.command.defaults;

import automatone.api.IBaritone;
import automatone.api.command.Command;
import automatone.api.command.argument.IArgConsumer;
import automatone.api.command.exception.CommandException;
import automatone.api.pathing.goals.Goal;
import automatone.api.pathing.goals.GoalBlock;
import automatone.api.utils.BetterBlockPos;
import net.minecraft.block.AirBlock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SurfaceCommand extends Command {

    protected SurfaceCommand(IBaritone baritone) {
        super(baritone, "surface", "top");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        final BetterBlockPos playerPos = baritone.getPlayerContext().feetPos();
        final int surfaceLevel = baritone.getPlayerContext().world().getSeaLevel();
        final int worldHeight = baritone.getPlayerContext().world().getHeight();

        // Ensure this command will not run if you are above the surface level and the block above you is air
        // As this would imply that your are already on the open surface
        if (playerPos.getY() > surfaceLevel && ctx.world().getBlockState(playerPos.up()).getBlock() instanceof AirBlock) {
            logDirect("Already at surface");
            return;
        }

        final int startingYPos = Math.max(playerPos.getY(), surfaceLevel);

        for (int currentIteratedY = startingYPos; currentIteratedY < worldHeight; currentIteratedY++) {
            final BetterBlockPos newPos = new BetterBlockPos(playerPos.getX(), currentIteratedY, playerPos.getZ());

            if (!(ctx.world().getBlockState(newPos).getBlock() instanceof AirBlock) && newPos.getY() > playerPos.getY()) {
                Goal goal = new GoalBlock(newPos.up());
                logDirect(String.format("Going to: %s", goal.toString()));
                baritone.getCustomGoalProcess().setGoalAndPath(goal);
                return;
            }
        }
        logDirect("No higher location found");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Used to get out of caves, mines, ...";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The surface/top command tells Baritone to head towards the closest surface-like area.",
                "",
                "This can be the surface or the highest available air space, depending on circumstances.",
                "",
                "Usage:",
                "> surface - Used to get out of caves, mines, ...",
                "> top - Used to get out of caves, mines, ..."
        );
    }
}