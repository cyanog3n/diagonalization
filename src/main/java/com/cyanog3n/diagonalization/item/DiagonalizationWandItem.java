package com.cyanog3n.diagonalization.item;

import com.cyanog3n.diagonalization.PlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiagonalizationWandItem extends Item {

    public DiagonalizationWandItem(Properties properties) {
        super(properties.defaultDurability(1500));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {

        int mode = stack.getOrCreateTag().getInt("mode");

        MutableComponent message = null;

        switch (mode) {
            case 0, 1 -> message = new TranslatableComponent("tooltips.diagonalization.vertical_edges"); //yellow
            case 2 -> message = new TranslatableComponent("tooltips.diagonalization.horizontal_edges");  //aqua
            case 3 -> message = new TranslatableComponent("tooltips.diagonalization.vertices");  //green
        }

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

        if(mode == 0){
            mode = 1;
            tag.putInt("mode", mode);
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

            setBlockWithConditions(targetpos, block, level, player);
            //System.out.println("Coordinate: "+coordinates+" LookAngle: "+lookAngle);
        }

        return super.use(level, player, hand);
    }


    public boolean isAirOrFluid(BlockState state){

        return state.isAir() || state.getBlock() instanceof LiquidBlock;

    }

    public static void onPlaceBlockFromOffhand(PlayerInteractEvent.RightClickBlock event){

        Player player = event.getPlayer();
        InteractionHand hand = event.getHand();

        ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if(stack.is(ItemInit.DIAGONALIZATION_WAND.get()) && hand.equals(InteractionHand.OFF_HAND)){
            event.setCanceled(true);
        }

    }

    public void setBlockWithConditions(BlockPos pos, BlockState block, Level level, Player player){

        Inventory inventory = player.getInventory();
        ItemStack stack = new ItemStack(block.getBlock().asItem(), 1);
        ItemStack wand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));

        if(isAirOrFluid(level.getBlockState(pos)) && entities.isEmpty()){

            if(player.isCreative()) {

                if(offhand.getItem() instanceof BlockItem blockItem) {
                    level.setBlock(pos, blockItem.getBlock().defaultBlockState(), 1);
                }
                else {
                    level.setBlock(pos, block, 1);
                }
            }
            else if(offhand.getItem() instanceof BlockItem blockItem){
                level.setBlock(pos, blockItem.getBlock().defaultBlockState(), 1);

                offhand.shrink(1);
                wand.hurtAndBreak(1, player, LivingEntity::stopUsingItem);
            }
            else if(inventory.contains(stack)){
                level.setBlock(pos, block, 1);

                int slot = inventory.findSlotMatchingItem(stack);
                inventory.getItem(slot).shrink(1);
                wand.hurtAndBreak(1, player, LivingEntity::stopUsingItem);
            }
        }

    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }
}
