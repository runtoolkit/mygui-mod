package com.example.mygui.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MyCustomScreen extends Screen {

    // Panel boyutları
    private static final int BG_WIDTH  = 240;
    private static final int BG_HEIGHT = 180;

    // Renkler (ARGB)
    private static final int COLOR_BG         = 0xE0101018;
    private static final int COLOR_HEADER      = 0xFF1A1A2E;
    private static final int COLOR_BORDER      = 0xFF4444AA;
    private static final int COLOR_BORDER_GLOW = 0x334444FF;
    private static final int COLOR_TEXT        = 0xFFCCCCFF;
    private static final int COLOR_LABEL       = 0xFF8888CC;

    private int bgX, bgY;

    // Widget'lar
    private TextFieldWidget inputField;
    private final List<String> log = new ArrayList<>();

    public MyCustomScreen() {
        super(Text.literal("My Custom GUI"));
    }

    @Override
    protected void init() {
        bgX = (this.width  - BG_WIDTH)  / 2;
        bgY = (this.height - BG_HEIGHT) / 2;

        int centerX = bgX + BG_WIDTH / 2;

        // --- Text field ---
        inputField = new TextFieldWidget(
            this.textRenderer,
            bgX + 20, bgY + 70,
            BG_WIDTH - 40, 18,
            Text.literal("")
        );
        inputField.setMaxLength(64);
        inputField.setPlaceholder(Text.literal("Buraya yaz...").withColor(0x666688));
        this.addSelectableChild(inputField);

        // --- Buton 1: Gönder ---
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Gönder"),
            btn -> {
                String val = inputField.getText().trim();
                if (!val.isEmpty()) {
                    addLog("[Gönderildi] " + val);
                    inputField.setText("");
                }
            }
        ).dimensions(bgX + 20, bgY + 100, 90, 20).build());

        // --- Buton 2: Temizle ---
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Temizle"),
            btn -> {
                log.clear();
                inputField.setText("");
            }
        ).dimensions(bgX + BG_WIDTH - 110, bgY + 100, 90, 20).build());

        // --- Buton 3: Kapat ---
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("✕"),
            btn -> this.close()
        ).dimensions(bgX + BG_WIDTH - 18, bgY + 4, 14, 14).build());
    }

    private void addLog(String msg) {
        log.add(msg);
        if (log.size() > 3) log.remove(0);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Karartılmış arka plan
        this.renderBackground(context, mouseX, mouseY, delta);

        int x = bgX, y = bgY;
        int w = BG_WIDTH, h = BG_HEIGHT;

        // --- Glow (dış hale) ---
        context.fill(x - 2, y - 2, x + w + 2, y + h + 2, COLOR_BORDER_GLOW);

        // --- Ana panel ---
        context.fill(x, y, x + w, y + h, COLOR_BG);

        // --- Header bar ---
        context.fill(x, y, x + w, y + 24, COLOR_HEADER);

        // --- Border ---
        // Üst
        context.fill(x, y, x + w, y + 1, COLOR_BORDER);
        // Alt
        context.fill(x, y + h - 1, x + w, y + h, COLOR_BORDER);
        // Sol
        context.fill(x, y, x + 1, y + h, COLOR_BORDER);
        // Sağ
        context.fill(x + w - 1, y, x + w, y + h, COLOR_BORDER);
        // Header alt çizgi
        context.fill(x, y + 24, x + w, y + 25, COLOR_BORDER);

        // --- Başlık ---
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            x + w / 2,
            y + 8,
            COLOR_TEXT
        );

        // --- Input label ---
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Mesaj:").withColor(COLOR_LABEL),
            x + 20, y + 58,
            COLOR_LABEL
        );

        // --- Text field render ---
        inputField.render(context, mouseX, mouseY, delta);

        // --- Log alanı ---
        int logY = y + 130;
        context.fill(x + 10, logY - 4, x + w - 10, logY + 12 * 3 + 2, 0x44000000);
        for (int i = 0; i < log.size(); i++) {
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(log.get(i)).withColor(0xFF88FFAA),
                x + 14, logY + i * 12,
                0xFF88FFAA
            );
        }

        // --- Widgetları çiz ---
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter → gönder
        if (keyCode == 257 && inputField.isFocused()) {
            String val = inputField.getText().trim();
            if (!val.isEmpty()) {
                addLog("[Gönderildi] " + val);
                inputField.setText("");
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        inputField.setFocused(
            mouseX >= inputField.getX() && mouseX < inputField.getX() + inputField.getWidth() &&
            mouseY >= inputField.getY() && mouseY < inputField.getY() + inputField.getHeight()
        );
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
