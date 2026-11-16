package sign.editor.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static sign.editor.SignEditor.*;

@Mixin(AbstractContainerScreen.class)
public abstract class ScreenMixin extends Screen {

	@Unique private Button customButton;
	@Unique private Button backButton;
	@Unique private EditBox lengthBox;
	@Unique private EditBox customSign1;
	@Unique private EditBox customSign2;
	@Unique private EditBox customSign3;
	@Unique private EditBox customSign4;



    protected ScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void init(CallbackInfo c) {
		if (this.minecraft.player == null || this.minecraft.gui.screen() == null) {
			return;
		}

		int width = 115;
		int height = 20;
		int y = 40;
		int space = 30;

		this.addRenderableWidget(Button.builder(Component.literal("No Render: "+ (noRender ? "§a" : "§c") + noRender), b -> {
			noRender = !noRender;
			updatevision();
			b.setMessage(Component.literal(noRender ? "No Render: §a" + noRender : "No Render: §c" + noRender));
				}).bounds(5, y, width, height).build());

		y += space;

		this.addRenderableWidget(Button.builder(Component.literal("Auto Sign: " + (autoSign ? "§a" : "§c") + autoSign), b -> {
			autoSign = !autoSign;
			b.setMessage(Component.literal(autoSign ? "Auto Sign: §a" + autoSign : "Auto Sign: §c" + autoSign));
			updatevision();
		}).bounds(5, y, width, height).build());

		y += space;

		this.customButton = Button.builder(Component.literal("Custom: " + (CustomMode ? "§a" : "§c") + CustomMode), b -> {
			CustomMode = !CustomMode;
			b.setMessage(Component.literal(CustomMode ? "Custom: §a" + CustomMode : "Custom: §c" + CustomMode));
			updatevision();
		}).bounds(5, y, width, height).build();
		this.customButton.visible = autoSign;
		this.addRenderableWidget(this.customButton);

		y += space;

		this.backButton = Button.builder(Component.literal("Back: " + (BackMode ? "§a" : "§c") + BackMode), b -> {
			BackMode = !BackMode;
			b.setMessage(Component.literal(BackMode ? "Back: §a" + BackMode : "Back: §c" + BackMode));
		}).bounds(5, y, width, height).build();
		this.backButton.visible = autoSign;
		this.addRenderableWidget(this.backButton);

		y += space;

		this.lengthBox = new EditBox(this.font, 5, y, 160, 20, Component.literal("Sign Length"));
		lengthBox.setMaxLength(256);
		lengthBox.setValue(String.valueOf(signLength));
		lengthBox.setFocused(false);
		lengthBox.visible = autoSign;

		lengthBox.setResponder(text -> {
			try {
				int value = text.isEmpty() ? 1 : Integer.parseInt(text);
				signLength = Math.clamp(value, 1, 256);
			} catch (NumberFormatException ignored) {
				lengthBox.setValue(String.valueOf(signLength));
				if (mc.player != null) {
					mc.player.sendOverlayMessage(Component.literal("§cPlease enter an Integer!"));
				}
			}
		});
		this.addRenderableWidget(lengthBox);

		y += space;

		this.customSign1 = new EditBox(this.font, 5, y, 160, 20, Component.literal("Custom Text 1"));
		this.customSign1.setMaxLength(256);
		this.customSign1.setHint(Component.literal("Yo gurt"));
		this.customSign1.setValue(customText1);
		this.customSign1.setFocused(false);
		this.customSign1.visible = autoSign && CustomMode;
		this.customSign1.setResponder(text -> customText1 = text);
		this.addRenderableWidget(this.customSign1);

		y += space;

		this.customSign2 = new EditBox(this.font, 5, y, 160, 20, Component.literal("Custom Text 2"));
		this.customSign2.setMaxLength(256);
		this.customSign2.setHint(Component.literal("Yo gurt 2"));
		this.customSign2.setValue(customText2);
		this.customSign2.setFocused(false);
		this.customSign2.visible = autoSign && CustomMode;
		this.customSign2.setResponder(text -> customText2 = text);
		this.addRenderableWidget(this.customSign2);

		y += space;

		this.customSign3 = new EditBox(this.font, 5, y, 160, 20, Component.literal("Custom Text 3"));
		this.customSign3.setMaxLength(256);
		this.customSign3.setHint(Component.literal("Yo gurt 3"));
		this.customSign3.setValue(customText3);
		this.customSign3.setFocused(false);
		this.customSign3.visible = autoSign && CustomMode;
		this.customSign3.setResponder(text -> customText3 = text);
		this.addRenderableWidget(this.customSign3);

		y += space;

		this.customSign4 = new EditBox(this.font, 5, y, 160, 20, Component.literal("Custom Text 4"));
		this.customSign4.setMaxLength(256);
		this.customSign4.setHint(Component.literal("Yo gurt 4"));
		this.customSign4.setValue(customText4);
		this.customSign4.setFocused(false);
		this.customSign4.visible = autoSign && CustomMode;
		this.customSign4.setResponder(text -> customText4 = text);
		this.addRenderableWidget(this.customSign4);

		y += space;

		this.addRenderableWidget(Button.builder(Component.literal("MAX: "+ (MAXMODE ? "§a" : "§c") + MAXMODE), b -> {
			MAXMODE = !MAXMODE;
			updatevision();
			b.setMessage(Component.literal(MAXMODE ? "MAX: §a" + MAXMODE : "MAX: §c" + MAXMODE));
		}).bounds(5, y, width, height).build());
	}


	@Unique
	private void updatevision() {
		if (MAXMODE) {
			if (customButton != null) customButton.visible = true;
			if (backButton != null) backButton.visible = true;
			if (lengthBox != null) lengthBox.visible = true;
			if (customSign1 != null) customSign1.visible = false;
			if (customSign2 != null) customSign2.visible = false;
			if (customSign3 != null) customSign3.visible = false;
			if (customSign4 != null) customSign4.visible = false;
			return;
		}
		if (customButton != null) customButton.visible = autoSign;
		if (backButton != null) backButton.visible = autoSign;
		if (lengthBox != null) lengthBox.visible = autoSign;
		boolean showCustom = autoSign && CustomMode;
		if (customSign1 != null) customSign1.visible = showCustom;
		if (customSign2 != null) customSign2.visible = showCustom;
		if (customSign3 != null) customSign3.visible = showCustom;
		if (customSign4 != null) customSign4.visible = showCustom;
	}
}