package com.teammetallurgy.atum.utils.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.teammetallurgy.atum.init.AtumBiomes;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.items.artifacts.atum.EyesOfAtumItem;
import com.teammetallurgy.atum.items.artifacts.nuit.NuitsVanishingItem;
import com.teammetallurgy.atum.utils.AtumConfig;
import com.teammetallurgy.atum.utils.Constants;
import com.teammetallurgy.atum.world.WorldProviderAtum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    private static final ResourceLocation MUMMY_BLUR_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/hud/mummyblur.png");
    private static final ResourceLocation SAND_BLUR_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/hud/sandstormwip.png");
    private static float intensity = 1;

    @SubscribeEvent
    public static void renderlast(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().player.dimension.getId() == AtumConfig.DIMENSION_ID) {
            if (Minecraft.getInstance().gameSettings.hideGUI) {
                renderSand(event.getPartialTicks(), 1, 2, 3, 4, 5, 6);
            } else {
                renderSand(event.getPartialTicks(), 1, 2, 3, 4, 5, 6);
            }
        }
    }

    /*@SubscribeEvent
    public static void renderSand(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != ElementType.ALL) return;

        if (Minecraft.getInstance().player.dimension == AtumConfig.DIMENSION_ID) {
            //renderSand(event.getPartialTicks(), 1); //TODO Keithy. Minor for later
        }
    }*/

    private static void renderSand(float partialTicks, int... layers) {
        float baseDarkness = AtumConfig.SAND_DARKNESS;
        float baseAlpha = AtumConfig.SAND_ALPHA;
        float eyesOfAtumAlpha = AtumConfig.SAND_EYES_ALPHA;
        Minecraft mc = Minecraft.getInstance();
        Dimension dimension = mc.player.world.getDimension();

        if (dimension instanceof WorldProviderAtum && mc.player.dimension.getId() == AtumConfig.DIMENSION_ID) {
            WorldProviderAtum atum = (WorldProviderAtum) dimension;
            float stormStrength = atum.stormStrength;

            if (stormStrength < 0.0001F) {
                return;
            }

            float light = mc.player.world.getSunBrightness(partialTicks);

            GlStateManager.pushMatrix();
            GlStateManager.pushTextureAttributes();

            //mc.entityRenderer.setupOverlayRendering();

            //GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0F, 0.0F, -2000.0F);

            GlStateManager.enableBlend();
            GlStateManager.disableDepthTest();
            GlStateManager.depthMask(false);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlphaTest();
            mc.getTextureManager().bindTexture(SAND_BLUR_TEXTURE);

            ClientPlayerEntity player = mc.player;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

            BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
            boolean sky = player.world.canBlockSeeSky(playerPos);
            if (!sky || player.world.getBiome(playerPos) == AtumBiomes.OASIS) {
                intensity -= 0.001f * partialTicks;
                intensity = Math.max(0, intensity);
            } else {
                intensity += 0.01f * partialTicks;
                intensity = Math.min(stormStrength, intensity);
            }

            for (int i : layers) {
                float scale = 0.2f / (float) i;
                float alpha = (float) Math.pow(intensity - baseAlpha, i) * intensity;

                // Make it easier to see
                ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
                if (helmet.getItem() instanceof EyesOfAtumItem) {
                    alpha *= eyesOfAtumAlpha;
                }

                GlStateManager.color4f(baseDarkness * light, baseDarkness * light, baseDarkness * light, alpha);
                double scaleX = 0.01f * mc.mainWindow.getScaledHeight() * scale * mc.mainWindow.getGuiScaleFactor();
                double scaleY = 0.01f * mc.mainWindow.getScaledWidth() * scale * mc.mainWindow.getGuiScaleFactor();
                float speed = 500f - i * 15;
                float movement = -(System.currentTimeMillis() % (int) speed) / speed;
                float yaw = 0.25f * (mc.player.rotationYaw % 360 / 360f) / scale;
                float pitch = 0.5f * (mc.player.rotationPitch % 360 / 360f) / scale;

                bufferbuilder.pos(0.0D, (double) mc.mainWindow.getScaledHeight(), 90.0D).tex(movement + yaw, 1.0D / scaleY + pitch).endVertex();
                bufferbuilder.pos(mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight(), 90.0D).tex(1.0D / scaleX + movement + yaw, 1.0D / scaleY + pitch).endVertex();
                bufferbuilder.pos(mc.mainWindow.getScaledWidth(), 0.0D, 90.0D).tex(1.0D / scaleX + movement + yaw, 0.0D + pitch).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 90.0D).tex(movement + yaw, 0.0D + pitch).endVertex();
            }
            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepthTest();
            GlStateManager.enableAlphaTest();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();

            GlStateManager.popAttributes();
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public static void renderFog(EntityViewRenderEvent.RenderFogEvent event) {
        float sandstormFog = AtumConfig.SANDSTORM_FOG;
        Dimension dimension = Minecraft.getInstance().player.world.dimension;
        Entity entity = event.getInfo().getRenderViewEntity();

        if (dimension instanceof WorldProviderAtum && entity.dimension.getId() == AtumConfig.DIMENSION_ID && AtumConfig.FOG_ENABLED) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            float fogDensity = 0.08F;

            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
                if (player.getPosition().getY() <= 60) {
                    fogDensity += (float) (62 - player.getPosition().getY()) * 0.005F;
                }
                if (helmet.getItem() instanceof EyesOfAtumItem) {
                    fogDensity = fogDensity / 3;
                }
                if (helmet.getItem() == AtumItems.WANDERER_HELMET || helmet.getItem() == AtumItems.DESERT_HELMET_IRON || helmet.getItem() == AtumItems.DESERT_HELMET_GOLD || helmet.getItem() == AtumItems.DESERT_HELMET_DIAMOND) {
                    fogDensity = fogDensity / 1.5F;
                }
                if (player.posY >= player.world.getSeaLevel() - 8) {
                    WorldProviderAtum providerAtum = (WorldProviderAtum) dimension;
                    fogDensity *= 1 + sandstormFog - (sandstormFog - sandstormFog * providerAtum.stormStrength);
                }
                GlStateManager.fogDensity(fogDensity);
            }
        }
    }

    @SubscribeEvent
    public static void onRender(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        Hand hand = player.getHeldItem(Hand.OFF_HAND).getItem() == AtumItems.NUITS_VANISHING ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack heldStack = player.getHeldItem(hand);
        /*if (NuitsVanishingItem.IS_BAUBLES_INSTALLED && NuitsVanishingItem.getAmulet(player).getItem() == AtumItems.NUITS_VANISHING) {
            heldStack = NuitsVanishingItem.getAmulet(player);
        }*/
        if (heldStack.getItem() == AtumItems.NUITS_VANISHING) {
            if (!NuitsVanishingItem.isPlayerMoving(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void renderMummyHelmet(RenderGameOverlayEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        Minecraft mc = Minecraft.getInstance();

        if (player != null && mc.gameSettings.thirdPersonView == 0 && event.getType() == RenderGameOverlayEvent.ElementType.HELMET && player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == AtumItems.MUMMY_HELMET) {
            int width = mc.mainWindow.getScaledWidth();
            int height = mc.mainWindow.getScaledHeight();

            GlStateManager.disableDepthTest();
            GlStateManager.depthMask(false);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableAlphaTest();
            mc.getTextureManager().bindTexture(MUMMY_BLUR_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, height, -90.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(width, height, -90.0D).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(width, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepthTest();
            GlStateManager.enableAlphaTest();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}