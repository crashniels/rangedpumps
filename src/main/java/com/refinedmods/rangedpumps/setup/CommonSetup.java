package com.refinedmods.rangedpumps.setup;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.block.PumpBlock;
import com.refinedmods.rangedpumps.blockentity.PumpBlockEntity;
import com.refinedmods.rangedpumps.item.tab.MainCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class CommonSetup {

    public static void RegisterBlocks() {
        Registry.register(Registry.BLOCK, new ResourceLocation(RangedPumps.ID,""), new PumpBlock());
    }

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new ResourceLocation(RangedPumps.ID, "pump"), new BlockItem(PumpBlock.BLOCK, new Item.Properties().tab(MainCreativeModeTab.MAIN_TAB)));
    }

    public static void RegisterTiles() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(RangedPumps.ID, "pump"), FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, PumpBlock.BLOCK).build(null));
    }
}
