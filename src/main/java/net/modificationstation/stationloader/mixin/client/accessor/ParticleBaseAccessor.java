package net.modificationstation.stationloader.mixin.client.accessor;

import net.minecraft.entity.ParticleBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleBase.class)
public interface ParticleBaseAccessor {

    @Accessor
    int getField_2635();
}
