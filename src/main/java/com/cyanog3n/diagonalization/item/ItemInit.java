package com.cyanog3n.diagonalization.item;

import com.cyanog3n.diagonalization.Diagonalization;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Diagonalization.MODID);
    public static final CreativeModeTab MODTAB = new CreativeModeTab("Diagonalization") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.DIAGONALIZATION_WAND.get());
        }
    };

    //-----ITEMS-----//

    public static final RegistryObject<Item> DIAGONALIZATION_WAND = ITEMS.register("diagonalization_wand",
            () -> new DiagonalizationWandItem(new Item.Properties().tab(MODTAB)));

    public static final RegistryObject<Item> INFINITY_DIAGONALIZATION_WAND = ITEMS.register("infinity_diagonalization_wand",
            () -> new InfinityWandItem(new Item.Properties().tab(MODTAB)));

    //----------//

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
