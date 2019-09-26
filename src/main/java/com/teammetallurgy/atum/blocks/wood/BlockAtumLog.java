package com.teammetallurgy.atum.blocks.wood;

import com.teammetallurgy.atum.utils.OreDictHelper;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockAtumLog extends LogBlock {

    public BlockAtumLog() {
        super();
        this.setDefaultState(this.blockState.getBaseState().with(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    @Nonnull
    public MaterialColor getMapColor(BlockState state, IBlockReader blockAccess, BlockPos blockPos) {
        return BlockAtumPlank.WoodType.PALM.getMapColor();
    }

    @Override
    @Nonnull
    public BlockState getStateFromMeta(int meta) {
        BlockState state = this.getDefaultState();

        switch (meta & 12) {
            case 0:
                state = state.with(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                state = state.with(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                state = state.with(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                state = state.with(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }
        return state;
    }

    @Override
    public int getMetaFromState(BlockState state) {
        int i = 0;

        switch (state.getValue(LOG_AXIS)) {
            case X:
                i |= 4;
                break;
            case Y:
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
        }
        return i;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public void getOreDictEntries() {
        OreDictHelper.add(this, "log", Objects.requireNonNull(this.getRegistryName()).getPath().replace("_log", ""));
        OreDictHelper.add(this, "logWood");
    }
}