package com.refinedmods.rangedpumps.item.tab;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.block.PumpBlock;
import com.refinedmods.rangedpumps.setup.CommonSetup;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MainCreativeModeTab {

    public static final CreativeModeTab MAIN_TAB = FabricItemGroupBuilder.build(
            new ResourceLocation(RangedPumps.ID, "itemgroup"),
            () -> new ItemStack(CommonSetup.PumpBlock)
    );

}
