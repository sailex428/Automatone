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

package baritone.utils;

import baritone.Baritone;
import baritone.altoclef.AltoClefSettings;
import baritone.api.utils.IInputOverrideHandler;
import baritone.api.utils.input.Input;
import baritone.behavior.Behavior;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Set;

/**
 * An interface with the game's control system allowing the ability to
 * force down certain controls, having the same effect as if we were actually
 * physically forcing down the assigned key.
 *
 * @author Brady
 * @since 7/31/2018
 */
public final class InputOverrideHandler extends Behavior implements IInputOverrideHandler {

    /**
     * Maps inputs to whether or not we are forcing their state down.
     */
    private final Set<Input> inputForceStateMap = EnumSet.noneOf(Input.class);

    private final BlockBreakHelper blockBreakHelper;
    private final BlockPlaceHelper blockPlaceHelper;
    private boolean needsUpdate;

    public InputOverrideHandler(Baritone baritone) {
        super(baritone);
        this.blockBreakHelper = new BlockBreakHelper(baritone.getPlayerContext());
        this.blockPlaceHelper = new BlockPlaceHelper(baritone.getPlayerContext());
    }

    /**
     * Returns whether or not we are forcing down the specified {@link Input}.
     *
     * @param input The input
     * @return Whether or not it is being forced down
     */
    @Override
    public final synchronized boolean isInputForcedDown(Input input) {
        return input != null && this.inputForceStateMap.contains(input);
    }

    /**
     * Sets whether or not the specified {@link Input} is being forced down.
     *
     * @param input  The {@link Input}
     * @param forced Whether or not the state is being forced
     */
    @Override
    public final synchronized void setInputForceState(Input input, boolean forced) {
        if (forced) {
            this.inputForceStateMap.add(input);
        } else {
            this.inputForceStateMap.remove(input);
        }
        this.needsUpdate = true;
    }

    /**
     * Clears the override state for all keys
     */
    @Override
    public final synchronized void clearAllKeys() {
        // Note that calling setSprinting before entity attributes are initialized will crash the game
        // PERF: entity flags use a lock, see if we can put this elsewhere to reduce the number of calls
        if (this.ctx.entity().isSprinting()) this.ctx.entity().setSprinting(false);
        this.inputForceStateMap.clear();
        this.needsUpdate = true;
    }

    @Override
    public final void onTickServer() {
        if (!this.needsUpdate) return;

        if (isInputForcedDown(Input.CLICK_LEFT)) {
            setInputForceState(Input.CLICK_RIGHT, false);
        }

        ServerPlayerEntity entity = this.ctx.entity();

        Vec3d currentVelocity = entity.getVelocity();
        entity.setSneaking(false);

        float vel;
        float forward = 0.0F;
        float strafing = 0.0F;

        if (this.isInputForcedDown(Input.SNEAK)) {
            entity.setSneaking(true);
            vel = 0.3F;
        } else {
            vel = 1.0F;
        }

        if (this.isInputForcedDown(Input.MOVE_FORWARD)) {
            forward++;
        }
        if (this.isInputForcedDown(Input.MOVE_BACK)) {
            forward--;
        }
        if (this.isInputForcedDown(Input.MOVE_LEFT)) {
            strafing++;
        }
        if (this.isInputForcedDown(Input.MOVE_RIGHT)) {
            strafing--;
        }

        if (forward != 0.0F || strafing != 0.0F) {
            float yaw = entity.getYaw();
            double yawRad = Math.toRadians(yaw);

            double sin = Math.sin(yawRad);
            double cos = Math.cos(yawRad);

            double speed = vel * 0.3;
            double motionX = (strafing * cos - forward * sin) * speed;
            double motionZ = (forward * cos + strafing * sin) * speed;

            entity.setVelocity(motionX, currentVelocity.y, motionZ);
            entity.velocityModified = true;
        } else {
            entity.setVelocity(0, currentVelocity.y, 0);
        }

        if (this.isInputForcedDown(Input.JUMP) && entity.isOnGround()) {
            entity.jump();
        }

        if (AltoClefSettings.getInstance().isInteractionPaused()) {
            setInputForceState(Input.CLICK_LEFT, false);
            setInputForceState(Input.CLICK_RIGHT, false);
        }

        blockBreakHelper.tick(isInputForcedDown(Input.CLICK_LEFT));
        blockPlaceHelper.tick(isInputForcedDown(Input.CLICK_RIGHT));

        this.needsUpdate = false;
    }

    public BlockBreakHelper getBlockBreakHelper() {
        return blockBreakHelper;
    }
}
