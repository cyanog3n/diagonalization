package com.cyanog3n.diagonalization.item;

import com.cyanog3n.diagonalization.PlacementHelper;
import com.cyanog3n.diagonalization.network.PacketHandler;
import com.cyanog3n.diagonalization.network.UpdateToServer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfinityWandItem extends DiagonalizationWandItem {

    public InfinityWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {

        int range = stack.getOrCreateTag().getInt("range");
        if(range == 0){
            range = 1;
            stack.getOrCreateTag().putInt("range", range);
        }

        MutableComponent rangeComponent = new TextComponent(String.valueOf(range)).withStyle(ChatFormatting.LIGHT_PURPLE);
        MutableComponent message = new TranslatableComponent("tooltips.diagonalization.range").append(rangeComponent);

        components.add(message);
        super.appendHoverText(stack, level, components, tooltipFlag);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        BlockPos pos = result.getBlockPos();
        BlockState block = level.getBlockState(pos);
        Vec3 viewLocation = result.getLocation();

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        int mode = tag.getInt("mode");
        int range = tag.getInt("range");

        if(mode == 0){
            mode = 1;
            tag.putInt("mode", mode);
        }

        if(range == 0){
            range = 1;
            tag.putInt("range", range);
        }

        if(player.isShiftKeyDown() && isAirOrFluid(block)){

            //cycles wand mode
            if(mode == 1 || mode == 2){
                mode++;
            }
            else{
                mode = 1;
            }
            tag.putInt("mode", mode);

            MutableComponent message = null;
            switch(mode){
                case 1 -> message = new TranslatableComponent("messages.diagonalization.vertical_edges");
                case 2 -> message = new TranslatableComponent("messages.diagonalization.horizontal_edges");
                case 3 -> message = new TranslatableComponent("messages.diagonalization.vertices");
            }

            if(level.isClientSide){
                player.sendMessage(message, player.getUUID());
            }
        }
        else if(!isAirOrFluid(block)){

            double xoffset = Math.abs(pos.getX() - viewLocation.x);
            double yoffset = Math.abs(pos.getY() - viewLocation.y);
            double zoffset = Math.abs(pos.getZ() - viewLocation.z);

            Vec3 lookAngle = player.getLookAngle();
            Direction direction = player.getDirection();
            Vec3 coordinates = new Vec3(xoffset, yoffset, zoffset);

            BlockPos targetpos = PlacementHelper.getEdgePlacement(pos, lookAngle, coordinates, mode, direction);

            for(int i = 1; i<=range; ++i){
                setBlockWithConditions(targetpos, block, level, player);
                targetpos = PlacementHelper.getEdgePlacement(targetpos, lookAngle, coordinates, mode, direction);
            }

        }

        return InteractionResultHolder.pass(stack);
    }


    public static void onPlaceBlockFromOffhand(PlayerInteractEvent.RightClickBlock event){

        Player player = event.getPlayer();
        InteractionHand hand = event.getHand();

        ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if(stack.is(ItemInit.INFINITY_DIAGONALIZATION_WAND.get()) && hand.equals(InteractionHand.OFF_HAND)){
            event.setCanceled(true);
        }

    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

}
