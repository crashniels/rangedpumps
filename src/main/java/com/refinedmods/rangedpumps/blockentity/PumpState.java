package com.refinedmods.rangedpumps.blockentity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum PumpState {
    ENERGY,
    REDSTONE,
    WORKING,
    FULL,
    DONE;

//    public static Component getMessage(PumpBlockEntity pump) {
//        return switch (pump.getState()) {
//            case ENERGY -> new TranslatableComponent("block.rangedpumps.pump.state.energy");
//            case REDSTONE -> new TranslatableComponent("block.rangedpumps.pump.state.redstone");
//            case WORKING -> new TranslatableComponent("block.rangedpumps.pump.state.working", pump.getCurrentPosition().getX(), pump.getCurrentPosition().getY(), pump.getCurrentPosition().getZ(), pump.getRange());
//            case FULL -> new TranslatableComponent("block.rangedpumps.pump.state.full");
//            case DONE -> new TranslatableComponent("block.rangedpumps.pump.state.done");
//        };
//    }
}
