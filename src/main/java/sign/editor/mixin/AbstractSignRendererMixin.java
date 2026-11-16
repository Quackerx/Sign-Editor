package sign.editor.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static sign.editor.SignEditor.noRender;

@Mixin(AbstractSignRenderer.class)
public class AbstractSignRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"))
    private void submit(SignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (noRender) {
            state.frontText = null;
            state.backText = null;
        }
    }
}
