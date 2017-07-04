/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package cn.mazu.auth;



/**
 * Enumeration for an identity policy.
 * <p>
 * This enumeration lists possible choices for the user identity (login name).
 * <p>
 * When using password authentication, it is clear that the user has to provide
 * an identity to login. The only choice is whether you will use the user&apos;s
 * email address or another login name.
 * <p>
 * When using a 3rd party authenticator, e.g. using OAuth, a login name is no
 * longer needed, but you may still want to give the user the opportunity to
 * choose one.
 * <p>
 * 
 * @see AuthService#setIdentityPolicy(IdentityPolicy identityPolicy)
 */
public enum IdentityPolicy {
	/**
	 * A unique login name chosen by the user.
	 * <p>
	 * Even if not really required for authentication, a user still chooses a
	 * unique user name. If possible, a third party autheticator may suggest a
	 * user name.
	 * <p>
	 * This may be useful for sites which have a social aspect.
	 */
	LoginNameIdentity,
	/**
	 * The email address serves as the identity.
	 * <p>
	 * This may be useful for sites which do not have any social character, but
	 * instead render a service to individual users. When the site has a social
	 * character, you will likely not want to display the email address of other
	 * users, but instead a user-chosen login name.
	 */
	EmailAddressIdentity,
	/**
	 * An identity is optional, and only asked if needed for authentication.
	 * <p>
	 * Unless the authentication procedure requires a user name, no particular
	 * identity is asked for. In this case, the identity is a unique internal
	 * identifier.
	 * <p>
	 * This may be useful for sites which do not have any social character, but
	 * instead render a service to individual users.
	 */
	OptionalIdentity;

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return ordinal();
	}
}
