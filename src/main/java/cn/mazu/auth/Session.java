package cn.mazu.auth;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

/*import cn.mazu.auth.db.UserDatabase;
import cn.mazu.auth.entity.AuthInfo;*/
import cn.mazu.auth.serv.AuthService;
import cn.mazu.auth.serv.PasswordService;
import cn.mazu.mysql.DbFactory;
import edu.vt.middleware.password.CharacterCharacteristicsRule;
import edu.vt.middleware.password.DigitCharacterRule;
import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.LowercaseCharacterRule;
import edu.vt.middleware.password.NonAlphanumericCharacterRule;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.UppercaseCharacterRule;
import edu.vt.middleware.password.WhitespaceRule;

public class Session {

	static AuthService myAuthService;
	static PasswordService myPasswordService;
	//static List<OAuthService> myOAuthServices;

	private EntityManager entityManager_;
	//private UserDatabase userDatabase_;
	//private Login login_;
	
	public static void configureAuth() {
		if (myAuthService != null) 
			return;
		
		myAuthService = new AuthService();
		myPasswordService = new PasswordService(myAuthService);
		//myOAuthServices = new ArrayList<OAuthService>();
		
		myAuthService.setAuthTokensEnabled(true, "loginCookie");
		//myAuthService.setEmailVerificationEnabled(true);

		PasswordVerifier verifier = new PasswordVerifier();
		verifier.addHashFunction(new BCryptHashFunction());
		myPasswordService.setVerifier(verifier);
		myPasswordService.setAttemptThrottlingEnabled(true);
		
		//myPasswordService.setStrengthValidator(new PasswordStrengthValidator(createStrengthValidator()));
		
	}
	
	private static PasswordValidator createStrengthValidator() {
		List<Rule> ruleList = new ArrayList<Rule>();
		
		// password must be between 8 and 16 chars long
		LengthRule lengthRule = new LengthRule(8, 16);
		ruleList.add(lengthRule);

		// don't allow whitespace
		WhitespaceRule whitespaceRule = new WhitespaceRule();
		ruleList.add(whitespaceRule);

		// control allowed characters
		//CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
		//System.out.println("charRule.getNumberOfCharacteristics():"+charRule.getNumberOfCharacteristics());
		// require at least 1 digit in passwords
		//charRule.getRules().add(new DigitCharacterRule(1));
		// require at least 1 non-alphanumeric char
		//charRule.getRules().add(new NonAlphanumericCharacterRule(1));
		// require at least 1 upper case char
		//charRule.getRules().add(new UppercaseCharacterRule(1));
		// require at least 1 lower case char
		//charRule.getRules().add(new LowercaseCharacterRule(1));
		// require at least 3 of the previous rules be met
		/*charRule.setNumberOfCharacteristics(0);
		ruleList.add(charRule);*/
		
		return new PasswordValidator(ruleList);
	}

	public Session(EntityManager entityManager) {
		try {
			entityManager_ = entityManager;
			//login_ = new Login();
			//userDatabase_ =   (UserDatabase) DbFactory.createDatabase();//new UserDatabase(entityManager_);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public AuthInfo getAuthInfo() {
		return ((UserDatabase)userDatabase_).find(login_.getUser());
	}*/

	/*public UserDatabase getUserDatabase() {
		return  userDatabase_;
	}*/

	/* */

	public static AuthService getAuth() {
		return myAuthService;
	}

	public static PasswordService getPasswordAuth() {
		return myPasswordService;
	}

//	public static List<OAuthService> getOAuth() {
//		return myOAuthServices;
//	}

	
}
