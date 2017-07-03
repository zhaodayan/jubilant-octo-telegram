/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 
package cn.mazu.auth.mvc;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.WObject;
import cn.mazu.auth.IdentityPolicy;
import cn.mazu.auth.Login;
import cn.mazu.auth.db.UserDatabase;
import cn.mazu.auth.serv.AbstractPasswordService;
import cn.mazu.auth.serv.AuthService;
import cn.mazu.utils.WString;
import cn.mazu.webkit.servlet.WtServlet;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

*//**
 * A base model class for authentication-related forms.
 * <p>
 * 
 * This class manages the the auth services and the user database which an
 * authentication model will use to implement a form..
 *//*
public class FormBaseModel extends FormModel {
	private static Logger logger = LoggerFactory.getLogger(FormBaseModel.class);

	*//**
	 * {@link Login} name field.
	 *//*
	public static final String LoginNameField = "user-name";

	*//**
	 * Constructor.
	 *//*
	public FormBaseModel(AuthService baseAuth, UserDatabase users,
			WObject parent) {
		super(parent);
		this.baseAuth_ = baseAuth;
		this.users_ = users;
		this.passwordAuth_ = null;
		WApplication app = WApplication.getInstance();
		app.getBuiltinLocalizedStrings().useBuiltin(WtServlet.AuthStrings_xml);
	}

	*//**
	 * Constructor.
	 * <p>
	 * Calls
	 * {@link #FormBaseModel(AuthService baseAuth, AbstractUserDatabase users, WObject parent)
	 * this(baseAuth, users, (WObject)null)}
	 *//*
	public FormBaseModel(AuthService baseAuth, UserDatabase users) {
		this(baseAuth, users, (WObject) null);
	}

	*//**
	 * Returns the authentication base service.
	 * <p>
	 * This returns the service passed through the constructor.
	 *//*
	public AuthService getBaseAuth() {
		return this.baseAuth_;
	}

	*//**
	 * Returns the user database.
	 *//*
	public UserDatabase getUsers() {
		return this.users_;
	}

	*//**
	 * Adds a password authentication service.
	 * <p>
	 * This enables password-based registration, including choosing a proper
	 * password.
	 * <p>
	 * Only one password authentication service can be configured.
	 * <p>
	 * 
	 * @see FormBaseModel#addOAuth(OAuthService auth)
	 *//*
	public void addPasswordAuth(AbstractPasswordService auth) {
		this.passwordAuth_ = auth;
	}

	*//**
	 * Returns the password authentication service.
	 * <p>
	 * 
	 * @see FormBaseModel#addPasswordAuth(AbstractPasswordService auth)
	 *//*
	public AbstractPasswordService getPasswordAuth() {
		return this.passwordAuth_;
	}

	public WString label(String field) {
		if (field == LoginNameField
				&& this.baseAuth_.getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
			field = "email";
		}
		return WString.tr(field);
	}

	protected void setValid(String field) {
		this.setValid(field, WString.Empty);
	}

	protected void setValid(String field, CharSequence message) {
		this.setValidation(field,
				new Validator.Result(Validator.State.Valid,
						(message.length() == 0) ? WString.tr("valid")
								: message));
	}

	private AuthService baseAuth_;
	private UserDatabase users_;
	private AbstractPasswordService passwordAuth_;
}
*/