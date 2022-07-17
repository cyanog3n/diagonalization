package com.cyanog3n.diagonalization;

import com.cyanog3n.diagonalization.item.DiagonalizationWandItem;
import com.cyanog3n.diagonalization.item.InfinityWandItem;
import com.cyanog3n.diagonalization.item.ItemInit;
import com.cyanog3n.diagonalization.network.PacketHandler;
import com.cyanog3n.diagonalization.network.UpdateToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onPlayerPlaceBlock(PlayerInteractEvent.RightClickBlock event){
        DiagonalizationWandItem.onPlaceBlockFromOffhand(event);
        InfinityWandItem.onPlaceBlockFromOffhand(event);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onDrawSelectionWireframe(DrawSelectionEvent.HighlightBlock event){
        PreviewBlockRenderer.renderPreview(event);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onScroll(InputEvent.MouseScrollEvent event){

        double scrollDelta = event.getScrollDelta();

        Player player = Minecraft.getInstance().player;
        ItemStack item = player.getMainHandItem();

        if(item.is(ItemInit.INFINITY_DIAGONALIZATION_WAND.get()) && player.isShiftKeyDown()){
            PacketHandler.INSTANCE.sendToServer(new UpdateToServer(scrollDelta));
            event.setCanceled(true);
        }

    }




}
