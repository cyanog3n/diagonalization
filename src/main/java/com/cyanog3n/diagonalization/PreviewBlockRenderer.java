package com.cyanog3n.diagonalization;

import com.cyanog3n.diagonalization.item.ItemInit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

public class PreviewBlockRenderer {

    public static void renderPreview(DrawSelectionEvent.HighlightBlock event){

        PoseStack poseStack = event.getPoseStack();
        Vec3 viewLocation = event.getTarget().getLocation();
        BlockPos pos = event.getTarget().getBlockPos();
        Level level = event.getCamera().getEntity().level;
        MultiBufferSource source = event.getMultiBufferSource();
        BlockState state = level.getBlockState(pos);
        Entity e = event.getCamera().getEntity();
        long tick =level.getGameTime();

        if(e instanceof Player player && level.isClientSide){
            ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);

            double xoffset = Math.abs(pos.getX() - viewLocation.x);
            double yoffset = Math.abs(pos.getY() - viewLocation.y);
            double zoffset = Math.abs(pos.getZ() - viewLocation.z);

            Vec3 lookAngle = player.getLookAngle();
            Direction direction = player.getDirection();
            Vec3 coordinates = new Vec3(xoffset, yoffset, zoffset);

            if(item.is(ItemInit.DIAGONALIZATION_WAND.get())){

                int mode = item.getOrCreateTag().getInt("mode");
                BlockPos targetpos = PlacementHelper.getEdgePlacement(pos, lookAngle, coordinates, mode, direction);

                if(isValid(targetpos, level)){

                    if(offhand.getItem() instanceof BlockItem block){
                        BlockState offhandState = block.getBlock().defaultBlockState();
                        renderBlock(offhandState, targetpos, poseStack, source, tick);
                    }
                    else{
                        renderBlock(state, targetpos, poseStack, source, tick);
                    }
                }
            }
            else if(item.is(ItemInit.INFINITY_DIAGONALIZATION_WAND.get())){

                int mode = item.getOrCreateTag().getInt("mode");
                int range = item.getOrCreateTag().getInt("range");

                BlockPos targetpos = PlacementHelper.getEdgePlacement(pos, lookAngle, coordinates, mode, direction);
                BlockState renderState = state;

                if(offhand.getItem() instanceof BlockItem block){
                    renderState = block.getBlock().defaultBlockState();
                }

                for(int i = 1; i<=range; ++i){

                    if(isValid(targetpos, level)){
                        renderBlock(renderState, targetpos, poseStack, source, tick);
                    }
                    targetpos = PlacementHelper.getEdgePlacement(targetpos, lookAngle, coordinates, mode, direction);
                }

            }

        }
    }

    private static void renderBlock(BlockState state, BlockPos pos, PoseStack stack, MultiBufferSource source, long tick){
        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        ClientLevel level = Minecraft.getInstance().level;
        IModelData data = renderer.getBlockModel(state).getModelData(level, pos, state, ModelDataManager.getModelData(level, pos));

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        stack.pushPose();
        int brightness = (int) ((200 * (Math.sin(0.2 * tick)+1)) / 2) + 55;
        int overlay = OverlayTexture.NO_OVERLAY;

        stack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

        renderer.renderSingleBlock(state, stack, source, brightness, overlay, data);
        stack.popPose();
    }

    public static boolean isValid(BlockPos pos, Level level){

        BlockState state = level.getBlockState(pos);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));

        return (state.isAir() || state.getBlock() instanceof LiquidBlock) && entities.isEmpty();

    }

}
