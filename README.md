# Automatone

> **Note**: This project is no longer maintained. Development has moved to [SecondBrainEngine](https://github.com/sailex428/SecondBrainEngine)

A server-side Minecraft pathfinding bot built on top of Baritone.
This project is a fork of Automatone, modified to work exclusively server-side, without any client-side NPC rendering.
It leverages Carpet to spawn `ServerPlayerEntity` instances using a `FakeClientConnection`, which are then controlled through Automatone's pathfinding logic.

**Warning: this project is experimental. Although it is already performing well in vanilla, strange bugs may arise in heavily modded environments.
Backwards compatibility is also not being considered at the current time, so avoid depending on this for stable projects.**

## API

Below is an example of basic usage for changing some settings, and then pathing to an X/Z goal.

```java
IBaritone baritone = BaritoneAPI.getProvider().getBaritone(entity);
baritone.settings().allowSprint.set(true);
baritone.settings().primaryTimeoutMS.set(2000L);

baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(10000, 20000));
```

### Control a NPC Player (idk if this still works)
This mod includes fabric-carpet. To spawn a NPC use the `/player <your-npc-name> spawn`.
Then you can execute every automatone command as the NPC player. 
Example: `/execute as <your-npc-name> run automatone goto 100 100`

### How is it so fast?

Magic. (Hours of [leijurv](https://github.com/leijurv/) enduring excruciating pain)
