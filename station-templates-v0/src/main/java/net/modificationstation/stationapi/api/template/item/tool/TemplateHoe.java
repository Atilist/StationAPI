package net.modificationstation.stationapi.api.template.item.tool;

import net.minecraft.item.tool.ToolMaterial;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.template.item.ItemTemplate;

public class TemplateHoe extends net.minecraft.item.tool.Hoe implements ItemTemplate<TemplateHoe>, ToolTemplate {

    public TemplateHoe(Identifier identifier, ToolMaterial arg) {
        this(ItemRegistry.INSTANCE.getNextLegacyIdShifted(), arg);
        ItemTemplate.onConstructor(this, identifier);
    }

    public TemplateHoe(int i, ToolMaterial arg) {
        super(i, arg);
    }

    @Override
    public TemplateHoe setTexturePosition(int texturePosition) {
        return (TemplateHoe) super.setTexturePosition(texturePosition);
    }

    @Override
    public TemplateHoe setMaxStackSize(int newMaxStackSize) {
        return (TemplateHoe) super.setMaxStackSize(newMaxStackSize);
    }

    @Override
    public TemplateHoe setTexturePosition(int x, int y) {
        return (TemplateHoe) super.setTexturePosition(x, y);
    }

    @Override
    public TemplateHoe setHasSubItems(boolean hasSubItems) {
        return (TemplateHoe) super.setHasSubItems(hasSubItems);
    }

    @Override
    public TemplateHoe setDurability(int durability) {
        return (TemplateHoe) super.setDurability(durability);
    }

    @Override
    public TemplateHoe setRendered3d() {
        return (TemplateHoe) super.setRendered3d();
    }

    @Override
    public TemplateHoe setTranslationKey(String newName) {
        return (TemplateHoe) super.setTranslationKey(newName);
    }

    @Override
    public TemplateHoe setContainerItem(net.minecraft.item.ItemBase itemType) {
        return (TemplateHoe) super.setContainerItem(itemType);
    }
}
