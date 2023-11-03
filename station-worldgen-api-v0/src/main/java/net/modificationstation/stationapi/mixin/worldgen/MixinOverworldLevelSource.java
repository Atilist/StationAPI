package net.modificationstation.stationapi.mixin.worldgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Sand;
import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.source.LevelSource;
import net.minecraft.level.source.OverworldLevelSource;
import net.modificationstation.stationapi.impl.worldgen.WorldDecoratorImpl;
import net.modificationstation.stationapi.impl.worldgen.WorldGeneratorImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverworldLevelSource.class)
public class MixinOverworldLevelSource {
    @Shadow
    private Level level;
    @Shadow
    private double[] noises;

    @Inject(
            method = "decorate",
            at = @At("HEAD")
    )
    private void decorateSurface(LevelSource source, int cx, int cz, CallbackInfo info) {
        WorldDecoratorImpl.decorate(this.level, cx, cz);
    }
    
    @Inject(
        method = "decorate",
        at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V", ordinal = 0, shift = Shift.BEFORE),
        cancellable = true
    )
    private void cancelStructureGeneration(LevelSource source, int cx, int cz, CallbackInfo info, @Local Biome biome) {
        if (biome.isNoDimensionStrucutres()) {
            Sand.fallInstantly = false;
            info.cancel();
        }
    }

    @ModifyConstant(
            method = "buildSurface",
            constant = @Constant(intValue = 127)
    )
    private int cancelSurfaceMaking(int constant, @Local Biome biome) {
        return biome.noSurfaceRules() ? level.getTopY() - 1 : -1;
    }

    @Inject(
            method = "shapeChunk",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/level/source/OverworldLevelSource;calculateNoise([DIIIIII)[D",
                    shift = Shift.AFTER
            )
    )
    private void changeHeight(int cx, int cz, byte[] args, Biome[] biomes, double[] par5, CallbackInfo info) {
        WorldGeneratorImpl.updateNoise(level, cx, cz, this.noises);
    }
}