package com.refinedmods.rangedpumps.block;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.blockentity.PumpBlockEntity;
import com.refinedmods.rangedpumps.blockentity.PumpState;
import com.refinedmods.rangedpumps.setup.CommonSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class PumpBlock extends BaseEntityBlock {
    public static final PumpBlock BLOCK = null;

    public PumpBlock() {
        super(Block.Properties.of(Material.STONE).strength(1.9F).sound(SoundType.STONE));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof PumpBlockEntity) {
                PumpBlockEntity pump = (PumpBlockEntity) blockEntity;

               EnergyStorage energy = EnergyStorage.SIDED.find(level, pos, player.getDirection());
               if (energy == null) {
                   return InteractionResult.SUCCESS;
               }

                Component message = PumpState.getMessage(pump);

                if (message != null) {
                    player.sendMessage(message, player.getUUID());
                }

                if (pump.tank.getAmount() == 0) {
                    player.sendMessage(new TranslatableComponent("block." + RangedPumps.ID + ".pump.state_empty", pump.energy.getAmount(), pump.energy.getCapacity()), player.getUUID());
                } else {
                    player.sendMessage(new TranslatableComponent("block." + RangedPumps.ID + ".pump.state", pump.tank.getAmount(), pump.tank.getResource().getFluid().getBucket().getDescription().getString(), pump.energy.getAmount(), pump.energy.getCapacity()), player.getUUID());
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PumpBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, CommonSetup.PumpBlockTE, PumpBlockEntity::serverTick);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
