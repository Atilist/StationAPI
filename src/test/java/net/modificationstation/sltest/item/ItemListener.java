package net.modificationstation.sltest.item;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.modificationstation.sltest.block.Blocks;
import net.modificationstation.sltest.block.VariationBlock;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.item.tool.MiningLevelManager;
import net.modificationstation.stationapi.api.item.tool.ToolMaterialFactory;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.template.item.BlockStateItem;

import static net.modificationstation.sltest.SLTest.NAMESPACE;

public class ItemListener {

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        MiningLevelManager.LevelNode moddedNode = new MiningLevelManager.LevelNode(TagKey.of(BlockRegistry.KEY, NAMESPACE.id("needs_tool_level_modded")));
        MiningLevelManager.GRAPH.putEdge(ToolMaterial.STONE.getMiningLevelNode(), moddedNode);
        MiningLevelManager.GRAPH.putEdge(moddedNode, ToolMaterial.IRON.getMiningLevelNode());
        MiningLevelManager.invalidateCache();

        testItem = new ModdedItem(NAMESPACE.id("test_item")).setTranslationKey(NAMESPACE, "testItem"); //8475
        testMaterial = ToolMaterialFactory.create("testMaterial", 3, Integer.MAX_VALUE, Float.MAX_VALUE, Integer.MAX_VALUE - 2).miningLevelNode(moddedNode);
        testPickaxe = new ModdedPickaxeItem(NAMESPACE.id("test_pickaxe"), testMaterial).setTranslationKey(NAMESPACE, "testPickaxe"); //8476
        testNBTItem = new NBTItem(NAMESPACE.id("nbt_item")).setTranslationKey(NAMESPACE, "nbt_item"); //8477
        testModelItem = new ModelItem(NAMESPACE.id("model_item")).setMaxCount(1).setTranslationKey(NAMESPACE, "idkSomething");
        ironOre = Registry.register(event.registry, NAMESPACE.id("ironOre"), new Item(ItemRegistry.AUTO_ID)).setTranslationKey(NAMESPACE.id("ironOre"));
        generatedItem = Registry.register(event.registry, NAMESPACE.id("generated_item"), new Item(ItemRegistry.AUTO_ID)).setTranslationKey(NAMESPACE.id("generatedItem"));
        variationBlockIdle = new BlockStateItem(NAMESPACE.id("variation_block_idle"), Blocks.VARIATION_BLOCK.get().getDefaultState()).setTranslationKey(NAMESPACE, "variationBlockIdle");
        variationBlockPassive = new BlockStateItem(NAMESPACE.id("variation_block_passive"), Blocks.VARIATION_BLOCK.get().getDefaultState().with(VariationBlock.VARIANT, VariationBlock.Variant.PASSIVE)).setTranslationKey(NAMESPACE, "variationBlockPassive");
        variationBlockActive = new BlockStateItem(NAMESPACE.id("variation_block_active"), Blocks.VARIATION_BLOCK.get().getDefaultState().with(VariationBlock.VARIANT, VariationBlock.Variant.ACTIVE)).setTranslationKey(NAMESPACE, "variationBlockActive");
        testShears = new TestShearsItem(NAMESPACE.id("test_shears")).setTranslationKey(NAMESPACE, "test_shears");
        pacifistSword = new PacifistSwordItem(NAMESPACE.id("pacifist_sword")).setTranslationKey(NAMESPACE, "pacifist_sword");
        dullPickaxe = new DullPickaxeItem(NAMESPACE.id("dull_pickaxe")).setTranslationKey(NAMESPACE, "dull_pickaxe");
        celestialTestItem = new CelestialTestItem(NAMESPACE.id("test_celestial")).setTranslationKey(NAMESPACE, "test_celestial");
        celestialToggleItem = new CelestialToggleItem(NAMESPACE.id("toggle_celestial")).setTranslationKey(NAMESPACE, "toggle_celestial");
    }

    public static Item testItem;
    public static ToolMaterial testMaterial;
    public static Item testPickaxe;
    public static Item testNBTItem;
    public static Item testModelItem;
    public static Item ironOre;
    public static Item generatedItem;
    public static Item variationBlockIdle;
    public static Item variationBlockPassive;
    public static Item variationBlockActive;
    public static Item testShears;
    public static Item pacifistSword;
    public static Item dullPickaxe;
    public static Item celestialTestItem;
    public static Item celestialToggleItem;
}
