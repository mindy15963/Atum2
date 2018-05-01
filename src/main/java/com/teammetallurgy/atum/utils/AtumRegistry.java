package com.teammetallurgy.atum.utils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.blocks.IRenderMapper;
import com.teammetallurgy.atum.init.AtumBlocks;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class AtumRegistry {
    private static final NonNullList<EntityEntry> MOBS = NonNullList.create();
    private static final NonNullList<EntityEntry> ENTITIES = NonNullList.create();

    /**
     * Same as {@link AtumRegistry#registerItem(Item, String, CreativeTabs)}, but have CreativeTab set by default
     */
    public static Item registerItem(@Nonnull Item item, @Nonnull String name) {
        return registerItem(item, name, Atum.CREATIVE_TAB);
    }

    /**
     * Registers an item
     *
     * @param item The item to be registered
     * @param name The name to register the item with
     * @param tab  The creative tab for the item. Set to null for no CreativeTab
     * @return The Item that was registered
     */
    public static Item registerItem(@Nonnull Item item, @Nonnull String name, @Nullable CreativeTabs tab) {
        item.setRegistryName(new ResourceLocation(Constants.MOD_ID, AtumUtils.toRegistryName(name)));
        item.setUnlocalizedName(Constants.MOD_ID + "." + AtumUtils.toUnlocalizedName(name));
        ForgeRegistries.ITEMS.register(item);

        if (tab != null) {
            item.setCreativeTab(tab);
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, AtumUtils.toRegistryName(name)), "inventory"));
        }

        return item;
    }

    /**
     * Same as {@link AtumRegistry#registerBlock(Block, Item, String, CreativeTabs)}, but have a basic ItemBlock and CreativeTab set by default
     */
    public static Block registerBlock(@Nonnull Block block, @Nonnull String name) {
        return registerBlock(block, new ItemBlock(block), name, Atum.CREATIVE_TAB);
    }

    /**
     * Same as {@link AtumRegistry#registerBlock(Block, Item, String, CreativeTabs)}, but have a basic ItemBlock set by default
     */
    public static Block registerBlock(@Nonnull Block block, @Nonnull String name, @Nullable CreativeTabs tab) {
        return registerBlock(block, new ItemBlock(block), name, tab);
    }

    /**
     * Same as {@link AtumRegistry#registerBlock(Block, Item, String, CreativeTabs)}, but have CreativeTab set by default
     */
    public static Block registerBlock(@Nonnull Block block, @Nonnull Item itemBlock, @Nonnull String name) {
        return registerBlock(block, itemBlock, name, Atum.CREATIVE_TAB);
    }

    /**
     * Registers a block
     *
     * @param block The block to be registered
     * @param name  The name to register the block with
     * @param tab   The creative tab for the block. Set to null for no CreativeTab
     * @return The Block that was registered
     */
    public static Block registerBlock(@Nonnull Block block, @Nonnull Item itemBlock, @Nonnull String name, @Nullable CreativeTabs tab) {
        block.setRegistryName(new ResourceLocation(Constants.MOD_ID, AtumUtils.toRegistryName(name)));
        block.setUnlocalizedName(Constants.MOD_ID + "." + AtumUtils.toUnlocalizedName(name));
        ForgeRegistries.BLOCKS.register(block);
        registerItem(itemBlock, AtumUtils.toRegistryName(name));

        if (tab != null) {
            block.setCreativeTab(tab);
        }

        if (block instanceof IRenderMapper && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientProxy.ignoreRenderProperty(block);
        }

        return block;
    }

    /**
     * Registers any mob, that will have a spawn egg.
     *
     * @param entityClass The entity class
     * @return The EntityEntry that was registered
     */
    public static EntityEntry registerMob(@Nonnull Class<? extends Entity> entityClass, int eggPrimary, int eggSecondary) {
        ResourceLocation location = new ResourceLocation(Constants.MOD_ID, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", ""));
        EntityEntry entry = new EntityEntry(entityClass, location.toString());
        entry.setRegistryName(location);
        entry.setEgg(new EntityList.EntityEggInfo(location, eggPrimary, eggSecondary));
        MOBS.add(entry);

        return entry;
    }

    /**
     * Registers any kind of entity, that is not a mob.
     *
     * @param entityClass The entity class
     * @return The EntityEntry that was registered
     */
    public static EntityEntry registerEntity(@Nonnull Class<? extends Entity> entityClass) {
        ResourceLocation location = new ResourceLocation(Constants.MOD_ID, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", ""));
        EntityEntry entry = new EntityEntry(entityClass, location.toString());
        entry.setRegistryName(location);
        ENTITIES.add(entry);

        return entry;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        AtumItems.registerItems();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        AtumBlocks.registerBlocks();
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        int networkId = 0;
        for (EntityEntry entry : MOBS) {
            Preconditions.checkNotNull(entry.getRegistryName(), "registryName");
            networkId++;
            event.getRegistry().register(EntityEntryBuilder.create()
                    .entity(entry.getEntityClass())
                    .id(entry.getRegistryName(), networkId)
                    .name(AtumUtils.toUnlocalizedName(entry.getName()))
                    .tracker(64, 1, true)
                    .egg(entry.getEgg().primaryColor, entry.getEgg().secondaryColor)
                    .build());
        }
        for (EntityEntry entry : ENTITIES) {
            Preconditions.checkNotNull(entry.getRegistryName(), "registryName");
            networkId++;
            event.getRegistry().register(EntityEntryBuilder.create()
                    .entity(entry.getEntityClass())
                    .id(entry.getRegistryName(), networkId)
                    .name(AtumUtils.toUnlocalizedName(entry.getName()))
                    .build());
        }
    }
}