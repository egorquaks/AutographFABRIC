package by.quaks.autograph.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

import static by.quaks.autograph.commands.AutographCommand.isAutographed;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    @Shadow @Final
    Inventory input;

    @Shadow @Final private Inventory result;

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                    ordinal = 0
            ),index = 0)
    public Slot addSlot(Slot par1){
        return new Slot(this.input,par1.getIndex(), par1.x, par1.y){
            public boolean canInsert(ItemStack stack) {
                return par1.canInsert(stack) || isAutographed(stack);
            }
        };

    }
    @Redirect(method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z",
                    ordinal = 0
            ))
    public boolean redirection(ItemStack instance){
        return instance.hasEnchantments() || isAutographed(instance);
    }

    @Inject(method = "grind",
    at = @At(
            value = "RETURN",
            target = "Lnet/minecraft/item/ItemStack;removeSubNbt(Ljava/lang/String;)V",
            ordinal = 0,
            shift = At.Shift.BEFORE
    ), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injection(ItemStack item, int damage, int amount, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Map map, int i){
        if(isAutographed(itemStack)){
            itemStack.removeSubNbt("display");
            if(itemStack.getNbt().getInt("RepairCost")==0){
                itemStack.removeSubNbt("RepairCost");
            }
        }
    }
}
