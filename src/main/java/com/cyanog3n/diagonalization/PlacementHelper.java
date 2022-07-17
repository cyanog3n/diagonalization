package com.cyanog3n.diagonalization;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class PlacementHelper {

    public static BlockPos getEdgePlacement(BlockPos pos, Vec3 lookAngle, Vec3 coordinate, int mode, Direction direction) {

        //check if target point is on the top/bottom or on the sides of the block
        boolean usingView = coordinate.y == 1 || coordinate.y == 0;

        if(usingView){
            switch (mode) {
                case 1 -> pos = verticalEdgeView(pos, lookAngle);
                case 2 -> pos = horizontalEdgeView(pos, lookAngle, direction);
                case 3 -> pos = vertexView(pos, lookAngle);
            }
        }
        else{
            switch (mode) {
                case 1 -> pos = verticalEdgeCoord(pos, coordinate);
                case 2 -> pos = horizontalEdgeCoord(pos, coordinate);
                case 3 -> pos = vertexCoord(pos, coordinate);
            }
        }

        return pos;
    }

    //MODE 1 - VERTICAL EDGES

    public static BlockPos verticalEdgeCoord(BlockPos pos, Vec3 coordinate){

        //east +x
        //south +z
        double x = coordinate.x;
        double z = coordinate.z;

        if(x < 0.5 && z < 0.5){
            pos = pos.west().north();
        }
        else if(x > 0.5 && z < 0.5){
            pos = pos.east().north();
        }
        else if(x < 0.5 && z > 0.5){
            pos = pos.west().south();
        }
        else if(x > 0.5 && z > 0.5){
            pos = pos.east().south();
        }

        return pos;
    }

    public static BlockPos verticalEdgeView(BlockPos pos, Vec3 lookAngle){

        if(lookAngle.x > 0 && lookAngle.z > 0){
            pos = pos.east().south();
        }
        else if(lookAngle.x > 0 && lookAngle.z < 0){
            pos = pos.east().north();
        }
        else if(lookAngle.x < 0 && lookAngle.z > 0){
            pos = pos.west().south();
        }
        else if(lookAngle.x < 0 && lookAngle.z < 0){
            pos = pos.west().north();
        }

        return pos;
    }

    //MODE 2 - HORIZONTAL EDGES

    public static BlockPos horizontalEdgeCoord(BlockPos pos, Vec3 coordinate){

        double x = coordinate.x;
        double y = coordinate.y;
        double z = coordinate.z;

        if(y < 0.5){
            pos = pos.below();
        }
        else if(y > 0.5){
            pos = pos.above();
        }

        if(1 - x > z && z > x){ // A quadrant -> west
            pos = pos.west();
        }
        else if(1 - z < x && x < z){ //B quadrant -> south
            pos = pos.south();
        }
        else if(1 - x < z && z < x){ //C quadrant -> east
            pos = pos.east();
        }
        else if(1 - z > x && x > z){ //D quadrant -> north
            pos = pos.north();
        }

        return pos;
    }

    public static BlockPos horizontalEdgeView(BlockPos pos, Vec3 lookAngle, Direction lookDirection){

        if(lookAngle.y > 0){ //facing up
            pos = pos.below();
        }
        else{ //facing down
            pos = pos.above();
        }

        if(lookDirection == Direction.EAST){
            pos = pos.east();
        }
        else if(lookDirection == Direction.SOUTH){
            pos = pos.south();
        }
        else if(lookDirection == Direction.WEST){
            pos = pos.west();
        }
        else if(lookDirection == Direction.NORTH){
            pos = pos.north();
        }

        return pos;
    }

    //MODE 3 - VERTICES

    public static BlockPos vertexCoord(BlockPos pos, Vec3 coordinate){

        double x = coordinate.x;
        double y = coordinate.y;
        double z = coordinate.z;

        if(y < 0.5){
            pos = pos.below();
        }
        else if(y > 0.5){
            pos = pos.above();
        }

        if(x < 0.5 && z < 0.5){
            pos = pos.west().north();
        }
        else if(x > 0.5 && z < 0.5){
            pos = pos.east().north();
        }
        else if(x < 0.5 && z > 0.5){
            pos = pos.west().south();
        }
        else if(x > 0.5 && z > 0.5){
            pos = pos.east().south();
        }

        return pos;
    }

    public static BlockPos vertexView(BlockPos pos, Vec3 lookAngle){

        if(lookAngle.y > 0){ //facing up
            pos = pos.below();
        }
        else{ //facing down
            pos = pos.above();
        }

        if(lookAngle.x > 0 && lookAngle.z > 0){
            pos = pos.east().south();
        }
        else if(lookAngle.x > 0 && lookAngle.z < 0){
            pos = pos.east().north();
        }
        else if(lookAngle.x < 0 && lookAngle.z > 0){
            pos = pos.west().south();
        }
        else if(lookAngle.x < 0 && lookAngle.z < 0){
            pos = pos.west().north();
        }

        return pos;
    }

}
