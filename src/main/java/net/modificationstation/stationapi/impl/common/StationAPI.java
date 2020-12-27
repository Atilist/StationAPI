package net.modificationstation.stationapi.impl.common;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.tool.ToolMaterial;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.common.block.EffectiveForTool;
import net.modificationstation.stationapi.api.common.event.EventRegistry;
import net.modificationstation.stationapi.api.common.event.GameEvent;
import net.modificationstation.stationapi.api.common.event.ModEvent;
import net.modificationstation.stationapi.api.common.event.achievement.AchievementRegister;
import net.modificationstation.stationapi.api.common.event.block.BlockNameSet;
import net.modificationstation.stationapi.api.common.event.block.BlockRegister;
import net.modificationstation.stationapi.api.common.event.block.TileEntityRegister;
import net.modificationstation.stationapi.api.common.event.container.slot.ItemUsedInCrafting;
import net.modificationstation.stationapi.api.common.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.common.event.entity.player.PlayerHandlerRegister;
import net.modificationstation.stationapi.api.common.event.item.ItemCreation;
import net.modificationstation.stationapi.api.common.event.item.ItemNameSet;
import net.modificationstation.stationapi.api.common.event.item.ItemRegister;
import net.modificationstation.stationapi.api.common.event.item.tool.EffectiveBlocksProvider;
import net.modificationstation.stationapi.api.common.event.item.tool.OverrideIsEffectiveOn;
import net.modificationstation.stationapi.api.common.event.level.LevelInit;
import net.modificationstation.stationapi.api.common.event.level.LoadLevelProperties;
import net.modificationstation.stationapi.api.common.event.level.LoadLevelPropertiesOnLevelInit;
import net.modificationstation.stationapi.api.common.event.level.SaveLevelProperties;
import net.modificationstation.stationapi.api.common.event.level.biome.BiomeByClimateProvider;
import net.modificationstation.stationapi.api.common.event.level.biome.BiomeRegister;
import net.modificationstation.stationapi.api.common.event.level.gen.ChunkPopulator;
import net.modificationstation.stationapi.api.common.event.mod.Init;
import net.modificationstation.stationapi.api.common.event.mod.PostInit;
import net.modificationstation.stationapi.api.common.event.mod.PreInit;
import net.modificationstation.stationapi.api.common.event.packet.MessageListenerRegister;
import net.modificationstation.stationapi.api.common.event.packet.PacketRegister;
import net.modificationstation.stationapi.api.common.event.recipe.RecipeRegister;
import net.modificationstation.stationapi.api.common.mod.StationMod;
import net.modificationstation.stationapi.api.common.mod.entrypoint.Instance;
import net.modificationstation.stationapi.api.common.mod.entrypoint.ModIDField;
import net.modificationstation.stationapi.api.common.packet.Message;
import net.modificationstation.stationapi.api.common.packet.MessageListenerRegistry;
import net.modificationstation.stationapi.api.common.registry.Identifier;
import net.modificationstation.stationapi.api.common.registry.LevelRegistry;
import net.modificationstation.stationapi.api.common.registry.ModID;
import net.modificationstation.stationapi.api.common.registry.Registry;
import net.modificationstation.stationapi.api.common.resource.RecursiveReader;
import net.modificationstation.stationapi.impl.common.achievement.AchievementPage;
import net.modificationstation.stationapi.impl.common.achievement.AchievementPageManager;
import net.modificationstation.stationapi.impl.common.block.BlockManager;
import net.modificationstation.stationapi.impl.common.config.Category;
import net.modificationstation.stationapi.impl.common.config.Configuration;
import net.modificationstation.stationapi.impl.common.config.Property;
import net.modificationstation.stationapi.impl.common.factory.EnumFactory;
import net.modificationstation.stationapi.impl.common.factory.GeneralFactory;
import net.modificationstation.stationapi.impl.common.item.CustomReach;
import net.modificationstation.stationapi.impl.common.lang.I18n;
import net.modificationstation.stationapi.impl.common.preset.item.PlaceableTileEntityWithMeta;
import net.modificationstation.stationapi.impl.common.preset.item.PlaceableTileEntityWithMetaAndName;
import net.modificationstation.stationapi.impl.common.recipe.CraftingRegistry;
import net.modificationstation.stationapi.impl.common.recipe.RecipeManager;
import net.modificationstation.stationapi.impl.common.recipe.SmeltingRegistry;
import net.modificationstation.stationapi.impl.common.util.ReflectionHelper;
import net.modificationstation.stationapi.impl.common.util.UnsafeProvider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StationAPI implements net.modificationstation.stationapi.api.common.StationAPI, PreInit, Init {

    protected final Set<String> entrypoints = new HashSet<>();
    private final Set<ModContainer> modsToVerifyOnClient = new HashSet<>();

    @Override
    public void setup() {
        ModID modID = getModID();
        entrypoints.add(modID + ":mod");
        String sideName = FabricLoader.getInstance().getEnvironmentType().name().toLowerCase();
        entrypoints.add(modID + ":mod_" + sideName);
        String name = modID.getName();
        setLogger(LogManager.getFormatterLogger(name + "|API"));
        Configurator.setLevel("mixin", Level.TRACE);
        Configurator.setLevel("Fabric|Loader", Level.INFO);
        Configurator.setLevel(name + "|API", Level.INFO);
        getLogger().info("Initializing " + name + "...");
        PreInit.EVENT.register(this, modID);
        Init.EVENT.register(this, modID);
        getLogger().info("Setting up API...");
        setupAPI();
        getLogger().info("Setting up lang folder...");
        net.modificationstation.stationapi.api.common.lang.I18n.INSTANCE.addLangFolder("/assets/" + modID + "/lang", modID);
        getLogger().info("Loading mods...");
        loadMods();
        getLogger().info("Finished " + name + " setup");
    }

    public void setupAPI() {
        getLogger().info("Setting up GeneralFactory...");
        net.modificationstation.stationapi.api.common.factory.GeneralFactory generalFactory = net.modificationstation.stationapi.api.common.factory.GeneralFactory.INSTANCE;
        generalFactory.setHandler(new GeneralFactory());
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.config.Configuration.class, args -> new Configuration((File) args[0]));
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.config.Category.class, args -> new Category((String) args[0]));
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.config.Property.class, args -> new Property((String) args[0]));
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.achievement.AchievementPage.class, args -> new AchievementPage((String) args[0]));
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.preset.item.PlaceableTileEntityWithMeta.class, args -> new PlaceableTileEntityWithMeta((int) args[0]));
        generalFactory.addFactory(net.modificationstation.stationapi.api.common.preset.item.PlaceableTileEntityWithMetaAndName.class, args -> new PlaceableTileEntityWithMetaAndName((int) args[0]));
        net.modificationstation.stationapi.api.common.factory.EnumFactory enumFactory = net.modificationstation.stationapi.api.common.factory.EnumFactory.INSTANCE;
        generalFactory.addFactory(ToolMaterial.class, args -> enumFactory.addEnum(ToolMaterial.class, (String) args[0], new Class[]{int.class, int.class, float.class, int.class}, new Object[]{args[1], args[2], args[3], args[4]}));
        generalFactory.addFactory(EntityType.class, args -> enumFactory.addEnum(EntityType.class, (String) args[0], new Class[]{Class.class, int.class, Material.class, boolean.class}, new Object[]{args[1], args[2], args[3], args[4]}));
        getLogger().info("Loading config...");
        net.modificationstation.stationapi.api.common.config.Configuration config = getDefaultConfig();
        config.load();
        getLogger().info("Setting up EnumFactory...");
        enumFactory.setHandler(new EnumFactory());
        getLogger().info("Setting up I18n...");
        net.modificationstation.stationapi.api.common.lang.I18n.INSTANCE.setHandler(new I18n());
        getLogger().info("Setting up BlockManager...");
        net.modificationstation.stationapi.api.common.block.BlockManager.INSTANCE.setHandler(new BlockManager());
        getLogger().info("Setting up RecipeManager...");
        RecipeManager recipeManager = new RecipeManager();
        net.modificationstation.stationapi.api.common.recipe.RecipeManager.INSTANCE.setHandler(recipeManager);
        RecipeRegister.EVENT.register(recipeManager);
        getLogger().info("Setting up CraftingRegistry...");
        net.modificationstation.stationapi.api.common.recipe.CraftingRegistry.INSTANCE.setHandler(new CraftingRegistry());
        getLogger().info("Setting up UnsafeProvider...");
        net.modificationstation.stationapi.api.common.util.UnsafeProvider.INSTANCE.setHandler(new UnsafeProvider());
        getLogger().info("Setting up SmeltingRegistry...");
        SmeltingRegistry smeltingRegistry = new SmeltingRegistry();
        net.modificationstation.stationapi.api.common.recipe.SmeltingRegistry.INSTANCE.setHandler(smeltingRegistry);
        getLogger().info("Setting up CustomReach...");
        net.modificationstation.stationapi.api.common.item.CustomReach.CONSUMERS.put("setDefaultBlockReach", CustomReach::setDefaultBlockReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.CONSUMERS.put("setHandBlockReach", CustomReach::setHandBlockReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.CONSUMERS.put("setDefaultEntityReach", CustomReach::setDefaultEntityReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.CONSUMERS.put("setHandEntityReach", CustomReach::setHandEntityReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.SUPPLIERS.put("getDefaultBlockReach", CustomReach::getDefaultBlockReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.SUPPLIERS.put("getHandBlockReach", CustomReach::getHandBlockReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.SUPPLIERS.put("getDefaultEntityReach", CustomReach::getDefaultEntityReach);
        net.modificationstation.stationapi.api.common.item.CustomReach.SUPPLIERS.put("getHandEntityReach", CustomReach::getHandEntityReach);
        getLogger().info("Setting up AchievementPageManager...");
        net.modificationstation.stationapi.api.common.achievement.AchievementPageManager.INSTANCE.setHandler(new AchievementPageManager());
        getLogger().info("Setting up CustomData packet...");
        net.modificationstation.stationapi.api.common.config.Category networkConfig = config.getCategory("Network");
        PacketRegister.EVENT.register(register -> {
            register.accept(networkConfig.getProperty("PacketCustomDataID", 254).getIntValue(), true, true, Message.class);
            config.save();
            MessageListenerRegister.EVENT.getInvoker().registerMessageListeners(MessageListenerRegistry.INSTANCE, MessageListenerRegister.EVENT.getListenerModID(MessageListenerRegister.EVENT.getInvoker()));
        });
        getLogger().info("Setting up BlockNameSet...");
        BlockNameSet.EVENT.register((block, name) -> {
            ModEvent<BlockRegister> event = BlockRegister.EVENT;
            if (event.getCurrentListener() != null) {
                ModID modID = event.getCurrentListenerModID();
                if (modID != null) {
                    String modid = modID + ":";
                    if (!name.startsWith(modid) && !name.contains(":"))
                        return modid + name;
                }
            }
            return name;
        });
        getLogger().info("Setting up ItemNameSet...");
        ItemNameSet.EVENT.register((item, name) -> {
            ModEvent<ItemRegister> event = ItemRegister.EVENT;
            if (event.getCurrentListener() != null) {
                ModID modID = event.getCurrentListenerModID();
                if (modID != null) {
                    String modid = modID + ":";
                    if (!name.startsWith(modid) && !name.contains(":"))
                        return modid + name;
                }
            }
            return name;
        });
        getLogger().info("Setting up IsEffectiveOn...");
        OverrideIsEffectiveOn.EVENT.register((toolLevel, arg, meta, effective) -> {
            if (arg instanceof EffectiveForTool)
                effective = ((EffectiveForTool) arg).isEffectiveFor(toolLevel, meta);
            return effective;
        });
        getLogger().info("Setting up TileEntityRegister...");
        TileEntityRegister.EVENT.register(smeltingRegistry);
        getLogger().info("Setting up LoadLevelPropertiesOnLevelInit...");
        LoadLevelPropertiesOnLevelInit.EVENT.register((levelProperties, tag) -> {
            Registry<Registry<?>> registriesRegistry = Registry.REGISTRIES;
            CompoundTag registriesTag = tag.getCompoundTag(registriesRegistry.getRegistryId().toString());
            registriesRegistry.forEach((identifier, registry) -> {
                if (registry instanceof LevelRegistry)
                    ((LevelRegistry<?>) registry).load(registriesTag.getCompoundTag(registry.getRegistryId().toString()));
            });
        });
        getLogger().info("Setting up SaveLevelProperties...");
        SaveLevelProperties.EVENT.register((levelProperties, tag, spPlayerData) -> {
            Registry<Registry<?>> registriesRegistry = Registry.REGISTRIES;
            CompoundTag registriesTag = new CompoundTag();
            registriesRegistry.forEach((identifier, registry) -> {
                if (registry instanceof LevelRegistry) {
                    CompoundTag registryTag = new CompoundTag();
                    ((LevelRegistry<?>) registry).save(registryTag);
                    registriesTag.put(identifier.toString(), registryTag);
                }
            });
            tag.put(registriesRegistry.getRegistryId().toString(), registriesTag);
        });
    }

    @Override
    public void loadMods() {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        getLogger().info("Loading entrypoints...");
        entrypoints.forEach(entrypoint -> fabricLoader.getEntrypointContainers(entrypoint, StationMod.class).forEach(this::addMod));
        fabricLoader.getEntrypointContainers(Identifier.of(getModID(), "game_event_bus").toString(), Object.class).forEach(entrypointContainer -> {
            ModContainer modContainer = entrypointContainer.getProvider();
            Object o = entrypointContainer.getEntrypoint();
            GameEvent.EVENT_BUS.register(o);
            setupAnnotations(modContainer, o);
        });
        fabricLoader.getEntrypointContainers(Identifier.of(getModID(), "mod_event_bus").toString(), Object.class).forEach(entrypointContainer -> {
            ModContainer modContainer = entrypointContainer.getProvider();
            Object o = entrypointContainer.getEntrypoint();
            ModEvent.getEventBus(ModID.of(modContainer)).register(o);
            setupAnnotations(modContainer, o);
        });
        getLogger().info("Loading assets...");
        FabricLoader.getInstance().getAllMods().forEach(this::addModAssets);
        getLogger().info("Gathering mods that require client verification...");
        String value = getModID() + ":verify_client";
        fabricLoader.getAllMods().forEach(modContainer -> {
            ModMetadata modMetadata = modContainer.getMetadata();
            if (modMetadata.containsCustomValue(value) && modMetadata.getCustomValue(value).getAsBoolean())
                modsToVerifyOnClient.add(modContainer);
        });
        getLogger().info("Invoking preInit event...");
        PreInit.EVENT.getInvoker().preInit(EventRegistry.INSTANCE, PreInit.EVENT.getListenerModID(PreInit.EVENT.getInvoker()));
        getLogger().info("Invoking init event...");
        Init.EVENT.getInvoker().init();
        getLogger().info("Invoking postInit event...");
        PostInit.EVENT.getInvoker().postInit();
    }

    @Override
    public void preInit(EventRegistry eventRegistry, ModID modID) {
        eventRegistry.registerValue(Identifier.of(modID, "achievement_register"), AchievementRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "block_name_set"), BlockNameSet.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "block_register"), BlockRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "tile_entity_register"), TileEntityRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "item_used_in_crafting"), ItemUsedInCrafting.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "player_handler_register"), PlayerHandlerRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "entity_register"), EntityRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "effective_blocks_provider"), EffectiveBlocksProvider.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "is_effective_on"), OverrideIsEffectiveOn.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "item_creation"), ItemCreation.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "item_name_set"), ItemNameSet.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "item_register"), ItemRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "biome_by_climate_provider"), BiomeByClimateProvider.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "biome_register"), BiomeRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "chunk_populator"), ChunkPopulator.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "packet_register"), PacketRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "recipe_register"), RecipeRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "level_init"), LevelInit.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "message_listener_register"), MessageListenerRegister.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "load_level_properties"), LoadLevelProperties.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "save_level_properties"), SaveLevelProperties.EVENT);
        eventRegistry.registerValue(Identifier.of(modID, "load_level_properties_on_level_init"), LoadLevelPropertiesOnLevelInit.EVENT);
    }

    @Override
    public void init() {
        EventRegistry.INSTANCE.forEach((identifier, event) -> event.register(identifier));
    }

    @Override
    public void addMod(EntrypointContainer<StationMod> stationModEntrypointContainer) {
        ModContainer modContainer = stationModEntrypointContainer.getProvider();
        StationMod stationMod = stationModEntrypointContainer.getEntrypoint();
        ModID modID = ModID.of(modContainer);
        stationMod.setModID(modID);
        getLogger().info("Set mod's container");
        if (stationMod instanceof PreInit)
            PreInit.EVENT.register((PreInit) stationMod, modID);
        Init.EVENT.register(stationMod, modID);
        if (stationMod instanceof PostInit)
            PostInit.EVENT.register((PostInit) stationMod, modID);
        getLogger().info("Registered events");
        setupAnnotations(modContainer, stationMod);
        getLogger().info(String.format("Done loading %s (%s)'s \"%s\" StationMod", modID.getName(), modID, stationMod.getClass().getName()));
    }

    @Override
    public void addModAssets(ModContainer modContainer) {
        ModID modID = ModID.of(modContainer);
        String stationSubFolder = "/assets/" + modID + "/" + getModID();
        String pathName = stationSubFolder + "/lang";
        URL path = getClass().getResource(pathName);
        if (path != null) {
            net.modificationstation.stationapi.api.common.lang.I18n.INSTANCE.addLangFolder(pathName, modID);
            getLogger().info("Registered lang path");
        }
        pathName = stationSubFolder + "/recipes";
        path = getClass().getResource(pathName);
        if (path != null) {
            try {
                for (URL url : new RecursiveReader(pathName, (file) -> file.endsWith(".json")).read())
                    net.modificationstation.stationapi.api.common.recipe.RecipeManager.INSTANCE.addJsonRecipe(url);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            getLogger().info("Listed recipes");
        }
    }

    public static void setupAnnotations(ModContainer modContainer, Object o) {
        try {
            ReflectionHelper.setFinalFieldsWithAnnotation(o, Instance.class, o);
            ReflectionHelper.setFinalFieldsWithAnnotation(o, ModIDField.class, modIDField -> modIDField.value().isEmpty() ? ModID.of(modContainer) : ModID.of(modIDField.value()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<ModContainer> getModsToVerifyOnClient() {
        return Collections.unmodifiableSet(modsToVerifyOnClient);
    }
}