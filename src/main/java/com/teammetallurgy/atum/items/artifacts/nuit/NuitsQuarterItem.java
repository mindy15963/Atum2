package com.teammetallurgy.atum.items.artifacts.nuit;

import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.init.AtumParticles;
import com.teammetallurgy.atum.items.tools.KhopeshItem;
import com.teammetallurgy.atum.utils.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class NuitsQuarterItem extends KhopeshItem {
    private boolean isOffhand = false;
    private static boolean isBlocking = false;

    public NuitsQuarterItem() {
        super(ItemTier.DIAMOND, new Item.Properties().rarity(Rarity.RARE));
        this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            public float call(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return isOffhand ? 72000 : 0;
    }

    @Override
    public boolean isShield(@Nonnull ItemStack stack, @Nullable LivingEntity entity) {
        return isOffhand;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        if (hand == Hand.OFF_HAND) {
            player.setActiveHand(Hand.OFF_HAND);
            this.isOffhand = true;
            return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(Hand.OFF_HAND));
        }
        this.isOffhand = false;
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (random.nextFloat() <= 0.25F) {
            applyWeakness(target, attacker,attacker.getHeldItemOffhand().getItem() == AtumItems.NUITS_IRE);
        }
        return super.hitEntity(stack, target, attacker);
    }

    @SubscribeEvent
    public static void onUse(LivingEntityUseItemEvent.Tick event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity && entity.getHeldItem(Hand.OFF_HAND).getItem() == AtumItems.NUITS_QUARTER) {
            isBlocking = true;
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        Entity trueSource = event.getSource().getImmediateSource();
        if (trueSource instanceof LivingEntity && event.getEntityLiving() instanceof PlayerEntity && isBlocking && random.nextFloat() <= 0.25F) {
            applyWeakness((LivingEntity) trueSource, event.getEntityLiving(), event.getEntityLiving().getHeldItemMainhand().getItem() == AtumItems.NUITS_IRE);
            isBlocking = false;
        }
    }

    private static void applyWeakness(LivingEntity attacker, LivingEntity target, boolean isNuitsIreHeld) {
        if (attacker != target) {
            for (int l = 0; l < 8; ++l) {
                target.world.addParticle(AtumParticles.NUIT_BLACK, target.posX + (random.nextDouble() - 0.5D) * (double) target.getWidth(), target.posY + random.nextDouble() * (double) target.getHeight(), target.posZ + (random.nextDouble() - 0.5D) * (double) target.getWidth(), 0.0D, 0.0D, 0.0D);
            }
            attacker.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 60, isNuitsIreHeld ? 2 : 1));
        }
    }
}