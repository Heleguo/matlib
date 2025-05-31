package me.matl114.matlib.implement.custom.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import me.matl114.matlib.nmsMirror.fix.DataHelper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record ScreenTemplate(List<String> patterns, Map<Character, SlotType> key) {
    private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
        if (pattern.size() > 6) {
            return DataResult.error(() -> "Invalid pattern: too many rows, 6 is maximum");
        } else if (pattern.isEmpty()) {
            return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
        } else {
            for (String string : pattern) {
                if (string.length() != 9) {
                    return DataResult.error(() -> "Invalid pattern: column mismatch, must be 9");
                }

            }
            return DataHelper.A.I.success(pattern);
        }
    }, Function.identity());
    private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(keyEntry -> {
        if (keyEntry.length() != 1) {
            return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
        } else {
            return " ".equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataHelper.A.I.success(keyEntry.charAt(0));
        }
    }, String::valueOf);
    public static final MapCodec<ScreenTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.patterns),
                new UnboundedMapCodec<>(SYMBOL_CODEC, SlotType.CODEC).fieldOf("key").forGetter(data -> data.key)
            )
            .apply(instance, ScreenTemplate::new)
    );
}
