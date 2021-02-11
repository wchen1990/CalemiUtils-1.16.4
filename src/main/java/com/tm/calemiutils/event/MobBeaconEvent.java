package com.tm.calemiutils.event;

import com.tm.calemiutils.tileentity.TileEntityMobBeacon;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MobBeaconEvent {

    /**
     * Handles the Mob Beacon's spawning prevention.
     */
    @SubscribeEvent
    public void onEntitySpawn (LivingSpawnEvent.CheckSpawn event) {

        //Checks if its a natural spawn or a reinforcement.
        if (event.getSpawnReason() == SpawnReason.NATURAL || event.getSpawnReason() == SpawnReason.REINFORCEMENT) {

            //Checks if the entity is a monster or a Hoglin.
            if (event.getEntity() instanceof MonsterEntity || event.getEntity() instanceof HoglinEntity) {

                IWorld world = event.getWorld();
                MonsterEntity entity = (MonsterEntity) event.getEntity();
                IChunk chunk = world.getChunk(entity.getPosition());

                //Iterate through all Tile Entities within the entity's chunk.
                for (BlockPos tePos : chunk.getTileEntitiesPos()) {

                    TileEntity te = world.getTileEntity(tePos);

                    //Checks if the Tile Entity is a Mob Beacon.
                    if (te instanceof TileEntityMobBeacon) {

                        //Prevents the entity from spawning.
                        event.setResult(Event.Result.DENY);
                        return;
                    }
                }
            }
        }
    }
}
