package com.teammetallurgy.atum.blocks.machines.tileentity;

import com.teammetallurgy.atum.blocks.base.tileentity.InventoryBaseTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KilnBaseTileEntity extends InventoryBaseTileEntity implements ISidedInventory {
    private BlockPos primaryPos;
    private static final int[] SLOTS_TOP = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_BOTTOM = new int[]{5, 6, 7, 8};
    private static final int[] SLOTS_SIDES = new int[]{4};

    KilnBaseTileEntity(TileEntityType<?> tileType) {
        super(tileType, 9);
    }

    @Override
    @Nonnull
    public TileEntityType<?> getType() {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.getType();
            }
        }
        return super.getType();
    }

    public boolean isPrimary() {
        return primaryPos != null && primaryPos.equals(this.pos);
    }

    public void setPrimaryPos(BlockPos primaryPos) {
        this.primaryPos = primaryPos;
    }

    public BlockPos getPrimaryPos() {
        return primaryPos;
    }

    KilnBaseTileEntity getPrimary() {
        if (this.isPrimary()) {
            return this;
        }
        if (this.world != null && primaryPos != null) {
            TileEntity te = world.getTileEntity(primaryPos);
            if (te instanceof KilnBaseTileEntity) {
                return (KilnBaseTileEntity) te;
            }
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.isItemValidForSlot(index, stack);
            } else {
                return false;
            }
        }
        if (index >= 5 && index <= 9) {
            return false;
        } else if (index == 4) {
            return AbstractFurnaceTileEntity.isFuel(stack);
        } else {
            return true;
        }
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull Direction side) {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.getSlotsForFace(side);
            }
        }
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
        }
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, Direction side) {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.canInsertItem(index, stack, side);
            } else {
                return false;
            }
        }
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull Direction side) {
        return this.isPrimary() || this.getPrimary() != null && this.getPrimary().canExtractItem(index, stack, side);
    }

    @Override
    public int getSizeInventory() {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.getSizeInventory();
            }
        }
        return super.getSizeInventory();
    }

    @Override
    public int getInventoryStackLimit() {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.getInventoryStackLimit();
            }
        }
        return super.getInventoryStackLimit();
    }

    @Override
    public boolean isEmpty() {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.isEmpty();
            }
        }
        return super.isEmpty();
    }

    @Override
    public void read(@Nonnull CompoundNBT compound) {
        super.read(compound);
        boolean hasPrimary = compound.getBoolean("has_primary");
        if (hasPrimary) {
            int x = compound.getInt("px");
            int y = compound.getInt("py");
            int z = compound.getInt("pz");
            primaryPos = new BlockPos(x, y, z);
        }
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        if (primaryPos != null) {
            compound.putBoolean("has_primary", true);
            compound.putInt("px", primaryPos.getX());
            compound.putInt("py", primaryPos.getY());
            compound.putInt("pz", primaryPos.getZ());
        } else {
            compound.putBoolean("has_primary", false);
        }
        return compound;
    }

    @Override
    protected Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory) {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.createMenu(windowID, playerInventory);
            }
        }
        return null;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        super.onDataPacket(manager, packet);
        this.read(packet.getNbtCompound());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.WEST);

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (!isPrimary()) {
            KilnBaseTileEntity primary = getPrimary();
            if (primary != null) {
                return primary.getCapability(capability, facing);
            }
        }
        if (!this.removed && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP) {
                return handlers[0].cast();
            } else if (facing == Direction.DOWN) {
                return handlers[1].cast();
            } else {
                return handlers[2].cast();
            }
        }
        return super.getCapability(capability, facing);
    }
}