/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package cn.mazu.auth;


/**
 * Enumeration for a login state.
 * <p>
 * 
 * @see Login#getState()
 */
public enum LoginState {
	/**
	 * No user is currently identified.
	 */
	LoggedOut,
	/**
	 * The identified user was refused to login.
	 * <p>
	 * This is caused by {@link User#getStatus() User#getStatus()} returning
	 * {@link User.Status#Disabled}
	 */
	DisabledLogin,
	/**
	 * A user is weakly authenticated.
	 * <p>
	 * The authentication method was weak, typically this means that a secondary
	 * authentication system was used (e.g. an authentication cookie) instead of
	 * a primary mechanism (like a password).
	 * <p>
	 * You may want to allow certain operations, but request to authenticate
	 * fully before more senstive operations.
	 */
	WeakLogin,
	/**
	 * A user is strongly authenticated.
	 */
	StrongLogin;

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return ordinal();
	}
}
