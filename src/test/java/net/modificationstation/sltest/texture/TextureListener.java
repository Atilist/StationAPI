package net.modificationstation.sltest.texture;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.resource.ClientResourcesReloadEvent;

public class TextureListener {

    @EventListener
    public void registerTextures(ClientResourcesReloadEvent event) {

//        ExpandableAtlas terrain = Atlases.getTerrain();
//
//        TEST_BLOCK.get().texture = terrain.addTexture(of(MODID, "blocks/testBlock")).index;
//        TEST_ANIMATED_BLOCK.get().texture = terrain.addTexture(of(MODID, "blocks/testAnimatedBlock")).index;
//        FREEZER.get().texture = terrain.addTexture(of(MODID, "blocks/FreezerTop")).index;
//        FREEZER.get(BlockFreezer.class).sideTexture = terrain.addTexture(of(MODID, "blocks/FreezerSide")).index;
//
//        altarTextures[Direction.DOWN.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_bottom")).index;
//        altarTextures[Direction.UP.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_top")).index;
//        altarTextures[Direction.EAST.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_east")).index;
//        altarTextures[Direction.WEST.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_west")).index;
//        altarTextures[Direction.NORTH.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_north")).index;
//        altarTextures[Direction.SOUTH.ordinal()] = terrain.addTexture(of(MODID, "blocks/altar_south")).index;
//
//        ItemListener.testNBTItem.setTexture(of(MODID, "items/nbtItem"));
//        ItemListener.testItem.setTexture(of(MODID, "items/highres"));
//        ItemListener.testPickaxe.setAnimationBinder("/assets/sltest/stationapi/textures/items/testPickaxe.png", 1, of(MODID, "items/testItem"));
//        ItemListener.testPickaxe.setTexture(of(MODID, "items/testPickaxe"));

//        SquareAtlas.GUI_ITEMS.addAnimationBinder("/assets/sltest/textures/items/testPickaxe.png", 1, 0);

//        TEST_ATLAS = new ExpandableAtlas(of(SLTest.MODID, "test_atlas"));
//
//        TEST_ATLAS.addTexture(of(MODID, "items/testItem"));
//        TEST_ATLAS.addTexture(of(MODID, "blocks/testBlock"));
//        TEST_ATLAS.addTexture(of(MODID, "blocks/testAnimatedBlock"));
//        TEST_ATLAS.addTexture(of(MODID, "items/testPickaxe"));
//        TEST_ATLAS.addTexture(of(MODID, "items/nbtItem"));
//        TEST_ATLAS.addTexture(of(MODID, "blocks/FreezerTop"));
//        TEST_ATLAS.addTexture(of(MODID, "blocks/FreezerSide"));

//        farlandsBlockModel = JsonUnbakedModel.get(of(MODID, "farlandsBlock"));
//        testItemModel = JsonUnbakedModel.get(of(MODID, "item/testItem"));
    }

    public static final int[] altarTextures = new int[6];

//    public static ExpandableAtlas TEST_ATLAS;
//
//    public static JsonUnbakedModel farlandsBlockModel;
//    public static JsonUnbakedModel testItemModel;
}
