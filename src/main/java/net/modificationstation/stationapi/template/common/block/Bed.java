package net.modificationstation.stationapi.template.common.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.common.block.BlockRegistry;
import net.modificationstation.stationapi.api.common.registry.Identifier;

public class Bed extends net.minecraft.block.Bed implements IBlockTemplate<Bed> {

    public Bed(Identifier identifier) {
        this(BlockRegistry.INSTANCE.getNextSerializedID());
        BlockRegistry.INSTANCE.registerValue(identifier, this);
    }

    public Bed(int id) {
        super(id);
    }

    @Override
    public Bed disableNotifyOnMetaDataChange() {
        return (Bed) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public Bed setSounds(BlockSounds sounds) {
        return (Bed) super.setSounds(sounds);
    }

    @Override
    public Bed setLightOpacity(int i) {
        return (Bed) super.setLightOpacity(i);
    }

    @Override
    public Bed setLightEmittance(float f) {
        return (Bed) super.setLightEmittance(f);
    }

    @Override
    public Bed setBlastResistance(float resistance) {
        return (Bed) super.setBlastResistance(resistance);
    }

    @Override
    public Bed setHardness(float hardness) {
        return (Bed) super.setHardness(hardness);
    }

    @Override
    public Bed setUnbreakable() {
        return (Bed) super.setUnbreakable();
    }

    @Override
    public Bed setTicksRandomly(boolean ticksRandomly) {
        return (Bed) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public Bed setTranslationKey(String string) {
        return (Bed) super.setTranslationKey(string);
    }

    @Override
    public Bed disableStat() {
        return (Bed) super.disableStat();
    }
}
