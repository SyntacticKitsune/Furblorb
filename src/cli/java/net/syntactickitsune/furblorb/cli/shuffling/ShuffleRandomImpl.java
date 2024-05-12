package net.syntactickitsune.furblorb.cli.shuffling;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * The implementation of {@link ShuffleRandom}.
 * @author SyntacticKitsune
 */
final class ShuffleRandomImpl implements ShuffleRandom {

	private final RandomGenerator random;
	private final Random fake;

	/**
	 * Constructs a new {@code ShuffleRandomImpl} with the specified {@code RandomGenerator}.
	 * @param random The {@code RandomGenerator} to wrap.
	 */
	ShuffleRandomImpl(RandomGenerator random) {
		this.random = random;
		fake = random instanceof Random r ? r : new FakeRandom(random);
	}

	@Override
	public void nextBytes(byte[] bytes) { random.nextBytes(bytes); }
	@Override
	public int nextInt() { return random.nextInt(); }
	@Override
	public int nextInt(int bound) { return random.nextInt(bound); }
	@Override
	public long nextLong() { return random.nextLong(); }
	@Override
	public boolean nextBoolean() { return random.nextBoolean(); }
	@Override
	public float nextFloat() { return random.nextFloat(); }
	@Override
	public double nextDouble() { return random.nextDouble(); }
	@Override
	public synchronized double nextGaussian() { return random.nextGaussian(); }
	@Override
	public IntStream ints(long streamSize) { return random.ints(streamSize); }
	@Override
	public IntStream ints() { return random.ints(); }
	@Override
	public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) { return random.ints(streamSize, randomNumberOrigin, randomNumberBound); }
	@Override
	public IntStream ints(int randomNumberOrigin, int randomNumberBound) { return random.ints(randomNumberOrigin, randomNumberBound); }
	@Override
	public LongStream longs(long streamSize) { return random.longs(streamSize); }
	@Override
	public LongStream longs() { return random.longs(); }
	@Override
	public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) { return random.longs(streamSize, randomNumberOrigin, randomNumberBound); }
	@Override
	public LongStream longs(long randomNumberOrigin, long randomNumberBound) { return random.longs(randomNumberOrigin, randomNumberBound); }
	@Override
	public DoubleStream doubles(long streamSize) { return random.doubles(streamSize); }
	@Override
	public DoubleStream doubles() { return random.doubles(); }
	@Override
	public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) { return random.doubles(streamSize, randomNumberOrigin, randomNumberBound); }
	@Override
	public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) { return random.doubles(randomNumberOrigin, randomNumberBound); }
	@Override
	public boolean isDeprecated() { return random.isDeprecated(); }
	@Override
	public double nextDouble(double bound) { return random.nextDouble(bound); }
	@Override
	public double nextDouble(double origin, double bound) { return random.nextDouble(origin, bound); }
	@Override
	public double nextExponential() { return random.nextExponential(); }
	@Override
	public float nextFloat(float bound) { return random.nextFloat(bound); }
	@Override
	public float nextFloat(float origin, float bound) { return random.nextFloat(origin, bound); }
	@Override
	public double nextGaussian(double mean, double stddev) { return random.nextGaussian(mean, stddev); }
	@Override
	public int nextInt(int origin, int bound) { return random.nextInt(origin, bound); }
	@Override
	public long nextLong(long bound) { return random.nextLong(bound); }
	@Override
	public long nextLong(long origin, long bound) { return random.nextLong(origin, bound); }

	@Override
	public void shuffle(List<?> list) {
		Collections.shuffle(list, fake);
	}

	/**
	 * Serves as a bridge between ancient {@link Random}-based APIs and the newer {@link RandomGenerator} ones.
	 * @author SyntacticKitsune
	 */
	private static final class FakeRandom extends Random {

		private final RandomGenerator delegate;

		FakeRandom(RandomGenerator delegate) {
			this.delegate = delegate;
		}

		@Override
		public void nextBytes(byte[] bytes) { delegate.nextBytes(bytes); }
		@Override
		public int nextInt() { return delegate.nextInt(); }
		@Override
		public int nextInt(int bound) { return delegate.nextInt(bound); }
		@Override
		public long nextLong() { return delegate.nextLong(); }
		@Override
		public boolean nextBoolean() { return delegate.nextBoolean(); }
		@Override
		public float nextFloat() { return delegate.nextFloat(); }
		@Override
		public double nextDouble() { return delegate.nextDouble(); }
		@Override
		public synchronized double nextGaussian() { return delegate.nextGaussian(); }
		@Override
		public IntStream ints(long streamSize) { return delegate.ints(streamSize); }
		@Override
		public IntStream ints() { return delegate.ints(); }
		@Override
		public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) { return delegate.ints(streamSize, randomNumberOrigin, randomNumberBound); }
		@Override
		public IntStream ints(int randomNumberOrigin, int randomNumberBound) { return delegate.ints(randomNumberOrigin, randomNumberBound); }
		@Override
		public LongStream longs(long streamSize) { return delegate.longs(streamSize); }
		@Override
		public LongStream longs() { return delegate.longs(); }
		@Override
		public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) { return delegate.longs(streamSize, randomNumberOrigin, randomNumberBound); }
		@Override
		public LongStream longs(long randomNumberOrigin, long randomNumberBound) { return delegate.longs(randomNumberOrigin, randomNumberBound); }
		@Override
		public DoubleStream doubles(long streamSize) { return delegate.doubles(streamSize); }
		@Override
		public DoubleStream doubles() { return delegate.doubles(); }
		@Override
		public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) { return delegate.doubles(streamSize, randomNumberOrigin, randomNumberBound); }
		@Override
		public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) { return delegate.doubles(randomNumberOrigin, randomNumberBound); }
		@Override
		public boolean isDeprecated() { return delegate.isDeprecated(); }
		@Override
		public double nextDouble(double bound) { return delegate.nextDouble(bound); }
		@Override
		public double nextDouble(double origin, double bound) { return delegate.nextDouble(origin, bound); }
		@Override
		public double nextExponential() { return delegate.nextExponential(); }
		@Override
		public float nextFloat(float bound) { return delegate.nextFloat(bound); }
		@Override
		public float nextFloat(float origin, float bound) { return delegate.nextFloat(origin, bound); }
		@Override
		public double nextGaussian(double mean, double stddev) { return delegate.nextGaussian(mean, stddev); }
		@Override
		public int nextInt(int origin, int bound) { return delegate.nextInt(origin, bound); }
		@Override
		public long nextLong(long bound) { return delegate.nextLong(bound); }
		@Override
		public long nextLong(long origin, long bound) { return delegate.nextLong(origin, bound); }
	}
}