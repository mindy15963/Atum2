package com.teammetallurgy.atum.items.artifacts.horus;

import com.teammetallurgy.atum.api.God;
import com.teammetallurgy.atum.api.IArtifact;
import com.teammetallurgy.atum.entity.projectile.arrow.ArrowStraightEntity;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.items.tools.BaseBowItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class HorusSoaringItem extends BaseBowItem implements IArtifact {

    public HorusSoaringItem() {
        super(new Item.Properties().rarity(Rarity.RARE).maxDamage(650));
        this.setRepairItem(AtumItems.NEBU_INGOT);
    }

    @Override
    public God getGod() {
        return God.HORUS;
    }

    @Override
    protected ArrowEntity setArrow(@Nonnull ItemStack stack, World world, PlayerEntity player, float velocity) {
        return new ArrowStraightEntity(world, player, velocity);
    }

    @Override
    protected void onShoot(ArrowEntity arrow, PlayerEntity player, float velocity) {
        arrow.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 2.0F, 0.0F);
    }
}