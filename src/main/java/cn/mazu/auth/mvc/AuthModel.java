/*package cn.mazu.auth.mvc;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.WObject;
import cn.mazu.auth.AuthTokenResult;
import cn.mazu.auth.EmailTokenResult;
import cn.mazu.auth.Identity;
import cn.mazu.auth.Login;
import cn.mazu.auth.PasswordResult;
import cn.mazu.auth.User;
import cn.mazu.auth.db.UserDatabase;
import cn.mazu.auth.serv.AuthService;
import cn.mazu.utils.WString;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.webkit.servlet.WEnvironment;
import cn.mazu.widget.kit.web.interact.InteractWidget;
import cn.mazu.widget.validator.Validator;

public class AuthModel extends FormBaseModel {
	public static Logger logger = LoggerFactory.getLogger(AuthModel.class);
	
	public static final String PasswordField = "user-password";
	public static final String RememberMeField = "remember-me";
	
	private int throttlingDelay_;

	public AuthModel(AuthService baseAuth, UserDatabase users,
			WObject parent) {
		super(baseAuth, users, parent);
		this.throttlingDelay_ = 0;
		this.reset();
	}
	
	public AuthModel(AuthService baseAuth, UserDatabase users){
		this(baseAuth, users, null);
	}
	
	@Override
	public void reset(){
		this.addField(LoginNameField);
		this.addField(PasswordField);
		int days = this.getBaseAuth().getAuthTokenValidity()/24/60;
		WString info = new WString();
		if(days%7 !=0)
			info = tr("remember-me-info.days").arg(days);
		else
			info = tr("remember-me-info-weeks").arg(days/7);
		this.addField(RememberMeField, info);
		this.setValidation(RememberMeField, new Validator.Result(Validator.State.Valid, info));
	}
	
	@Override
	public boolean isVisible(String field){
		if(field == RememberMeField){
			return this.getBaseAuth().isAuthTokensEnabled();
		}
		return true;
	}
	
	@Override
	public boolean validateField(String field){
		if(field == RememberMeField)
			return true;
		User user = this.getUsers().findWithIdentity(Identity.LoginName, 
				this.valueText(LoginNameField));
		if(field == LoginNameField){
			if(user.isValid())
				this.setValid(LoginNameField);
			else{
				this.setValidation(LoginNameField, new Validator.Result(
						Validator.State.Invalid, "<span class='span'>"+
						WString.tr("user-name-invalid")+"</span>"));
				this.throttlingDelay_ = 0;
			}
			return user.isValid();
		} else {
			if(field == PasswordField){
				if(user.isValid()){
					PasswordResult r = this.getPasswordAuth().verifyPassword(
							user, this.valueText(PasswordField));
					switch(r){
						case PasswordInvalid:
							this.setValidation(PasswordField, new Validator.Result(Validator.State.Invalid,
									"<span class='span'>"+tr("user-password-invalid")+"</span>"));
							if(this.getPasswordAuth().isAttemptThrottlingEnabled()){
								this.throttlingDelay_ = this.getPasswordAuth().delayForNextAttempt(user);
							}
							return false;
						case LoginThrottling:
							this.setValidation(PasswordField,
									new Validator.Result(Validator.State.Invalid, 
											"<span class='span'>"+tr("user-password-info")+"</span>"));
							this.setValidated(PasswordField, false);
							this.throttlingDelay_ = this.getPasswordAuth().delayForNextAttempt(user);
							logger.warn(new StringWriter().append("secure:")
									.append("throttling: ").append(String.valueOf(this.throttlingDelay_))
									.append(" seconds for ").append(user.getIdentity(Identity.LoginName)).toString());
							return false;
						case PasswordValid:
							this.setValid(PasswordField);
							return true;
					}
					return false;
				}else {
					return false;
				}
			}else
				return false;
		}
	}
	
	@Override
	public boolean validate(){
		UserDatabase.Transaction t = this.getUsers().startTransaction();
		boolean result = super.validate();
		if(t!=null)
			t.commit();
		return result;
	}
	
	public void configureThrottling(InteractWidget button){
		if(this.getPasswordAuth() != null && this.getPasswordAuth().isAttemptThrottlingEnabled()){
			WApplication app = WApplication.getInstance();
			app.loadJavaScript("js/AuthModel.js", wtjs1());
			button.setJavaScriptMember("AuthThrottle", "new Wt3_3_1.AuthThrottle(Wt3_3_1,"
					+ button.getJsRef()
					+","
					+WString.toWString(tr("throttle-retry"))
					.getJsStringLiteral() + ");");
			
		}
	}
	
	private static WJavaScriptPreamble wtjs1() {
		return new WJavaScriptPreamble(JavaScriptScope.WtClassScope, 
				JavaScriptObjectType.JavaScriptPrototype.JavaScriptFunction, 
				"AuthThrottle", 
				"function(e,a,h){function f(){" +
				"clearInterval(b);" +
				"b = null;" +
				"e.setHtml(a,d);" +
				"a.disabled = false;" +
				"d=null}" +
				"function g(){" +
				"if(c==0)f();" +
				"else{e.setHtml(a,h.replace(\"{1}\",c));--c}}" +
				"jQuery.data(a,\"throttle\",this);" +
				"var b =null,d=null,c=0;" +
				"this.reset=function(i){" +
				"b&&f();" +
				"d=a.innerHTML;" +
				"if(c==i){" +
				"b=setInterval(g,1E3);" +
				"a.disabled=true;" +
				"g()}}}");
	}

	public void updateThrottling(InteractWidget button){
		if(this.getPasswordAuth() != null &&
				this.getPasswordAuth().isAttemptThrottlingEnabled()){
			StringBuffer s = new StringBuffer();
			s.append("jQuery.data(")
			.append(button.getJsRef())
			.append(",'throttle').reset(").append(this.throttlingDelay_)
			.append(");");
			button.doJavaScript(s.toString());
		}
	}

	public synchronized boolean login(Login login){
		if(this.isValid()){
			User user = this.getUsers().findWithIdentity(Identity.LoginName, 
					this.valueText(LoginNameField));
			if(user.isValid()){
				Object v = this.getValue(RememberMeField);
				if(!(v == null)&& (Boolean)v == true){
					WApplication app = WApplication.getInstance();
					app.setCookie(this.getBaseAuth().getAuthTokenCookieName(), 
							this.getBaseAuth().createAuthToken(user), 
							this.getBaseAuth().getAuthTokenValidity()*60);
				}
				login.login(user);
				return true;
			}
		}
		return false;
	}
	
	public void logout(Login login){
		if(login.isLoggedIn()){
			if(this.getBaseAuth().isAuthTokensEnabled()){
				WApplication app = WApplication.getInstance();
				app.removeCookie(this.getBaseAuth().getAuthTokenCookieName());
			}
			login.logout();
		}
	}
	
	public EmailTokenResult processEmailToken(String token){
		return this.getBaseAuth().processEmailToken(token, this.getUsers());
	}
	
	public User processAuthToken(){
		WApplication app = WApplication.getInstance();
		WEnvironment env = app.getEnvironment();
		if(this.getBaseAuth().isAuthTokensEnabled()){
			String token = env.getCookieValue(this.getBaseAuth().getAuthTokenCookieName());
			if(token != null){
				AuthTokenResult result = this.getBaseAuth().processAuthToken(token, this.getUsers());
				switch(result.getResult()){
				case Valid:
					app.setCookie(this.getBaseAuth().getAuthTokenCookieName(), result.getNewToken(), result.getNewTokenValidity());
					//WebSession.getInstance().setApplication(app);
					//env.setCookieValue(this.getBaseAuth().getAuthTokenCookieName(), result.getNewToken());
					return result.getUser();
				case Invalid:
					app.setCookie(this.getBaseAuth().getAuthTokenCookieName(), "", 0);
					return new User();
				}
			}
		}
		return new User();
	}
	
}
*/