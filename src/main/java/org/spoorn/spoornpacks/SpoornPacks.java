package org.spoorn.spoornpacks;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;

@Log4j2
public class SpoornPacks implements ModInitializer {

    public static final String MODID = "SpoornPacks";

    @Override
    public void onInitialize() {
        log.info("Hello from SpoornPacks!");

        SpoornPack.test();
    }
}
