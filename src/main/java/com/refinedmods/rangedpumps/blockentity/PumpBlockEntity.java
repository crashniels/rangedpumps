package com.refinedmods.rangedpumps.blockentity;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.setup.CommonSetup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("ALL")
public class PumpBlockEntity extends BlockEntity {
    public static final BlockEntityType<PumpBlockEntity> TYPE = null;

    private final SingleFluidStorage tank = new SingleFluidStorage() {
        @Override
        protected long getCapacity(FluidVariant fluidVariant) {
            return RangedPumps.SERVER_CONFIG.getTankCapacity();
        }
    };
    public final SimpleEnergyStorage energy = new SimpleEnergyStorage(RangedPumps.SERVER_CONFIG.getEnergyCapacity(), 512,512){
        @Override
        protected void onFinalCommit() {
            //idk
        }
    };

    private int ticks;

    @Nullable
    private BlockPos currentPos;
    private int range = -1;
    private Queue<BlockPos> surfaces = new LinkedList<>();
    private Block blockToReplaceLiquidsWith;

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(CommonSetup.PumpBlockTE, pos, state);

        if (surfaces.isEmpty()) {
            rebuildSurfaces();
        }
    }

    private void rebuildSurfaces() {
        surfaces.clear();

        if (range == -1) {
            surfaces.add(getBlockPos().below());

            return;
        }

        int hl = 3 + 2 * range;
        int vl = 1 + 2 * range;

        // Top
        for (int i = 0; i < hl; ++i) {
            surfaces.add(getBlockPos().offset(-range - 1 + i, -1, -range - 1));
        }

        // Right
        for (int i = 0; i < vl; ++i) {
            surfaces.add(getBlockPos().offset(-range - 1 + vl + 1, -1, -range - 1 + i + 1));
        }

        // Bottom
        for (int i = 0; i < hl; ++i) {
            surfaces.add(getBlockPos().offset(-range - 1 + hl - i - 1, -1, -range - 1 + hl - 1));
        }

        // Left
        for (int i = 0; i < vl; ++i) {
            surfaces.add(getBlockPos().offset(-range - 1, -1, -range - 1 + vl - i));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PumpBlockEntity tile) {
        //tile.update(level, pos);
    }

//    private void update(Level level, BlockPos pos) {
//        if (!RangedPumps.SERVER_CONFIG.getUwseEnergy()) {
//            energy.insert(energy.capacity, Transaction.openOuter());
//        }
//
//        // Fill neighbors
//        if (!tank.getResource().isBlank()) {
//            List<FluidVariant> fluidHandlers = new LinkedList<>();
//
//            for (Direction facing : Direction.values()) {
//                BlockEntity fluidTile = level.getBlockEntity(pos.relative(facing));
//
//                if (fluidTile != null) {
//                    FluidStorage handler = fluidTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
//
//                    if (handler != null) {
//                        fluidHandlers.add(handler);
//                    }
//                }
//            }
//
//            if (!fluidHandlers.isEmpty()) {
//                int transfer = (int) Math.floor((float) tank.getAmount() / (float) fluidHandlers.size());
//
//                for (IFluidHandler fluidHandler : fluidHandlers) {
//                    FluidVariant toFill = tank.getResource();
//                    toFill.setAmount(transfer);
//
//                    tank.drain(fluidHandler.fill(toFill, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
//                }
//            }
//        }
//
//        if ((RangedPumps.SERVER_CONFIG.getSpeed() == 0 || (ticks % RangedPumps.SERVER_CONFIG.getSpeed() == 0)) && getState() == PumpState.WORKING) {
//            if (currentPos == null || currentPos.getY() == 0) {
//                if (surfaces.isEmpty()) {
//                    range++;
//
//                    if (range > RangedPumps.SERVER_CONFIG.getRange()) {
//                        return;
//                    }
//
//                    rebuildSurfaces();
//                }
//
//                currentPos = surfaces.poll();
//            } else {
//                currentPos = currentPos.below();
//            }
//
//            energy.extract(RangedPumps.SERVER_CONFIG.getEnergyUsagePerMove(), Transaction.openOuter());
//
//            FluidVariant drained = drainAt(level, currentPos, IFluidHandler.FluidAction.SIMULATE);
//
//            if (!drained.isBlank() && tank.fillInternal(drained, IFluidHandler.FluidAction.SIMULATE) == drained.getAmount()) {
//                drained = drainAt(level, currentPos, IFluidHandler.FluidAction.EXECUTE);
//
//                if (!drained.isBlank()) {
//                    tank.fillInternal(drained, IFluidHandler.FluidAction.EXECUTE);
//
//                    if (RangedPumps.SERVER_CONFIG.getReplaceLiquidWithBlock()) {
//                        if (blockToReplaceLiquidsWith == null) {
//                            blockToReplaceLiquidsWith = Registry.BLOCK.get(new ResourceLocation(RangedPumps.SERVER_CONFIG.getBlockIdToReplaceLiquidsWith()));
//                        }
//
//                        if (blockToReplaceLiquidsWith != null) {
//                            level.setBlockAndUpdate(currentPos, blockToReplaceLiquidsWith.defaultBlockState());
//                        }
//                    }
//
//                    energy.extract(RangedPumps.SERVER_CONFIG.getEnergyUsagePerDrain(), Transaction.openOuter());
//                }
//            }
//
//            setChanged();
//        }
//
//        ticks++;
//    }
//
//    @NotNull
//    private FluidVariant drainAt(Level level, BlockPos pos, TransactionContext.Result action) {
//        BlockState frontBlockState = level.getBlockState(pos);
//        Block frontBlock = frontBlockState.getBlock();
//
//        if (frontBlock instanceof LiquidBlock liquidBlock) {
//            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
//            if (frontBlockState.getValue(LiquidBlock.LEVEL) == 0) {
//                Fluid fluid = liquidBlock.getFluid().getFlowing();
//
//                if (action == TransactionContext.Result.COMMITTED) {
//                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
//                }
//
//                return new Fluid(fluid, FluidAttributes.BUCKET_VOLUME);
//            }
//        } else if (frontBlock instanceof IFluidBlock fluidBlock && fluidBlock.canDrain(level, pos)) {
//            return fluidBlock.drain(level, pos, action);
//        }
//
//        return FluidVariant.blank();
//    }
//
//    BlockPos getCurrentPosition() {
//        return currentPos == null ? getBlockPos().below() : currentPos;
//    }
//
//    int getRange() {
//        return range;
//    }
//
//    PumpState getState() {
//        if (range > RangedPumps.SERVER_CONFIG.getRange()) {
//            return PumpState.DONE;
//        } else if (level.hasNeighborSignal(getCurrentPosition())) {
//            return PumpState.REDSTONE;
//        } else if (energy.getAmount() == 0) {
//            return PumpState.ENERGY;
//        } else if (tank.getAmount() > tank.getCapacity() - FluidConstants.BUCKET) {
//            return PumpState.FULL;
//        } else {
//            return PumpState.WORKING;
//        }
//    }
//
//    public SingleFluidStorage getTank() {
//        return tank;
//    }
//
//    public SimpleEnergyStorage getEnergy() {
//        return energy;
//    }
//
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putLong("Energy", energy.getAmount());

        if (currentPos != null) {
            tag.putLong("CurrentPos", currentPos.asLong());
        }

        tag.putInt("Range", range);

        ListTag surfaces = new ListTag();

        this.surfaces.forEach(s -> surfaces.add(LongTag.valueOf(s.asLong())));

        tag.put("Surfaces", surfaces);

        tank.writeToNBT(tag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.deserializeNBT(tag);

        if (tag.contains("Energy")) {
            energy.insert(tag.getInt("Energy"), Transaction.openOuter());
        }

        if (tag.contains("CurrentPos")) {
            currentPos = BlockPos.of(tag.getLong("CurrentPos"));
        }

        if (tag.contains("Range")) {
            range = tag.getInt("Range");
        }

        if (tag.contains("Surfaces")) {
            ListTag surfaces = tag.getList("Surfaces", Tag.TAG_LONG);

            for (Tag surface : surfaces) {
                this.surfaces.add(BlockPos.of(((LongTag) surface).getAsLong()));
            }
        }

        tank.readFromNBT(tag);
    }


//    private static class PumpTank extends SingleVariantStorage<FluidVariant> implements ExtractionOnlyStorage<FluidVariant> {
//        @Override
//        protected FluidVariant getBlankVariant() {
//            return FluidVariant.of(Fluids.LAVA);
//        }
//
//        @Override
//        protected long getCapacity(FluidVariant variant) {
//            return RangedPumps.SERVER_CONFIG.getTankCapacity();
//        }
//
//        public long getCap(FluidVariant variant) {
//            return getCapacity();
//        }
//
//        public String getFluidName() {
//            return this.getAmount()
//        }
//    }
}
