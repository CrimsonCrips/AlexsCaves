package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EnigmaticEngineBlockEntity extends BlockEntity {

    private int checkTime;

    public EnigmaticEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.ENIGMATIC_ENGINE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnigmaticEngineBlockEntity entity) {
        if (entity.checkTime-- <= 0) {
            entity.checkTime = 30 + level.random.nextInt(30);
            entity.attemptAssembly();
        }
    }

    public boolean attemptAssembly() {
        Direction assembleIn = null;
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            if (isAssembledInDirection(direction)) {
                assembleIn = direction;
                break;
            }
        }
        if (assembleIn != null) {
            for (BlockPos pos : BlockPos.betweenClosed(this.getBlockPos().getX() - 1, this.getBlockPos().getY() - 1, this.getBlockPos().getZ() - 1, this.getBlockPos().getX() + 1, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 1)) {
                if(level.getBlockState(pos).is(ACBlockRegistry.DEPTH_GLASS.get()) || level.getBlockState(pos).is(ACTagRegistry.SUBMARINE_ASSEMBLY_BLOCKS)  || level.getBlockState(pos).is(ACBlockRegistry.ENIGMATIC_ENGINE.get())){
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            if(!level.isClientSide){
                SubmarineEntity submarine = ACEntityRegistry.SUBMARINE.get().create(level);
                Vec3 vec31 = Vec3.atCenterOf(this.getBlockPos()).add(0, -1, 0);
                submarine.setYRot(assembleIn.toYRot());
                submarine.setPos(vec31.x, vec31.y, vec31.z);
                submarine.setOxidizationLevel(0);
                level.addFreshEntity(submarine);
            }
            return true;
        }
        return false;
    }

    private boolean isAssembledInDirection(Direction direction) {
        List<BlockPos> scrap = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos blockCheck = new BlockPos(this.getBlockPos().getX() + x,this.getBlockPos().getY() + y,this.getBlockPos().getZ() + z);
                    if (y == 0){
                        if (level.getBlockState(blockCheck).is(ACBlockRegistry.SCRAP_METAL_PLATE.get())) {
                            scrap.add(blockCheck);
                        } else {
                            return false;
                        }
                    } else {
                        if (level.getBlockState(blockCheck).is(ACBlockRegistry.SCRAP_METAL_PLATE.get()) && x == 0 && z == 0) {
                            scrap.add(blockCheck);
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        System.out.println();
        if (scrap.size() > 1) {
            return false;
        } else {
            return false;
        }
    }
}
