package net.modificationstation.stationloader.mixin.common.accessor;

import net.minecraft.entity.EntityEntry;
import net.minecraft.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Biome.class)
public interface BiomeAccessor {

    @Accessor
    List<EntityEntry> getMonsters();
    @Accessor
    List<EntityEntry> getCreatures();
    @Accessor
    List<EntityEntry> getWaterCreatures();
}
