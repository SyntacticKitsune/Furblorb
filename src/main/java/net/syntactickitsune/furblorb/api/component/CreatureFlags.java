package net.syntactickitsune.furblorb.api.component;

import net.syntactickitsune.furblorb.api.asset.CreatureAsset;

/**
 * See {@link CreatureAsset#flags}.
 */
public final class CreatureFlags {

	/**
	 * The creature cannot be grappled.
	 */
	public static final int NO_GRAPPLE = 0b00001;

	/**
	 * The creature cannot be swallowed.
	 */
	public static final int NO_PREY    = 0b00010;

	/**
	 * The creature cannot be attacked.
	 */
	public static final int NO_FIGHT   = 0b00100;

	/**
	 * The creature does not yield any experience.
	 */
	public static final int NO_XP      = 0b01000;

	/**
	 * The creature does not take any turns in combat.
	 */
	public static final int SKIP_TURNS = 0b10000;
}