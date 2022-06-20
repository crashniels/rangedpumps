package com.refinedmods.rangedpumps.setup;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.block.PumpBlock;
import com.refinedmods.rangedpumps.blockentity.PumpBlockEntity;
import com.refinedmods.rangedpumps.item.tab.MainCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

public class CommonSetup {

    public static final Block PumpBlock = new PumpBlock();
    public static final BlockEntityType<PumpBlockEntity> PumpBlockTE = FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, PumpBlock).build(null);
    public static final Item PumpBlockItem = new BlockItem(PumpBlock, new Item.Properties().tab(MainCreativeModeTab.MAIN_TAB));

    public static void RegisterBlocks() {
        Registry.register(Registry.BLOCK, new ResourceLocation(RangedPumps.ID,"pump"), PumpBlock);
    }

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new ResourceLocation(RangedPumps.ID, "pump"), PumpBlockItem);
    }

    public static void RegisterTiles() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(RangedPumps.ID, "pump"), PumpBlockTE);
    }

    public static void RegiserEnergy() {
        EnergyStorage.SIDED.registerForBlockEntity((myBlockEntity, direction) -> myBlockEntity.energy, PumpBlockTE);
    }

    public static void RegiserFluids() {
        FluidStorage.SIDED.registerForBlockEntity((myBlockEntity, direction) -> myBlockEntity.tank, PumpBlockTE);
    }
}
