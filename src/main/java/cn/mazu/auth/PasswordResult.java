/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package cn.mazu.auth;

import cn.mazu.auth.serv.AbstractPasswordService;


/**
 * Enumeration for a password verification result.
 * <p>
 * 
 * @see AbstractPasswordService#verifyPassword(User user, String password)
 */
public enum PasswordResult {
	/**
	 * The password is invalid.
	 */
	PasswordInvalid,
	/**
	 * The attempt was not processed because of throttling.
	 */
	LoginThrottling,
	/**
	 * The password is valid.
	 */
	PasswordValid;

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return ordinal();
	}
}
