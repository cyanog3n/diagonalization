package com.cyanog3n.diagonalization.network;

import com.cyanog3n.diagonalization.item.ItemInit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UpdateToServer {

    public static double scrollDelta;

    public UpdateToServer(double scrollDelta) {
        UpdateToServer.scrollDelta = scrollDelta;
    }

    public UpdateToServer(FriendlyByteBuf buffer) {
        scrollDelta = buffer.readDouble();

    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeDouble(scrollDelta);

    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            assert serverPlayer != null;

            ItemStack item = serverPlayer.getMainHandItem();
            if(item.is(ItemInit.INFINITY_DIAGONALIZATION_WAND.get())){

                CompoundTag tag = item.getOrCreateTag();
                int range = tag.getInt("range");
                int maxRange = 20;

                if(range == 0){
                    range = 1;
                    tag.putInt("range", range);
                }

                if(scrollDelta > 0){

                    if(range == maxRange){
                        range = 1;
                    }
                    else{
                        range++;
                    }
                }
                if(scrollDelta < 0){

                    if(range == 1){
                        range = maxRange;
                    }
                    else{
                        range--;
                    }
                }
                tag.putInt("range", range);

                MutableComponent rangeComponent = new TextComponent(String.valueOf(range)).withStyle(ChatFormatting.LIGHT_PURPLE);
                serverPlayer.sendMessage(new TranslatableComponent("messages.diagonalization.range").append(rangeComponent), serverPlayer.getUUID());
            }

            success.set(true);

        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
