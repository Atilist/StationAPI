package net.modificationstation.stationapi.mixin.worldgen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.level.Level;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.level.dimension.DimensionData;
import net.modificationstation.stationapi.api.worldgen.BiomeAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class MixinLevel {
    @Inject(
        method = "<init>(Lnet/minecraft/level/dimension/DimensionData;Ljava/lang/String;JLnet/minecraft/level/dimension/Dimension;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/level/dimension/Dimension;initDimension(Lnet/minecraft/level/Level;)V",
            shift = Shift.BEFORE
        )
    )
    private void onInit(DimensionData data, String name, long seed, Dimension dimension, CallbackInfo info) {
        if (data.getLevelProperties() != null) {
            seed = data.getLevelProperties().getSeed();
        }
        BiomeAPI.init(Level.class.cast(this), seed);
    }
    
    @Inject(
        method = "<init>(Lnet/minecraft/level/dimension/DimensionData;Ljava/lang/String;Lnet/minecraft/level/dimension/Dimension;J)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/level/dimension/Dimension;initDimension(Lnet/minecraft/level/Level;)V",
            shift = Shift.BEFORE
        )
    )
    @Environment(value= EnvType.CLIENT)
    private void onInit(DimensionData data, String name, Dimension dimension, long seed, CallbackInfo info) {
        if (data.getLevelProperties() != null) {
            seed = data.getLevelProperties().getSeed();
        }
        BiomeAPI.init(Level.class.cast(this), seed);
    }
    
    @Inject(
        method = "<init>(Lnet/minecraft/level/Level;Lnet/minecraft/level/dimension/Dimension;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/level/dimension/Dimension;initDimension(Lnet/minecraft/level/Level;)V",
            shift = Shift.BEFORE
        )
    )
    @Environment(value= EnvType.CLIENT)
    private void onInit(Level level, Dimension dimension, CallbackInfo info) {
        BiomeAPI.init(level, level.getSeed());
    }
}