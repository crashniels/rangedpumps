package com.refinedmods.rangedpumps;

import com.refinedmods.rangedpumps.config.ServerConfig;
import com.refinedmods.rangedpumps.setup.CommonSetup;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class RangedPumps implements ModInitializer {
    public static final String ID = "rangedpumps";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(ID, ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        CommonSetup.RegisterBlocks();
        CommonSetup.RegisterTiles();
        CommonSetup.RegisterItems();
        CommonSetup.RegiserEnergy();
        CommonSetup.RegiserFluids();
    }
}
