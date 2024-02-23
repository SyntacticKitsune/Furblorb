package net.syntactickitsune.furblorb.api.component;

import net.syntactickitsune.furblorb.api.asset.CreatureAsset;

/**
 * <p>
 * Represents a bunch of different {@code boolean} flags on {@linkplain CreatureAsset creatures}.
 * Each flag modifies how the creature acts (or is acted upon) in combat.
 * Many of these disable actions others may perform on this creature.
 * </p>
 * <p>
 * Normally, these flags are composed into a single {@code int} value.
 * The {@code CreatureFlags} class exists to provide a friendlier means to inspect and change this information.
 * </p>
 * <p>
 * Currently, the flags are:
 * <table border="1">
 * <tr><td>{@code NO_GRAPPLE}</td><td>The creature cannot be grappled.</td></tr>
 * <tr><td>{@code NO_PREY}</td><td>The creature cannot be swallowed.</td></tr>
 * <tr><td>{@code NO_FIGHT}</td><td>The creature cannot be attacked.</td></tr>
 * <tr><td>{@code NO_XP}</td><td>The creature gives no experience when defeated.</td></tr>
 * <tr><td>{@code SKIP_TURNS}</td><td>The creature skips all of its turns in combat.</td></tr>
 * </table>
 * </p>
 * @see CreatureAsset#flags
 * @see CreatureFlags#CreatureFlags(int)
 * @see CreatureFlags#pack()
 */
public final class CreatureFlags {

	private static final int NO_GRAPPLE = 0b00001;
	private static final int NO_PREY    = 0b00010;
	private static final int NO_FIGHT   = 0b00100;
	private static final int NO_XP      = 0b01000;
	private static final int SKIP_TURNS = 0b10000;

	private boolean noGrapple;
	private boolean noPrey;
	private boolean noFight;
	private boolean noXp;
	private boolean skipTurns;

	/**
	 * @param noGrapple Whether or not the creature can be grappled.
	 * @param noPrey Whether or not the creature can be swallowed.
	 * @param noFight Whether or not the creature can be attacked.
	 * @param noXp Whether or not the creature gives any experience.
	 * @param skipTurns Whether or not the creature takes any turns in combat.
	 */
	private CreatureFlags(boolean noGrapple, boolean noPrey, boolean noFight, boolean noXp, boolean skipTurns) {
		this.noGrapple = noGrapple;
		this.noPrey = noPrey;
		this.noFight = noFight;
		this.noXp = noXp;
		this.skipTurns = skipTurns;
	}

	/**
	 * Initializes a new {@code CreatureFlags} with all parameters set to {@code false} (the defaults).
	 */
	public CreatureFlags() {
		this(false, false, false, false, false);
	}

	/**
	 * Initializes a new {@code CreatureFlags} with all parameters set from the given {@linkplain #pack() packed} {@code int}.
	 * @param value The packed {@code int} to initialize the parameters from.
	 */
	public CreatureFlags(int value) {
		this((value & NO_GRAPPLE) != 0,
				(value & NO_PREY) != 0,
				(value & NO_FIGHT) != 0,
				(value & NO_XP) != 0,
				(value & SKIP_TURNS) != 0);
	}

	/**
	 * Packs all of this {@code CreatureFlags}'s parameters into a single {@code int} and returns it.
	 * The returned {@code int} can be passed into {@link #CreatureFlags(int)} to retrieve an identical {@code CreatureFlags}.
	 * @return The packed {@code int}.
	 */
	public int pack() {
		int ret = 0;

		if (noGrapple) ret |= NO_GRAPPLE;
		if (noPrey) ret |= NO_PREY;
		if (noFight) ret |= NO_FIGHT;
		if (noXp) ret |= NO_XP;
		if (skipTurns) ret |= SKIP_TURNS;

		return ret;
	}

	/**
	 * @return {@code true} if the creature cannot be grappled.
	 * @see #noGrapple(boolean)
	 */
	public boolean noGrapple() {
		return noGrapple;
	}

	/**
	 * Sets {@link #noGrapple()}.
	 * @param value Whether or not the creature cannot be grappled.
	 * @return {@code this}.
	 */
	public CreatureFlags noGrapple(boolean value) {
		noGrapple = value;
		return this;
	}

	/**
	 * @return {@code true} if the creature cannot be swallowed.
	 * @see #noPrey(boolean)
	 */
	public boolean noPrey() {
		return noPrey;
	}

	/**
	 * Sets {@link #noPrey()}.
	 * @param value Whether or not the creature cannot be swallowed.
	 * @return {@code this}.
	 */
	public CreatureFlags noPrey(boolean value) {
		noPrey = value;
		return this;
	}

	/**
	 * @return {@code true} if the creature cannot be attacked.
	 * @see #noFight(boolean)
	 */
	public boolean noFight() {
		return noFight;
	}

	/**
	 * Sets {@link #noFight()}.
	 * @param value Whether or not the creature cannot be attacked.
	 * @return {@code this}.
	 */
	public CreatureFlags noFight(boolean value) {
		noFight = value;
		return this;
	}

	/**
	 * @return {@code true} if the creature yields no experience.
	 * @see #noXp(boolean)
	 */
	public boolean noXp() {
		return noXp;
	}

	/**
	 * Sets {@link #noXp()}.
	 * @param value Whether or not the creature gives no experience.
	 * @return {@code this}.
	 */
	public CreatureFlags noXp(boolean value) {
		noXp = value;
		return this;
	}

	/**
	 * @return {@code true} if the creature skips all of its turns in combat.
	 * @see #skipTurns(boolean)
	 */
	public boolean skipTurns() {
		return skipTurns;
	}

	/**
	 * Sets {@link #skipTurns()}.
	 * @param value Whether or not the creature skips all turns in combat.
	 * @return {@code this}.
	 */
	public CreatureFlags skipTurns(boolean value) {
		skipTurns = value;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof CreatureFlags cf)) return false;
		return noGrapple == cf.noGrapple && noPrey == cf.noPrey && noFight == cf.noFight
				&& noXp == cf.noXp && skipTurns == cf.skipTurns;
	}

	@Override
	public int hashCode() {
		return pack(); // Haha very funny.
	}
}