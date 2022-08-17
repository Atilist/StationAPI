package net.modificationstation.stationapi.mixin.item;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.item.ItemStackEvent;
import net.modificationstation.stationapi.api.item.nbt.HasItemEntity;
import net.modificationstation.stationapi.api.item.nbt.ItemEntity;
import net.modificationstation.stationapi.api.item.nbt.ItemWithEntity;
import net.modificationstation.stationapi.api.item.nbt.StationNBT;
import net.modificationstation.stationapi.api.nbt.NBTHelper;
import net.modificationstation.stationapi.impl.item.nbt.StationNBTSetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.modificationstation.stationapi.api.StationAPI.MODID;
import static net.modificationstation.stationapi.api.registry.Identifier.of;

@SuppressWarnings("deprecation")
@Mixin(ItemInstance.class)
public class MixinItemInstance implements HasItemEntity, StationNBTSetter, StationNBT {

    @Shadow
    public int itemId;

    @Deprecated
    @Unique
    private ItemEntity itemEntity;

    @Deprecated
    @Inject(method = "<init>(III)V", at = @At("RETURN"))
    private void onFreshInstance(int id, int count, int damage, CallbackInfo ci) {
        ItemBase itemBase = ItemBase.byId[id];
        if (itemBase instanceof ItemWithEntity itemWithEntity)
            itemEntity = itemWithEntity.getItemEntityFactory().get();
    }

    @Deprecated
    @Inject(method = "fromTag(Lnet/minecraft/util/io/CompoundTag;)V", at = @At("RETURN"))
    private void fromTag(CompoundTag tag, CallbackInfo ci) {
        ItemBase itemBase = ItemBase.byId[itemId];
        if (itemBase instanceof ItemWithEntity itemWithEntity)
            itemEntity = itemWithEntity.getItemEntityNBTFactory().apply(tag);
    }

    @Deprecated
    @Inject(method = "toTag(Lnet/minecraft/util/io/CompoundTag;)Lnet/minecraft/util/io/CompoundTag;", at = @At("RETURN"))
    private void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        ItemBase itemBase = ItemBase.byId[itemId];
        if (itemBase instanceof ItemWithEntity)
            itemEntity.writeToNBT(tag);
    }

    @Deprecated
    @Inject(method = "split(I)Lnet/minecraft/item/ItemInstance;", at = @At("RETURN"))
    private void onSplit(int countToTake, CallbackInfoReturnable<ItemInstance> cir) {
        if (itemEntity != null)
            HasItemEntity.cast(cir.getReturnValue()).setItemEntity(itemEntity.split(countToTake));
    }

    @Deprecated
    @Inject(method = "copy()Lnet/minecraft/item/ItemInstance;", at = @At("RETURN"))
    private void onCopy(CallbackInfoReturnable<ItemInstance> cir) {
        if (itemEntity != null)
            HasItemEntity.cast(cir.getReturnValue()).setItemEntity(itemEntity.copy());
    }

    @Deprecated
    @Override
    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    @Deprecated
    @Override
    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Inject(method = "onCrafted(Lnet/minecraft/level/Level;Lnet/minecraft/entity/player/PlayerBase;)V", at = @At("RETURN"))
    private void onCreation(Level arg, PlayerBase arg1, CallbackInfo ci) {
        StationAPI.EVENT_BUS.post(
                ItemStackEvent.Crafted.builder()
                        .itemStack((ItemInstance) (Object) this)
                        .level(arg)
                        .player(arg1)
                        .build()
        );
    }

    @Unique
    @Getter @Setter
    private CompoundTag stationNBT = new CompoundTag();

    @Inject(
            method = "split(I)Lnet/minecraft/item/ItemInstance;",
            at = @At("RETURN")
    )
    private void setSplitStackNbt(int par1, CallbackInfoReturnable<ItemInstance> cir) {
        if (!stationNBT.values().isEmpty())
            StationNBTSetter.cast(cir.getReturnValue()).setStationNBT(NBTHelper.copy(stationNBT));
    }

    @Inject(
            method = "toTag(Lnet/minecraft/util/io/CompoundTag;)Lnet/minecraft/util/io/CompoundTag;",
            at = @At("RETURN")
    )
    private void toTagCustom(CompoundTag par1, CallbackInfoReturnable<CompoundTag> cir) {
        if (!stationNBT.values().isEmpty())
            cir.getReturnValue().put(of(MODID, "item_nbt").toString(), stationNBT);
    }

    @Inject(
            method = "fromTag(Lnet/minecraft/util/io/CompoundTag;)V",
            at = @At("RETURN")
    )
    private void fromTagCustom(CompoundTag par1, CallbackInfo ci) {
        stationNBT = par1.getCompoundTag(of(MODID, "item_nbt").toString());
    }

    @Inject(
            method = "copy()Lnet/minecraft/item/ItemInstance;",
            at = @At("RETURN")
    )
    private void copy(CallbackInfoReturnable<ItemInstance> cir) {
        if (!stationNBT.values().isEmpty())
            StationNBTSetter.cast(cir.getReturnValue()).setStationNBT(NBTHelper.copy(stationNBT));
    }

    @Inject(
            method = "isStackIdentical(Lnet/minecraft/item/ItemInstance;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isStackIdentical(ItemInstance par1, CallbackInfoReturnable<Boolean> cir) {
        if (!NBTHelper.equals(stationNBT, StationNBT.cast(par1).getStationNBT()))
            cir.setReturnValue(false);
    }

    @Inject(
            method = "isDamageAndIDIdentical(Lnet/minecraft/item/ItemInstance;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isDamageAndIDIdentical(ItemInstance par1, CallbackInfoReturnable<Boolean> cir) {
        if (!NBTHelper.equals(stationNBT, StationNBT.cast(par1).getStationNBT()))
            cir.setReturnValue(false);
    }

    @Inject(
            method = "toString()Ljava/lang/String;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void toStringNBT(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cir.getReturnValue() + ", stationNBT=[" + stationNBT + "]");
    }

    @Inject(
            method = "isStackIdentical2(Lnet/minecraft/item/ItemInstance;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isStackIdentical2(ItemInstance par1, CallbackInfoReturnable<Boolean> cir) {
        if (!NBTHelper.equals(stationNBT, StationNBT.cast(par1).getStationNBT()))
            cir.setReturnValue(false);
    }
}
