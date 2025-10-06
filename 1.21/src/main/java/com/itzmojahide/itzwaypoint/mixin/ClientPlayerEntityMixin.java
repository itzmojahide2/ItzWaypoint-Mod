package com.itzmojahide.itzwaypoint.mixin;

import com.itzmojahide.itzwaypoint.manager.WaypointManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void onPlayerDeath(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        WaypointManager.createDeathPoint(player.getX(), player.getY(), player.getZ());
    }
}
