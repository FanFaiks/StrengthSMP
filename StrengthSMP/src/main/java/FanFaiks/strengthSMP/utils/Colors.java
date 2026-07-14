package FanFaiks.strengthSMP.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Colors {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    public static Component c(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text);
    }

    public static String toPlain(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}