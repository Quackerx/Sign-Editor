package sign.editor.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static sign.editor.SignEditor.*;

@Mixin(Connection.class)
public class ConnectionMixin {

    private final Random rand = new Random();

    private BlockPos editRearPos = null;
    private List<String> pendingRearLines = null;

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void onPacketReceive(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        if (!autoSign) return;

        if (mc.player == null) return;

        if (packet instanceof ClientboundOpenSignEditorPacket(BlockPos pos, SignTextSlot slot)) {
            ci.cancel();

            boolean isFront = (editRearPos == null || !editRearPos.equals(pos));

            if (isFront) {
                editRearPos = null;
                pendingRearLines = null;

                List<String> lines = createSignLines();

                mc.player.connection.send(new ServerboundSignUpdatePacket(pos, lines, SignTextSlot.FRONT));

                if (BackMode) {
                    editRearPos = pos;
                    pendingRearLines = lines;

                    scheduleNextTick(() -> {
                        if (mc.player == null || mc.gameMode == null || mc.level == null) {
                            editRearPos = null;
                            pendingRearLines = null;
                            return;
                        }

                        if (!pos.equals(editRearPos)) return;

                        BlockState state = mc.level.getBlockState(pos);
                        Direction interactSide = Direction.DOWN;

                        if (state.getBlock() instanceof WallHangingSignBlock) {
                            interactSide = state.getValue(WallHangingSignBlock.FACING).getOpposite();
                        } else if (state.getBlock() instanceof CeilingHangingSignBlock) {
                            int rotation = state.getValue(CeilingHangingSignBlock.ROTATION);
                            interactSide = Direction.fromYRot(rotation).getOpposite();
                        }
                        BlockHitResult result = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), interactSide, pos, false);

                        boolean wasSneaking = mc.player.isShiftKeyDown();
                        mc.player.setShiftKeyDown(false);
                        var sneakP = new ServerboundPlayerInputPacket(
                                new Input(
                                        mc.player.input.keyPresses.forward(),
                                        mc.player.input.keyPresses.backward(),
                                        mc.player.input.keyPresses.left(),
                                        mc.player.input.keyPresses.right(),
                                        mc.player.input.keyPresses.jump(),
                                        false,
                                        mc.player.input.keyPresses.sprint()));

                        mc.player.connection.send(sneakP);

                        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, result);

                        mc.player.setShiftKeyDown(wasSneaking);

                        sneakP = new ServerboundPlayerInputPacket(
                                new Input(
                                        mc.player.input.keyPresses.forward(),
                                        mc.player.input.keyPresses.backward(),
                                        mc.player.input.keyPresses.left(),
                                        mc.player.input.keyPresses.right(),
                                        mc.player.input.keyPresses.jump(),
                                        wasSneaking,
                                        mc.player.input.keyPresses.sprint()));
                        mc.player.connection.send(sneakP);
                    });
                }

            } else {
                List<String> lines = pendingRearLines != null ? pendingRearLines : createSignLines();
                mc.player.connection.send(new ServerboundSignUpdatePacket(pos, lines, SignTextSlot.BACK));
                editRearPos = null;
                pendingRearLines = null;
            }
        }
    }

    @Unique
    private List<String> createSignLines() {
        List<String> list = new ArrayList<>();

        if (CustomMode) {
            list.add(customText1);
            list.add(customText2);
            list.add(customText3);
            list.add(customText4);
        } else {
            for (int i = 0; i < 4; i++) {
                StringBuilder builder = new StringBuilder();

                for (int j = 0; j < signLength; j++) {

                    if (MAXMODE) {
                        builder.appendCodePoint(0x10001);
                        continue;
                    }
                    char c = (char) (rand.nextInt(0xD7FF - 0x20) + 0x20);
                    builder.append(c);
                }

                list.add(builder.toString());
            }
        }

        return list;
    }

}
