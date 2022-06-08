package com.refinedmods.rangedpumps.blockentity;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.setup.CommonSetup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("ALL")
public class PumpBlockEntity extends BlockEntity {
    public final SingleVariantStorage<FluidVariant> tank = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return RangedPumps.SERVER_CONFIG.getTankCapacity();
        }

        @Override
        protected void onFinalCommit() {
            setChanged();
        }
    };

    public final SimpleEnergyStorage energy = new SimpleEnergyStorage(RangedPumps.SERVER_CONFIG.getEnergyCapacity(), 512, 512) {
        @Override
        protected void onFinalCommit() {
            setChanged();
        }
    };

    public enum FluidAction {
        EXECUTE,
        SIMULATE
    }

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
        tile.update(level, pos, state, tile);
    }

    private void update(Level level, BlockPos pos, BlockState state, PumpBlockEntity tile) {
        if (!RangedPumps.SERVER_CONFIG.getUseEnergy()) {
            try (Transaction transaction = Transaction.openOuter()) {
                energy.insert(energy.capacity, transaction);
            }

        }

        // Fill neighbors
        if (!tank.getResource().isBlank()) {
            List<Storage<FluidVariant>> fluidHandlers = new LinkedList<>();

            for (Direction facing : Direction.values()) {
                BlockEntity fluidTile = level.getBlockEntity(pos.relative(facing));

                if (fluidTile != null) {
                    Storage<FluidVariant> handler = FluidStorage.SIDED.find(level, pos, null);

                    if (handler != null) {
                        fluidHandlers.add(handler);
                    }
                }
            }

            if (!fluidHandlers.isEmpty()) {
                int transfer = (int) Math.floor((float) tank.getAmount() / (float) fluidHandlers.size());

                for (Storage<FluidVariant> fluidHandler : fluidHandlers) {
                    FluidVariant toFill = tank.getResource();
                    try (Transaction transaction = Transaction.openOuter()) {
                        tank.extract(toFill, transfer, transaction);
                    }

                }
            }
        }

        if ((RangedPumps.SERVER_CONFIG.getSpeed() == 0 || (ticks % RangedPumps.SERVER_CONFIG.getSpeed() == 0)) && getState() == PumpState.WORKING) {
            if (currentPos == null || currentPos.getY() == 0) {
                if (surfaces.isEmpty()) {
                    range++;

                    if (range > RangedPumps.SERVER_CONFIG.getRange()) {
                        return;
                    }

                    rebuildSurfaces();
                }

                currentPos = surfaces.poll();
            } else {
                currentPos = currentPos.below();
            }
            try (Transaction transaction = Transaction.openOuter()) {
                energy.extract(RangedPumps.SERVER_CONFIG.getEnergyUsagePerMove(), transaction);
            }


            FluidVariant drained = drainAt(level, currentPos, FluidAction.SIMULATE);

            long inserted = 0;
            if (!drained.isBlank()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    inserted = tank.insert(drained, FluidConstants.BUCKET ,transaction);
                }
            }

            if (!drained.isBlank() && inserted == FluidConstants.BUCKET) {
                drained = drainAt(level, currentPos, FluidAction.EXECUTE);

                if (!drained.isBlank()) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        tank.insert(drained, FluidConstants.BUCKET, transaction);
                        transaction.commit();
                    }


                    if (RangedPumps.SERVER_CONFIG.getReplaceLiquidWithBlock()) {
                        if (blockToReplaceLiquidsWith == null) {
                            blockToReplaceLiquidsWith = Registry.BLOCK.get(new ResourceLocation(RangedPumps.SERVER_CONFIG.getBlockIdToReplaceLiquidsWith()));
                        }

                        if (blockToReplaceLiquidsWith != null) {
                            level.setBlockAndUpdate(currentPos, blockToReplaceLiquidsWith.defaultBlockState());
                        }
                    }
                    try (Transaction transaction = Transaction.openOuter()) {
                        energy.extract(RangedPumps.SERVER_CONFIG.getEnergyUsagePerDrain(), transaction);
                        transaction.commit();
                    }

                }
            }

            setChanged();
        }

        ticks++;
    }

    @NotNull
    private FluidVariant drainAt(Level level, BlockPos pos, FluidAction action) {
        BlockState frontBlockState = level.getBlockState(pos);
        Block frontBlock = frontBlockState.getBlock();

        if (frontBlock instanceof LiquidBlock liquidBlock) {
            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
            if (frontBlockState.getValue(LiquidBlock.LEVEL) == 0) {
                Fluid fluid = liquidBlock.fluid;

                if (action == FluidAction.EXECUTE) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                }

                return FluidVariant.of(fluid, null);
            }
//        } else if (frontBlock instanceof LiquidBlock fluidBlock && fluidBlock.canDrain(level, pos)) {
//            return fluidBlock.drain(level, pos, action);
        }

        return FluidVariant.blank();
    }

    BlockPos getCurrentPosition() {
        return currentPos == null ? getBlockPos().below() : currentPos;
    }

    int getRange() {
        return range;
    }

    PumpState getState() {
        if (range > RangedPumps.SERVER_CONFIG.getRange()) {
            return PumpState.DONE;
        } else if (level.hasNeighborSignal(getCurrentPosition())) {
            return PumpState.REDSTONE;
        } else if (energy.getAmount() == 0) {
            return PumpState.ENERGY;
        } else if (tank.getAmount() > tank.getCapacity() - FluidConstants.BUCKET) {
            return PumpState.FULL;
        } else {
            return PumpState.WORKING;
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putLong("Energy", energy.getAmount());

        if (currentPos != null) {
            tag.putLong("CurrentPos", currentPos.asLong());
        }

        tag.putInt("Range", range);

        ListTag surfaces = new ListTag();

        this.surfaces.forEach(s -> surfaces.add(LongTag.valueOf(s.asLong())));

        tag.put("Surfaces", surfaces);

        tag.put("fluidVariant", tank.variant.toNbt());
        tag.putLong("amount", tank.amount);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        tank.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        tank.amount = tag.getLong("amount");
        energy.amount = tag.getLong("Energy");

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
    }
}
