package by.quaks.autograph.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

import static by.quaks.autograph.commands.AutographCommand.*;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
            method = "updateResult", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
            ordinal = 0,
            shift = At.Shift.AFTER
    ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void updateResult(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map) {
        if (!hasAutographBy(itemStack, player)) {
            ItemStack finalItem = itemStack.copy();
            addLore(finalItem, genJsonAutograph(player));
            if (itemStack.isDamageable() && itemStack3.isOf(Items.PAPER)) {
                this.output.setStack(0, finalItem);
            }
        }
        ci.cancel();
    }
    @Unique
    private static Map <Inventory, ItemStack> itemStackMap = new HashMap<>();
    @Redirect(method = "onTakeOutput", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V",
            ordinal = 3
    ))
    public void setStack(Inventory instance, int i, ItemStack itemStack){
        if(itemStackMap.containsKey(instance)){
            if (itemStackMap.get(instance).isDamageable() && this.input.getStack(1).isOf(Items.PAPER)) {
                this.input.getStack(1).decrement(1);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
            itemStackMap.remove(instance);
        }else{
            this.input.setStack(1, ItemStack.EMPTY);
        }
    }
    @Redirect(method = "onTakeOutput", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V",
            ordinal = 0
    ))
    public void getLast(Inventory instance, int i, ItemStack itemStack){
            itemStackMap.put(instance,this.input.getStack(0).copy());
            this.input.setStack(0, ItemStack.EMPTY);
    }

}
