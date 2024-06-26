package net.modificationstation.stationapi.mixin.world;

import net.minecraft.world.World;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.celestial.CelestialInitializer;
import net.modificationstation.stationapi.api.celestial.CelestialTimeManager;
import net.modificationstation.stationapi.api.event.celestial.CelestialRegisterEvent;
import net.modificationstation.stationapi.api.event.world.WorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
class WorldMixin {
    @Inject(
            method = {
                    "<init>(Lnet/minecraft/world/dimension/DimensionData;Ljava/lang/String;Lnet/minecraft/world/dimension/Dimension;J)V",
                    "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/dimension/Dimension;)V",
                    "<init>(Lnet/minecraft/world/dimension/DimensionData;Ljava/lang/String;JLnet/minecraft/world/dimension/Dimension;)V"
            },
            at = @At("RETURN")
    )
    private void stationapi_onCor1(CallbackInfo ci) {
        CelestialTimeManager.clearLists();
        CelestialInitializer.clearList();
        StationAPI.EVENT_BUS.post(WorldEvent.Init.builder().world(World.class.cast(this)).build());
        StationAPI.EVENT_BUS.post(CelestialRegisterEvent.builder().world(World.class.cast(this)).build());
        CelestialInitializer.initializeEvents();
    }
	
	@Inject(
            method = "method_195",
            at = @At("HEAD")
    )
	private void stationapi_onLevelSave(CallbackInfo ci) {
		StationAPI.EVENT_BUS.post(WorldEvent.Save.builder().world(World.class.cast(this)).build());
	}
}
