package com.refinedmods.rangedpumps.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private ForgeConfigSpec.IntValue range;
    private ForgeConfigSpec.IntValue speed;
    private ForgeConfigSpec.LongValue tankCapacity;
    private ForgeConfigSpec.IntValue energyCapacity;
    private ForgeConfigSpec.IntValue energyUsagePerMove;
    private ForgeConfigSpec.IntValue energyUsagePerDrain;
    private ForgeConfigSpec.BooleanValue useEnergy;
    private ForgeConfigSpec.BooleanValue replaceLiquidWithBlock;
    private ForgeConfigSpec.ConfigValue<String> blockIdToReplaceLiquidsWith;

    public ServerConfig() {
        builder.push("pump");

        range = builder.comment("The range of the pump").defineInRange("range", 64, 0, 1024);
        speed = builder.comment("The interval in ticks for when to move on to the next block (higher is slower)").defineInRange("speed", 8, 0, 1024);
        tankCapacity = builder.comment("The capacity of the internal pump tank").defineInRange("tankCapacity", FluidConstants.BUCKET * 32, FluidConstants.BUCKET, Integer.MAX_VALUE);
        energyCapacity = builder.comment("The capacity of the energy storage").defineInRange("energyCapacity", 32000, 0, Integer.MAX_VALUE);
        energyUsagePerMove = builder.comment("Energy drained when moving to the next block").defineInRange("energyUsagePerMove", 0, 0, Integer.MAX_VALUE);
        energyUsagePerDrain = builder.comment("Energy drained when draining liquid").defineInRange("energyUsagePerDrain", 100, 0, Integer.MAX_VALUE);
        useEnergy = builder.comment("Whether the pump uses energy to work").define("useEnergy", true);
        replaceLiquidWithBlock = builder.comment("Replaces liquids that are removed with a block defined in 'blockIdToReplaceLiquidsWith' (to reduce lag)").define("replaceLiquidWithBlock", true);
        blockIdToReplaceLiquidsWith = builder.comment("The block that liquids are replaced with when 'replaceLiquidWithBlock' is true").define("blockIdToReplaceLiquidsWith", "minecraft:stone");

        builder.pop();

        spec = builder.build();
    }

    public int getRange() {
        return range.get();
    }

    public int getSpeed() {
        return speed.get();
    }

    public long getTankCapacity() {
        return tankCapacity.get();
    }

    public int getEnergyCapacity() {
        return energyCapacity.get();
    }

    public int getEnergyUsagePerMove() {
        return energyUsagePerMove.get();
    }

    public int getEnergyUsagePerDrain() {
        return energyUsagePerDrain.get();
    }

    public boolean getUseEnergy() {
        return useEnergy.get();
    }

    public boolean getReplaceLiquidWithBlock() {
        return replaceLiquidWithBlock.get();
    }

    public String getBlockIdToReplaceLiquidsWith() {
        return blockIdToReplaceLiquidsWith.get();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }
}
