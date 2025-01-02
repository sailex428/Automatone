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

package baritone.api.fakeplayer;

import com.demonwav.mcdev.annotations.CheckEnv;
import com.demonwav.mcdev.annotations.Env;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FakePlayers {
    /**
     * The ID for the default fake player spawn packet
     *
     * @see FakeServerPlayerEntity#createSpawnPacket()
     */
    public static final Identifier SPAWN_PACKET_ID = Identifier.of("automatone", "fake_player_spawn");
    public static final Identifier PROFILE_UPDATE_PACKET_ID = Identifier.of("automatone", "fake_player_profile");

    @CheckEnv(Env.CLIENT)
    static final Map<EntityType<? extends PlayerEntity>, FakePlayerFactory<ClientWorld, ?>> clientFactories = new HashMap<>();

    public static <P extends PlayerEntity & AutomatoneFakePlayer> EntityType.EntityFactory<PlayerEntity> entityFactory(ServerFakePlayerFactory<? extends P> serverFactory) {
        return (type, world) -> world.isClient ? FakePlayers.getFakeClientPlayerFactory(type).create(type, world) : serverFactory.create(type, (ServerWorld) world);
    }

    /**
     * Registers a clientside factory for the default fake player spawn packet.
     *
     * <p>If a clientside factory is not registered for a custom fake player type that uses the
     * {@linkplain #SPAWN_PACKET_ID default spawn packet},
     * a {@link FakeClientPlayerEntity} will be spawned in the client world.
     *
     * <p>Typical usage: <pre><code>
     *  public void onInitializeClient() {
     *      FakePlayers.registerClientFactory(MY_PLAYER_TYPE, MyClientPlayerEntity::new);
     *  }
     * </code></pre>
     *
     * @param type          the type of the fake player to register a clientside factory for
     * @param clientFactory a factory for that type of fake player
     */
    @CheckEnv(Env.CLIENT)
    public static void registerClientFactory(EntityType<? extends PlayerEntity> type, FakePlayerFactory<ClientWorld, ?> clientFactory) {
        clientFactories.put(type, clientFactory);
    }

    public static GameProfile generateGameProfile() {
        return new GameProfile(UUID.randomUUID(), "Fake Player");
    }

    static <W extends World, P extends PlayerEntity & AutomatoneFakePlayer> FakePlayerFactory<W, P> getFakeClientPlayerFactory(EntityType<? super P> type) {
        @SuppressWarnings("unchecked") FakePlayerFactory<W, P> factory = (FakePlayerFactory<W, P>) clientFactories.getOrDefault(type, FakeClientPlayerEntity::new);
        return factory;
    }

    public interface ServerFakePlayerFactory<P extends PlayerEntity> {
        P create(EntityType<? extends PlayerEntity> type, ServerWorld world);
    }

    public interface FakePlayerFactory<W extends World, P extends PlayerEntity & AutomatoneFakePlayer> {
        default P create(EntityType<? super P> type, W world) {
            return create(type, world, generateGameProfile());
        }

        P create(EntityType<? super P> type, W world, GameProfile profile);
    }
}
