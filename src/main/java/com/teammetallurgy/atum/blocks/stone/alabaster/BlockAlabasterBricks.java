package com.teammetallurgy.atum.blocks.stone.alabaster;

import com.google.common.collect.Maps;
import com.teammetallurgy.atum.utils.AtumRegistry;
import com.teammetallurgy.atum.utils.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockAlabasterBricks extends Block {
    private static final Map<Type, BlockAlabasterBricks> BRICKS = Maps.newEnumMap(Type.class);

    private BlockAlabasterBricks() {
        super(Material.ROCK);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    @Nonnull
    public MaterialColor getMapColor(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return MaterialColor.QUARTZ;
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return 0;
    }

    public static void registerBricks() {
        for (Type type : Type.values()) {
            BlockAlabasterBricks brick = new BlockAlabasterBricks();
            BRICKS.put(type, brick);
            AtumRegistry.registerBlock(brick, "alabaster_brick_" + type.getName());
        }
    }

    public static BlockAlabasterBricks getBrick(Type type) {
        return BRICKS.get(type);
    }

    @Override
    public void getOreDictEntries() {
        if (this == getBrick(Type.POLISHED)) {
            OreDictHelper.add(this, "stoneAlabasterPolished");
        }
    }

    public enum Type implements IStringSerializable {
        SMOOTH("smooth"),
        POLISHED("polished"),
        CARVED("carved"),
        TILED("tiled"),
        PILLAR("pillar");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.name;
        }
    }
}