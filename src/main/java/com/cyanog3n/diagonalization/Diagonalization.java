package com.cyanog3n.diagonalization;

import com.cyanog3n.diagonalization.item.ItemInit;
import com.cyanog3n.diagonalization.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Diagonalization.MODID)
public class Diagonalization
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "diagonalization";

    public Diagonalization()
    {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        eventbus.addListener(this::setup);
        eventbus.addListener(this::clientSetup);

        ItemInit.register(eventbus);
        PacketHandler.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        MinecraftForge.EVENT_BUS.register(new EventHandler());

    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        // some preinit code

    }


}
