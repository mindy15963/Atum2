package com.teammetallurgy.atum.items.artifacts.ra;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.init.AtumParticles;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Atum.MOD_ID)
public class LegsOfRaItem extends RaArmor {
    private static final AttributeModifier SPEED_BOOST = new AttributeModifier(UUID.fromString("2140f663-2112-497b-a5d7-36c40abb7a76"), "Legs of Ra speed boost", 0.02D, AttributeModifier.Operation.ADDITION);

    public LegsOfRaItem() {
        super("ra_armor", EquipmentSlotType.LEGS);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        World world = player.world;
        ModifiableAttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (player.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == AtumItems.LEGS_OF_RA) {
            if (player.isSprinting()) {
                if (player.world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) player.world;
                    serverWorld.spawnParticle(AtumParticles.RA_FIRE, player.getPosX() + (world.rand.nextDouble() - 0.5D) * (double) player.getWidth(), player.getPosY() + 0.15D, player.getPosZ() + (world.rand.nextDouble() - 0.5D) * (double) player.getWidth(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                if (!attribute.hasModifier(SPEED_BOOST)) {
                    attribute.applyNonPersistentModifier(SPEED_BOOST);
                } else {
                    attribute.removeModifier(SPEED_BOOST);
                }
            }
        } else if (attribute.hasModifier(SPEED_BOOST)) {
            attribute.removeModifier(SPEED_BOOST); //Fail safe, in case speed boost does somehow not get removed properly
        }
    }
}