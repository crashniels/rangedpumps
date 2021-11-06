package com.refinedmods.rangedpumps.setup;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.block.PumpBlock;
import com.refinedmods.rangedpumps.blockentity.PumpBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonSetup {
    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(new PumpBlock());
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new BlockItem(PumpBlock.BLOCK, new Item.Properties().tab(RangedPumps.MAIN_TAB)).setRegistryName(RangedPumps.ID, "pump"));
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<BlockEntityType<?>> e) {
        e.getRegistry().register(BlockEntityType.Builder.of(PumpBlockEntity::new, PumpBlock.BLOCK).build(null).setRegistryName(RangedPumps.ID, "pump"));
    }
}
