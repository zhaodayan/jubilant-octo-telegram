/*package cn.mazu.auth;

import cn.mazu.auth.serv.AbstractPasswordService;
import cn.mazu.auth.serv.AbstractPasswordService.StrengthValidatorResult;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.RuleResult;

*//** The default implementation for password strength validation.
*
* This implementation uses http://code.google.com/p/vt-middleware/wiki/vtpassword.
*//*
public class PasswordStrengthValidator extends AbstractPasswordService.AbstractStrengthValidator {
	*//**
	 * Constructor, accepts a configured instance of {@link PasswordValidator}.
	 *//*
	public PasswordStrengthValidator(PasswordValidator validator) {
		this.validator = validator;
	}

	@Override
	public StrengthValidatorResult evaluateStrength(String password, String loginName, String email) {
		PasswordData passwordData = new PasswordData(new Password(password));
		passwordData.setUsername(loginName);
		
		RuleResult result = validator.validate(passwordData);
		String m = "";
		if (validator.getMessages(result).size() > 0)
			m = validator.getMessages(result).get(0);
		if(m.indexOf("non-alphanumeric")!=-1)
			m = m.replace("non-alphanumeric", tr("non-alphanumeric"));
		if(m.indexOf("uppercase")!=-1)
			m = m.replace("uppercase", tr("uppercase"));
		if(m.indexOf("digit")!=-1)
			m = m.replace("digit", tr("digit"));
		return new StrengthValidatorResult(result.isValid(), m, result.isValid() ? 5 : 0);
	}
	
	*//**
	 * Returns the {@link PasswordValidator} instance.
	 *//*
	public PasswordValidator getValidator() {
		return validator;
	}
	
	private PasswordValidator validator;
}
*/