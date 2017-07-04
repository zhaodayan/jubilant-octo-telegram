/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 
package cn.mazu.auth.mvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.WObject;
import cn.mazu.auth.Identity;
import cn.mazu.auth.IdentityPolicy;
import cn.mazu.auth.Login;
import cn.mazu.auth.PasswordResult;
import cn.mazu.auth.User;
import cn.mazu.auth.db.AbstractUserDatabase;
import cn.mazu.auth.db.UserDatabase;
import cn.mazu.auth.entity.AuthIdentity;
import cn.mazu.auth.entity.AuthInfo;
import cn.mazu.auth.serv.AbstractPasswordService;
import cn.mazu.auth.serv.AuthService;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.utils.WString;
import cn.mazu.webkit.servlet.WebSession;
import cn.mazu.widget.FormView;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.validator.Validator;

*//**
 * Model for implementing a registration view.
 * <p>
 * 
 * This model implements the logic for the registration of a new user. It can
 * deal with traditional username/password registration, or registration of
 * pre-identified users using federated login.
 * <p>
 * The model exposes four fields:
 * <ul>
 * <li>LoginNameField: the login name (used as an identity for the
 * {@link Identity#LoginName} provider) -- this can be an email if the
 * {@link AuthService} is configured to use email addresses as identity</li>
 * <li>ChoosePasswordField: the password</li>
 * <li>RepeatPasswordField: the password (repeated)</li>
 * <li>EmailField: if an email address is to be asked (and is not used as
 * identity).</li>
 * </ul>
 * <p>
 * The largest complexity is in the handling of third party identity providers,
 * which is initiated with a call to
 * {@link RegistrationModel#registerIdentified(Identity identity)
 * registerIdentified()}.
 * <p>
 * When a user is re-identified with the same identity, then the model may
 * require that the (original) user confirms this new identity. The model
 * indicates that this button should be made visible with
 * {@link RegistrationModel#isConfirmUserButtonVisible()
 * isConfirmUserButtonVisible()}, the action to take is determined by
 * {@link RegistrationModel#getConfirmIsExistingUser()
 * getConfirmIsExistingUser()}, and
 * {@link RegistrationModel#existingUserConfirmed() existingUserConfirmed()}
 * ends this process by merging the new identity into the existing user.
 * <p>
 * 
 * @see RegistrationWidget
 *//*
public class RegistrationModel extends FormBaseModel {
	private static Logger logger = LoggerFactory
			.getLogger(RegistrationModel.class);

	public static final String LoginNameField = "user-name";
	*//**
	 * Choose Password field.
	 *//*
	public static final String UserPasswordField = "user-password";
	*//**
	 * Repeat password field.
	 *//*
	public static final String RepeatPasswordField = "repeat-password";
	*//**
	 * Email field (if login name is not email).
	 *//*
	public static final String EmailField = "user-email";
	
	public static final String NameField = "name";
	
	public static final String TelField = "tel";
	public static final String AuthCodeField = "auth-code";
	public static final String PreviousPwdField = "pre-password";
	public static final String personalImgField = "personal-img";

	private Login login_;
	private int minLoginNameLength_;
	private RegistrationModel.EmailPolicy emailPolicy_;
	private Identity idpIdentity_;
	private User existingUser_;
	
	private String loginid = WApplication.getInstance().getEnvironment().getCookie("loginid");
	
	*//**
	 * Enumeration for an email policy.
	 *//*
	public enum EmailPolicy {
		*//**
		 * The email address is not asked for.
		 *//*
		EmailDisabled,
		*//**
		 * A user may optionally provide an email address.
		 *//*
		EmailOptional,
		*//**
		 * A user must provide an email address.
		 *//*
		EmailMandatory;

		*//**
		 * Returns the numerical representation of this enum.
		 *//*
		public int getValue() {
			return ordinal();
		}
	}

	*//**
	 * Method for confirming to be an existing user.
	 *//*
	public enum IdentityConfirmationMethod {
		*//**
		 * Confirm using a password prompt.
		 *//*
		ConfirmWithPassword,
		*//**
		 * Confirm by using an email procedure.
		 *//*
		ConfirmWithEmail,
		*//**
		 * Confirmation is not possible.
		 *//*
		ConfirmationNotPossible;

		*//**
		 * Returns the numerical representation of this enum.
		 *//*
		public int getValue() {
			return ordinal();
		}
	}

	*//**
	 * Constructor.
	 * <p>
	 * Creates a new registration model, using a basic authentication service
	 * and user database.
	 * <p>
	 * The <code>login</code> object is used to indicate that an existing user
	 * was re-identified, and thus the registration process may be aborted.
	 *//*
	public RegistrationModel(AuthService baseAuth, UserDatabase users,
			Login login, WObject parent) {
		super(baseAuth, users, parent);
		this.login_ = login;
		this.minLoginNameLength_ = 2;
		//this.emailPolicy_ = RegistrationModel.EmailPolicy.EmailDisabled;
		this.idpIdentity_ = new Identity();
		this.existingUser_ = new User();
		//Ĭ��IdentityPolicy.LoginNameIdentity;emailVerification_ = false
		if (baseAuth.getIdentityPolicy() != IdentityPolicy.EmailAddressIdentity) {
			if (baseAuth.isEmailVerificationEnabled()) {
				this.emailPolicy_ = RegistrationModel.EmailPolicy.EmailOptional;
			} else {
				this.emailPolicy_ = RegistrationModel.EmailPolicy.EmailDisabled;
			}
		}
		this.reset();
	}

	*//**
	 * Constructor.
	 * <p>
	 * Calls
	 * {@link #RegistrationModel(AuthService baseAuth, AbstractUserDatabase users, Login login, WObject parent)
	 * this(baseAuth, users, login, (WObject)null)}
	 *//*
	public RegistrationModel(AuthService baseAuth, UserDatabase users,
			Login login) {
		this(baseAuth, users, login, (WObject) null);
	}

	*//**
	 * Resets the model.
	 * <p>
	 * This resets the model to initial values, clearing any entered information
	 * (login name, password, pre-identified identity).
	 *//*
	public void reset() {
		this.idpIdentity_ = new Identity();
		this.existingUser_ = new User();
		�����Ǻ�����֤���������ֶζ�Ҫ��
		if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
		} else {
		}
		this.setEmailPolicy(RegistrationModel.EmailPolicy.EmailOptional);
		this.addField(LoginNameField, WString.tr("user-name-info"));
		this.addField(EmailField, WString.tr("user-email-info"));
		this.addField(UserPasswordField, WString.tr("user-password-info"));
		this.addField(RepeatPasswordField, WString.tr("repeat-password-info"));
		//System.out.println("this.emailPolicy_:"+this.emailPolicy_.name());
		//this.setEmailPolicy(this.emailPolicy_);
		
	
	}

	*//**
	 * Returns the login object.
	 *//*
	public Login getLogin() {
		return this.login_;
	}

	*//**
	 * Configures a minimum length for a login name.
	 * <p>
	 * The default value is 4.
	 *//*
	public void setMinLoginNameLength(int chars) {
		this.minLoginNameLength_ = chars;
	}

	*//**
	 * Returns the minimum length for a login name.
	 * <p>
	 * 
	 * @see RegistrationModel#setMinLoginNameLength(int chars)
	 *//*
	public int getMinLoginNameLength() {
		return this.minLoginNameLength_;
	}

	*//**
	 * Configures whether an email address needs to be entered.
	 * <p>
	 * You may specify whether you want the user to enter an email address.
	 * <p>
	 * This has no effect when the IdentityPolicy is EmailAddressIdentity.
	 * <p>
	 * The default policy is:
	 * <ul>
	 * <li>EmailOptional when email address verification is enabled</li>
	 * <li>EmailDisabled otherwise</li>
	 * </ul>
	 *//*
	public void setEmailPolicy(RegistrationModel.EmailPolicy policy) {
		this.emailPolicy_ = policy;
		switch (this.emailPolicy_) {
		case EmailMandatory:
			this.addField(EmailField, WString.tr("user-email-info"));
			break;
		case EmailOptional:
			this.addField(EmailField, WString.tr("optional-email-info"));
			break;
		default:
			break;
		}
	}

	*//**
	 * Returns the email policy.
	 * <p>
	 * 
	 * @see RegistrationModel#setEmailPolicy(RegistrationModel.EmailPolicy
	 *      policy)
	 *//*
	public RegistrationModel.EmailPolicy getEmailPolicy() {
		return this.emailPolicy_;
	}

	*//**
	 * Register a user authenticated by an identity provider.
	 * <p>
	 * Using a 3rd party authentication service such as OAuth, a user may be
	 * identified which is not yet registered with the web application.
	 * <p>
	 * Then, you may still need to allow the user to complete registration, but
	 * because the user already is identified and authenticated, this simplifies
	 * the registration form, since fields related to authentication can be
	 * dropped.
	 * <p>
	 * Returns <code>true</code> if the given identity was already registered,
	 * and has been logged in.
	 *//*
	public synchronized boolean registerIdentified(Identity identity) {
		this.idpIdentity_ = identity;
		if (this.idpIdentity_.isValid()) {
			User user = this.getBaseAuth().identifyUser(this.idpIdentity_,
					this.getUsers());
			if (user.isValid()) {
				this.login_.login(user);
				return true;
			} else {
				switch (this.getBaseAuth().getIdentityPolicy()) {
				case LoginNameIdentity:
					if (this.idpIdentity_.getName().length() != 0) {
						this.setValue(LoginNameField,
								this.idpIdentity_.getName());
					} else {
						if (this.idpIdentity_.getEmail().length() != 0) {
							String suggested = this.idpIdentity_.getEmail();
							int i = suggested.indexOf('@');
							if (i != -1) {
								suggested = suggested.substring(0, 0 + i);
							}
							this.setValue(LoginNameField,
									new WString(suggested));
						}
					}
					break;
				case EmailAddressIdentity:
					if (this.idpIdentity_.getEmail().length() != 0) {
						this.setValue(LoginNameField, new WString(
								this.idpIdentity_.getEmail()));
					}
					break;
				default:
					break;
				}
				if (this.idpIdentity_.getEmail().length() != 0) {
					this.setValue(EmailField, this.idpIdentity_.getEmail());
					this.setValidation(EmailField, new Validator.Result(
							Validator.State.Valid, WString.Empty));
				}
				return false;
			}
		} else {
			return false;
		}
	}

	*//**
	 * Returns the existing user that needs to be confirmed.
	 * <p>
	 * When a user wishes to register with an identity that corresponds to an
	 * existing user, he may be allowd to confirm that he is in fact this
	 * existing user.
	 * <p>
	 * 
	 * @see RegistrationModel#getConfirmIsExistingUser()
	 *//*
	public User getExistingUser() {
		return this.existingUser_;
	}

	*//**
	 * Returns the method to be used to confirm to be an existing user.
	 * <p>
	 * When the ConfirmExisting field is visible, this returns an appropriate
	 * method to use to let the user confirm that he is indeed the identified
	 * existing user.
	 * <p>
	 * The outcome of this method (if it is an online method, like a password
	 * prompt), if successful, should be indicated using
	 * {@link RegistrationModel#existingUserConfirmed() existingUserConfirmed()}.
	 * <p>
	 * 
	 * @see RegistrationModel#existingUserConfirmed()
	 *//*
	public RegistrationModel.IdentityConfirmationMethod getConfirmIsExistingUser() {
		if (this.existingUser_.isValid()) {
			if (!this.existingUser_.getPassword().isEmpty()) {
				return RegistrationModel.IdentityConfirmationMethod.ConfirmWithPassword;
			} else {
				if (this.getBaseAuth().isEmailVerificationEnabled()
						&& this.existingUser_.getEmail().length() != 0) {
					return RegistrationModel.IdentityConfirmationMethod.ConfirmWithEmail;
				}
			}
		}
		return RegistrationModel.IdentityConfirmationMethod.ConfirmationNotPossible;
	}

	*//**
	 * Confirms that the user is indeed an existing user.
	 * <p>
	 * The new identity is added to this existing user (if applicable), and the
	 * user is logged in.
	 *//*
	public void existingUserConfirmed() {
		if (this.idpIdentity_.isValid()) {
			this.existingUser_.addIdentity(this.idpIdentity_.getProvider(),
					this.idpIdentity_.getId());
		}
		this.login_.login(this.existingUser_);
	}

	*//**
	 * Validates the login name.
	 * <p>
	 * This verifies that the login name is adequate (see also
	 * {@link RegistrationModel#setMinLoginNameLength(int chars)
	 * setMinLoginNameLength()}).
	 *//*
	public WString validateLoginName(String userName) {
		switch (this.getBaseAuth().getIdentityPolicy()) {
		case LoginNameIdentity:
			if ((int) userName.length() < this.minLoginNameLength_) {
				return WString.tr("user-name-tooshort").arg(
						this.minLoginNameLength_);
			} else {
				return WString.Empty;
			}
		case EmailAddressIdentity:
			if ((int) userName.length() < 3 || userName.indexOf('@') == -1) {
				return WString.tr("email-invalid");
			} else {
				return WString.Empty;
			}
		default:
			return WString.Empty;
		}
	}

	*//**
	 * Verifies that a user with that name does not yet exist.
	 * <p>
	 * If a user with that name already exists, it may in fact be the same user
	 * that is trying to register again (perhaps using a different
	 * identification method). If possible, we allow the user to confirm his
	 * identity.
	 *//*
	public synchronized void checkUserExists(String userName) {
		this.existingUser_ = this.getUsers().findWithIdentity(
				Identity.LoginName, userName);
	}

	*//**
	 * Performs the registration process.
	 *//*
	public synchronized User doRegister() {
		try {
			if (!(this.getPasswordAuth() != null)
					&& !this.idpIdentity_.isValid()) {
				return new User();
			} else {
				User user = this.getUsers().registerNew();
				//���䱸���򷵻�false������û�б����򷵻ظ��¸��û���authinfo�е�email��Ϣ
				this.getUsers().setEmail(user, this.valueText(RegistrationModel.EmailField));
				//Ĭ�ϳ�ʼ���󣬷���false
				if (this.idpIdentity_.isValid()) {
					//new authIdentity
					user.addIdentity(this.idpIdentity_.getProvider(),
							this.idpIdentity_.getId());
					//�����֤���ǿ�ѡ������loginname����һ��
					if (this.getBaseAuth().getIdentityPolicy() != IdentityPolicy.OptionalIdentity) {
						user.addIdentity(Identity.LoginName,
								this.valueText(LoginNameField));
					}
					String email = "";
					boolean emailVerified = false;
					if (this.idpIdentity_.getEmail().length() != 0) {
						email = this.idpIdentity_.getEmail();
						emailVerified = this.idpIdentity_.isEmailVerified();
					} else {
						if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
							email = this.valueText(LoginNameField);
						} else {
							email = this.valueText(EmailField);
						}
					}
					if (email.length() != 0) {
						if (emailVerified
								|| !this.getBaseAuth()
										.isEmailVerificationEnabled()) {
							user.setEmail(email);
						} else {
							this.getBaseAuth().verifyEmailAddress(user, email);
						}
					}
				} else {
					user.addIdentity(Identity.LoginName,
							this.valueText(LoginNameField));
					this.getPasswordAuth().updatePassword(user,
							this.valueText(UserPasswordField));
				}
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Date StringToDate(String dateStr,String formatStr){
		DateFormat dd=new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public void registerUserDetails(User user) {
//		user.setName(this.valueText(NameField));
//		user.setGender(this.valueText(GenderField));
//		user.setBirth(StringToDate(this.valueText(BirthField),"yyyy/MM/dd"));
//		user.setCompany(this.valueText(CompanyField));
//		user.setTel(this.valueText(TelField));
	}
	
	public boolean isVisible(String field) {
		if (field == LoginNameField) {
			if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.OptionalIdentity) {
				return this.getPasswordAuth() != null
						&& !this.idpIdentity_.isValid();
			} else {
				return true;
			}
		} else {
			if (field == UserPasswordField || field == RepeatPasswordField) {
				return this.getPasswordAuth() != null
						&& !this.idpIdentity_.isValid();
			} else {
				if (field == EmailField) {
					if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
						return false;
					} else {
						if (this.emailPolicy_ == RegistrationModel.EmailPolicy.EmailDisabled) {
							return false;
						} else {
							return true;
						}
					}
				} else {
					return true;
				}
			}
		}
	}

	public boolean isReadOnly(String field) {
		if (super.isReadOnly(field)) {
			return true;
		}
		if (field == LoginNameField) {
			return this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity
					&& this.idpIdentity_.isValid()
					&& this.idpIdentity_.isEmailVerified();
		} else {
			if (field == EmailField) {
				return this.idpIdentity_.isValid()
						&& this.idpIdentity_.isEmailVerified();
			} else {
				return false;
			}
		}
	}

	*//**
	 * model data validate (after validate)
	 *//*
	public synchronized boolean validateField(String field) {
		if (!this.isVisible(field)) {
			return true;
		}
		boolean valid = true;
		WString error = new WString();
		if (field == LoginNameField) {
			//������֤
			error = this.validateLoginName(this.valueText(field));
			//������֤ͨ��
			if ((error.length() == 0)) {
				this.checkUserExists(this.valueText(field));
				AuthInfo ai = this.getUsers().entityManager_.find(AuthInfo.class,this.existingUser_.getId());
				if(ai!=null&&ai.getUser().getId().equals(loginid)){
					this.existingUser_ = new User("", null);
				}
				boolean exists = this.existingUser_.isValid();
				valid = !exists;
				if (exists
						&& this.getConfirmIsExistingUser() == RegistrationModel.IdentityConfirmationMethod.ConfirmWithPassword) {
					error = WString.tr("user-name-exists");
				}
			} else {
				valid = false;
			}
			if (this.isReadOnly(field)) {
				valid = true;
			}
		}else if(field == NameField){
			String name_ = this.valueText(field);
			if(name_==null || name_.trim().equals("")){
				valid = false;
				error = WString.tr("name-noempty");
			}else
				valid = true;
		}else if(field == AuthCodeField){
			String code = this.valueText(field);
			String code_ = WebSession.Handler.getInstance().getRequest().getSession().getAttribute(AuthCode.RANDOMCODEKEY).toString();
			if(code == null || code.trim().equals("")){
				valid = false;
				error = tr("auth-code-error");
			}else if(!code.toUpperCase().equals(code_)){
				valid = false;
				error = tr("auth-code-error");
			}else
				valid = true;
		} else {
			if (field == UserPasswordField) {
				AbstractPasswordService.AbstractStrengthValidator v = this
						.getPasswordAuth().getStrengthValidator();
				if (v != null) {
					Validator.Result r = v.validate(
							this.valueText(UserPasswordField),
							this.valueText(LoginNameField),
							this.valueText(EmailField));
					valid = r.getState() == Validator.State.Valid;
					error = r.getMessage();
				} else {
					valid = true;
				}
			} else if(field ==TelField){
				String tel = this.valueText(TelField);
				if(tel.length() != 0 || !tel.equals("")){
					valid = true;
					//error =""
					Pattern p = Pattern.compile("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)");
					Matcher m = p.matcher(tel);
					if(!m.matches()){
						valid = false;
						error = tr("tel-format-error");
					}
					 
				}
			}else {
				if (field == RepeatPasswordField) {
					//if (this.getValidation(UserPasswordField).getState() == Validator.State.Valid) {
					if (!this.valueText(UserPasswordField).equals(
							this.valueText(RepeatPasswordField))) {
						error = WString.tr("passwords-dont-match");
						valid = (error.length() == 0);
//					}
					} else {
						return true;
					}
				} else {
					if (field == EmailField) {
						String email = this.valueText(EmailField);
						if (email.length() != 0) {
							if ((int) email.length() < 3
									|| email.indexOf('@') == -1) {
								error = WString.tr("email-invalid");
							}
							if ((error.length() == 0)) {
								AuthInfo ai = this.getUsers().findAinfoWithEmail(email);
								if(ai!=null){
									if(ai.getUser().getId().equals(loginid))
										ai =null;
								}
//								User user = this.getUsers()
//										.findWithEmail(email);
//								if (user.isValid()) {
//									error = WString.tr("email-exists");
//								}
								if(ai!=null)
									error = WString.tr("email-exists");
							}
						} else {
							if (this.emailPolicy_ != RegistrationModel.EmailPolicy.EmailOptional) {
								error = WString.tr("email-invalid");
							}
						}
						valid = (error.length() == 0);
						//preiviousPwd
					}else if(field == PreviousPwdField){
						if(this.getPasswordAuth().verifyPassword(this.getUsers().findWithAbs(loginid), this.valueText(PreviousPwdField))==PasswordResult.PasswordValid){
							valid = true;
						}else{
							error = WString.tr("incorrect-pwd");
						}
						  valid = (error.length() == 0);	
					}else {
						return true;
					}
				}
			}
		}
		if (valid) {
			this.setValid(field, error);
		} else {
			this.setValidation(field, new Validator.Result(
					Validator.State.Invalid, error));
		}
		return this.getValidation(field).getState() == Validator.State.Valid;
	}

	*//**
	 * Returns whether an existing user needs to be confirmed.
	 * <p>
	 * This returns whether the user is being identified as an existing user and
	 * he can confirm that he is in fact the same user.
	 *//*
	public boolean isConfirmUserButtonVisible() {
		return this.getConfirmIsExistingUser() != RegistrationModel.IdentityConfirmationMethod.ConfirmationNotPossible;
	}

	public static void validatePasswordsMatchJS(LineEdit password,
			LineEdit password2, Text info2) {
		password2
				.keyWentUp()
				.addListener(
						"function(o) {var i="
								+ info2.getJsRef()
								+ ",o1="
								+ password.getJsRef()
								+ ";if (!$(o1).hasClass('Wt-invalid')) {if (o.value == o1.value) {$(o).removeClass('Wt-invalid');Wt3_3_1.setHtml(i,"
								+ WString
										.toWString(WString.tr("valid"))
										.getJsStringLiteral()
								+ ");} else {$(o).removeClass('Wt-valid');Wt3_3_1.setHtml(i,"
								+ WString
										.toWString(
												WString.tr("repeat-password-info"))
										.getJsStringLiteral() + ");}}}");
	}
	������������ĸ�������
	public synchronized boolean doUpdate(String loginId) throws UnsupportedEncodingException, MessagingException, IOException{
		Transaction t = this.getUsers().startTransaction();
		try{
		    User user = this.getUsers().findWithAbs(loginId);
		    //System.out.println("user.db:"+user.isValid());
			this.getUsers().setEmail(user, this.valueText(RegistrationModel.EmailField));
			//tel��name��gender->TNG
			if(!loginId.equals(""))
			this.getUsers().setTNG(loginId,this.valueText(RegistrationModel.TelField),this.valueText(RegistrationModel.NameField),
					this.valueText(RegistrationModel.GenderField));
			
			if (this.idpIdentity_.isValid()) {
				//new authIdentity
				user.setIdentity(this.idpIdentity_.getProvider(),
						this.idpIdentity_.getId());
				//�����֤���ǿ�ѡ������loginname����һ��
				if (this.getBaseAuth().getIdentityPolicy() != IdentityPolicy.OptionalIdentity) {
					user.setIdentity(Identity.LoginName,
							this.valueText(LoginNameField));
				}
				String email = "";
				boolean emailVerified = false;
				if (this.idpIdentity_.getEmail().length() != 0) {
					email = this.idpIdentity_.getEmail();
					emailVerified = this.idpIdentity_.isEmailVerified();
				} else {
					if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
						email = this.valueText(LoginNameField);
					} else {
						email = this.valueText(EmailField);
					}
				}
				if (email.length() != 0) {
					if (emailVerified
							|| !this.getBaseAuth()
									.isEmailVerificationEnabled()) {
						user.setEmail(email);
					} else {
						this.getBaseAuth().verifyEmailAddress(user, email);
					}
				}
			} else {
				user.setIdentity(Identity.LoginName,
						this.valueText(LoginNameField));
			}
			t.commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
		}
		return false;
	}
	
	//���������ֶ�
	//���������ֶ�
    public void resetFields(FormView view){
    	List<String> fields = super.getFields();
    	if(view instanceof PswdChange){
    		this.addField(UserPasswordField, WString.tr("user-password-info"));
    		this.addField(RepeatPasswordField, WString.tr("repeat-password-info"));
    		this.addField(PreviousPwdField, WString.tr("pre-password-info"));
    	}else if(view instanceof PersonalView){
    		this.addField(NameField);
    		this.addField(EmailField);
    		this.addField(GenderField);
    		this.addField(TelField);
    		this.addField(LoginNameField);
    		this.addField(personalImgField);
    	}else if(view instanceof RegisView){
    		this.addField(EmailField);
    		if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
    			this.addField(LoginNameField, WString.tr("user-email-info"));
    		} else {
    			this.addField(LoginNameField, WString.tr("reguser-name-info"));
    		}
    		this.addField(UserPasswordField,WString.tr("reguser-password-info"));
    		this.addField(RepeatPasswordField);
    	}else if(view instanceof LoginView){
    		if (this.getBaseAuth().getIdentityPolicy() == IdentityPolicy.EmailAddressIdentity) {
    			this.addField(LoginNameField, WString.tr("user-email-info"));
    		} else {
    			this.addField(LoginNameField, WString.tr("user-name-info"));
    		}
    		this.addField(UserPasswordField);
    	}
    }
    //�ҵ�ihuo-�����趨����
    public synchronized boolean updatePwd(User user){
    	Transaction t = this.getUsers().startTransaction();
    	try{
    		if (!(this.getPasswordAuth() != null)
					&& !this.idpIdentity_.isValid()) {
    			t.commit();
        		return false;
			} else {
				this.getPasswordAuth().updatePassword(user,
					this.valueText(UserPasswordField));
				t.commit();
			}
    		return true;
    	}catch(Exception e){
    		t.rollback();
    		e.printStackTrace();
    		return false;
    	}
    }
	
}
*/