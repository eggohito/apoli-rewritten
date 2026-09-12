package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public record RandomBinomialNumberProvider(NumberProvider attempts, NumberProvider probability) implements NumberProvider {

	public static final MapCodec<RandomBinomialNumberProvider> CODEC = MapCodecUtil.lazy(RandomBinomialNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("attempts").forGetter(RandomBinomialNumberProvider::attempts),
		NumberProvider.CODEC.fieldOf("probability").forGetter(RandomBinomialNumberProvider::probability)
	).apply(instance, RandomBinomialNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, RandomBinomialNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(RandomBinomialNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, RandomBinomialNumberProvider::attempts,
		NumberProvider.STREAM_CODEC, RandomBinomialNumberProvider::probability,
		RandomBinomialNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.RANDOM_BINOMIAL;
	}

	@Override
	public double getDouble(Context context) {
		return this.getLong(context);
	}

	@Override
	public long getLong(Context context) {

		RandomSource random = context.level().getRandom();
		long result = 0;

		Context attemptsContext = context.forChild(".attempts");
		long attempts = attempts().getLong(attemptsContext);

		Context probabilityContext = context.forChild(".probability");
		double probability = probability().getDouble(probabilityContext);

		for (int i = 0; !attemptsContext.hasProblems() && !probabilityContext.hasProblems() && i < attempts; i++) {

			if (random.nextDouble() < probability) {
				result++;
			}

		}

		return result;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		attempts().validate(validator.forChild(".attempts"));
		probability().validate(validator.forChild(".probability"));

	}

}
