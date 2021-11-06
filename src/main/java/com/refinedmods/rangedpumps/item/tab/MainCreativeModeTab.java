package com.refinedmods.rangedpumps.item.tab;

import com.refinedmods.rangedpumps.RangedPumps;
import com.refinedmods.rangedpumps.block.PumpBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MainCreativeModeTab extends CreativeModeTab {
    public MainCreativeModeTab() {
        super(RangedPumps.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(PumpBlock.BLOCK);
    }
}
