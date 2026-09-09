package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.item.ItemProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ItemFuelNumberProvider(ItemProvider item) implements NumberProvider {

	public static final MapCodec<ItemFuelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ItemProvider.CODEC.fieldOf("item").forGetter(ItemFuelNumberProvider::item))
		.apply(instance, ItemFuelNumberProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemFuelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		ItemProvider.STREAM_CODEC, ItemFuelNumberProvider::item,
		ItemFuelNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ITEM_FUEL;
	}

	@Override
	public double getDouble(Context context) {
		return this.getLong(context);
	}

	@Override
	public long getLong(Context context) {
		return item().getItem(context.forChild(".item"))
			.map(context.level().fuelValues()::burnDuration)
			.orElse(0);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		item().validate(validator.forChild(".item"));
	}

}
