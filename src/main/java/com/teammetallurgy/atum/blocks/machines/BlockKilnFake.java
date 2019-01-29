package com.teammetallurgy.atum.blocks.machines;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.blocks.base.IRenderMapper;
import com.teammetallurgy.atum.blocks.machines.tileentity.TileEntityKiln;
import com.teammetallurgy.atum.init.AtumBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockKilnFake extends BlockContainer implements IRenderMapper {
    public static final PropertyBool UP = PropertyBool.create("up");

    public BlockKilnFake() {
        super(Material.ROCK, MapColor.SAND);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(UP, false));
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityKiln();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        BlockPos tepos = getPrimaryKilnBlock(world, pos, state);
        if (tepos != null) {
            TileEntity tileEntity = world.getTileEntity(tepos);
            if (tileEntity instanceof TileEntityKiln) {
                player.openGui(Atum.instance, 5, world, tepos.getX(), tepos.getY(), tepos.getZ());
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        BlockPos primaryPos = this.getPrimaryKilnBlock(world, pos, state);
        if (primaryPos != null) {
            IBlockState primaryState = world.getBlockState(primaryPos);
            ((BlockKiln) AtumBlocks.KILN).destroyMultiblock(world, primaryPos, primaryState.getValue(BlockKiln.FACING));
        }
    }

    public BlockPos getPrimaryKilnBlock(World world, BlockPos pos, IBlockState state) {
        BlockPos primaryPos = pos;
        if (state.getValue(UP))
            primaryPos = primaryPos.offset(EnumFacing.UP);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IBlockState primaryState = world.getBlockState(primaryPos.add(dx, 0, dz));
                if (primaryState.getBlock() == AtumBlocks.KILN && primaryState.getValue(BlockKiln.MULTIBLOCK_PRIMARY))
                    return primaryPos.add(dx, 0, dz);
            }
        }
        return null;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return new ItemStack(AtumBlocks.KILN);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(UP, (meta & 0b001) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(UP)) {
            meta |= 0b001;
        }
        return meta;
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, UP);
    }

    @Override
    public IProperty[] getNonRenderingProperties() {
        return new IProperty[]{UP};
    }
}