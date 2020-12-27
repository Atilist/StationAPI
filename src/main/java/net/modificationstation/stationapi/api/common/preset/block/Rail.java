package net.modificationstation.stationapi.api.common.preset.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.common.block.BlockRegistry;
import net.modificationstation.stationapi.api.common.registry.Identifier;

public class Rail extends net.minecraft.block.Rail {

    public Rail(Identifier identifier, int j, boolean flag) {
        this(BlockRegistry.INSTANCE.getNextSerializedID(), j, flag);
        BlockRegistry.INSTANCE.registerValue(identifier, this);
    }

    public Rail(int i, int j, boolean flag) {
        super(i, j, flag);
    }

    @Override
    public Rail disableNotifyOnMetaDataChange() {
        return (Rail) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public Rail sounds(BlockSounds sounds) {
        return (Rail) super.sounds(sounds);
    }

    @Override
    public Rail setLightOpacity(int i) {
        return (Rail) super.setLightOpacity(i);
    }

    @Override
    public Rail setLightEmittance(float f) {
        return (Rail) super.setLightEmittance(f);
    }

    @Override
    public Rail setBlastResistance(float resistance) {
        return (Rail) super.setBlastResistance(resistance);
    }

    @Override
    public Rail setHardness(float hardness) {
        return (Rail) super.setHardness(hardness);
    }

    @Override
    public Rail setUnbreakable() {
        return (Rail) super.setUnbreakable();
    }

    @Override
    public Rail setTicksRandomly(boolean ticksRandomly) {
        return (Rail) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public Rail setName(String string) {
        return (Rail) super.setName(string);
    }

    @Override
    public Rail disableStat() {
        return (Rail) super.disableStat();
    }
}