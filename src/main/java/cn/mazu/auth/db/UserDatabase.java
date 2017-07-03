package cn.mazu.auth.db;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.auth.PasswordHash;
import cn.mazu.auth.Require;
/*import cn.mazu.auth.Token;
import cn.mazu.auth.User;
import cn.mazu.auth.User.EmailTokenRole;
import cn.mazu.auth.User.Status;
import cn.mazu.auth.db.AbstractUserDatabase;
import cn.mazu.auth.entity.AbstractUser;
import cn.mazu.auth.entity.AuthIdentity;
import cn.mazu.auth.entity.AuthInfo;
import cn.mazu.auth.entity.AuthToken;
*/import cn.mazu.mysql.Database;
import cn.mazu.utils.WDate;


/**
 * A JPA implementation for user authentication data.
 */
public class UserDatabase extends AbstractUserDatabase {

	private int openTransactions = 0;
	private boolean commitTransaction = true;
	
	private static Logger logger = LoggerFactory.getLogger(UserDatabase.class);

	/**
	 * Constructor
	 */
	public UserDatabase(EntityManager entityManager) {
		super(entityManager);
		//entityManager_ = entityManager;
		maxAuthTokensPerUser_ = 50;
	}
//	public UserDatabase(EntityManager entityManager) {
//		entityManager_ = entityManager;
//		maxAuthTokensPerUser_ = 50;
//	}

//	
//	public Transaction startTransaction() {
//		return new TransactionImpl(this);
//	}

	/**
	 * Returns the {@link AuthInfo} object corresponding to an {@link User}.
	 */
	/*public AuthInfo find(User user) {
		return findAuthInfo(user.getId());
	}

    //方法的访问级别被改private-public
	public AuthInfo findAuthInfo(String id) {

//		long id_long = Long.parseLong(id);
//		return entityManager_.find(AuthInfo.class, id_long);
		return entityManager_.find(AuthInfo.class, id);
	}

	*//**
	 * Returns the {@link User} corresponding to an {@link AuthInfo} object.
	 *//*
	public User find(AuthInfo user) {
		return new User(user.getId() + "", this);
	}
     
	public User findWithAbs(String abs_id) {
		AbstractUser absUser = findAbsUserById(abs_id);
		AuthInfo ai = findAiByAbs(absUser.getId());
		if (ai != null)
			return new User(ai.getId(), this);
		else
			return new User();
	}
	public synchronized void setIdentity(User user, String provider, String id) {
		this.removeIdentity(user, provider);
		this.addIdentity(user, provider, id);
	}
	public int updateAuthToken(User user, String hash, String newHash) {
		logger.warn(new StringWriter().append(
				new Require("updateAuthToken()", AUTH_TOKEN).toString())
				.toString());
		return -1;
	}
	
	public synchronized User findWithIdentity(String provider, String identity) {
		String q = 
			"select a_info " +
			"	from AuthInfo a_info, AuthIdentity a_id " +
			"	where a_id.provider = :provider" +
			"		and a_id.identity = :identity" +
			"		and a_id.authInfo.id = a_info.id";

		Query query = entityManager_.createQuery(q);
		query.setParameter("provider", provider);
		query.setParameter("identity", identity);
		List<AuthInfo> ai = (List<AuthInfo>) query.getResultList();

		if (ai.size() == 1)
			return new User(ai.get(0).getId() + "", this);
		else
			return new User();
	}

	
	public String getIdentity(User user, String provider) {
		String q = "select a_id from AuthIdentity a_id "
				+ "	where a_id.authInfo.id = :user_id"
				+ "		and a_id.provider = :provider";

		Query query = entityManager_.createQuery(q);
		query.setParameter("user_id", user.getId());
		query.setParameter("provider", provider);

		List<AuthIdentity> result = (List<AuthIdentity>) query.getResultList();
		if (result.size() == 1)
			return result.get(0).getIdentity();
		else
			return "";
	}
	public void removeIdentity(User user, String provider) {
		String q = "delete from AuthIdentity a_id"
				+ "	where a_id.authInfo.id = :user_id"
				+ "		and a_id.provider = :provider";

		Query query = entityManager_.createQuery(q);
		query.setParameter("user_id", user.getId());
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	
	public User registerNew() {
		AbstractUser user = new AbstractUser();
		AuthInfo ai = new AuthInfo();
		ai.setUser(user);
		entityManager_.persist(ai);

		return new User(ai.getId() + "", this);
	}

	
	
	public User.Status getStatus(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return ai.getStatus();
	}

	
	public void setPassword(User user, PasswordHash password) {
		AuthInfo ai = findAuthInfo(user.getId());
		ai.setPassword(password.getValue(), password.getFunction(),
				password.getSalt());
	}

	
	public PasswordHash getPassword(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return new PasswordHash(ai.getPasswordMethod(), ai.getPasswordSalt(),
				ai.getPasswordHash());
	}

	
	public synchronized void addIdentity(User user, String provider, String identity) {
		User u = findWithIdentity(provider, identity);

		if (u.isValid()) {
			logger.error("cannot add identity " + provider + ":'" + identity
					+ "': already exists");
		} else {
			AuthInfo ai = findAuthInfo(user.getId());
			AuthIdentity a_id = new AuthIdentity(provider, identity);
			ai.getAuthIdentities().add(a_id);
			a_id.setAuthInfo(ai);
			entityManager_.persist(ai);
		}
	}
	 //���¸�����Ϣ�е�tel����ʵ�����Ա�
	public boolean setTNG(String loginId,String tel,String name,String gender){
		AbstractUser cur_user = findAbsUserById(loginId);
		if(cur_user!=null){
			AuthInfo ai = findAiByAbs(cur_user.getId());
			cur_user.setTel(tel);
            //cur_user.setGender(Integer.valueOf(gender));
			cur_user.setName(name);
			if(ai!=null){
				ai.setUser(cur_user);
				entityManager_.persist(ai);
			}
			return true;
		}
		return false;
	}
	public synchronized boolean setEmail(User user, String address) {
		Query query = entityManager_
				.createQuery("select a_info from AuthInfo a_info where email = :email");
		query.setParameter("email", address);
		if (query.getResultList().size() != 0) {
			return false;
		} else {
			AuthInfo ai = findAuthInfo(user.getId());
			ai.setEmail(address);
			return true;
		}
	}
	
	
	public String getEmail(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return ai.getEmail();
	}
	
	
	public boolean setName(User user, String name){
		AuthInfo ai = findAuthInfo(user.getId());
		if(!(ai!=null))
			return false;
		else{
			ai.getUser().setName(name);
			return true;
		}
	}

	
	public boolean setGender(User user, String sex) {
		AuthInfo ai = findAuthInfo(user.getId());
		if(!(ai!=null))
			return false;
		else{
			ai.getUser().setGender(Integer.valueOf(sex));
			return true;
		}
	}
	
	
	public boolean setBirth(User user, Date date) {
		AuthInfo ai = findAuthInfo(user.getId());
		if(!(ai!=null))
			return false;
		else{
			ai.getUser().setBirth(date);
			return true;
		}
	}

	
	public boolean setCompany(User user, String company) {
		AuthInfo ai = findAuthInfo(user.getId());
		if(!(ai!=null))
			return false;
		else{
			ai.getUser().setCompany(company);
			return true;
		}
	}

	
	public boolean setTel(User user, String tel) {
		AuthInfo ai = findAuthInfo(user.getId());
		if(!(ai!=null))
			return false;
		else{
			ai.getUser().setTel(tel);
			return true;
		}
	}

	
	public void setUnverifiedEmail(User user, String address) {
		AuthInfo ai = findAuthInfo(user.getId());
		ai.setUnverifiedEmail(address);
	}

	
	public String getUnverifiedEmail(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return ai.getUnverifiedEmail();
	}

	
	public User findWithEmail(String address) {
		Query query = entityManager_
				.createQuery("select a_info from AuthInfo a_info where email = :email");
		query.setParameter("email", address);
		List<AuthInfo> result = (List<AuthInfo>) query.getResultList();
		if (result.size() == 1)
			return new User(result.get(0).getId() + "", this);
		else
			return new User();
	}

	public AuthInfo findAinfoWithEmail(String address) {
		Query query = entityManager_
				.createQuery("select a_info from AuthInfo a_info where email = :email");
		query.setParameter("email", address);
		List<AuthInfo> result = (List<AuthInfo>) query.getResultList();
		if (result.size() > 0)
			return result.get(0);
		return null;
	}
	
	public synchronized void setEmailToken(User user, Token token, User.EmailTokenRole role) {
		Transaction t = startTransaction();
		AuthInfo ai = findAuthInfo(user.getId());
		WDate expirationTime = token.getExpirationTime();
		ai.setEmailToken(token.getHash(), expirationTime == null ? null : expirationTime.getDate(),	role);
		t.commit();
	}

	
	public Token getEmailToken(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return new Token(ai.getEmailToken(), new WDate(
				ai.getEmailTokenExpires()));
	}

	
	public User.EmailTokenRole getEmailTokenRole(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return ai.getEmailTokenRole();
	}

	
	public User findWithEmailToken(String token) {
		Query query = entityManager_
				.createQuery("select a_info from AuthInfo a_info where email_token = :token");
		query.setParameter("token", token);
		List<AuthInfo> result = (List<AuthInfo>) query.getResultList();
		if (result.size() == 1)
			return new User(result.get(0).getId() + "", this);
		else
			return new User();
	}

	
	public void addAuthToken(User user, Token token) {
		// This should be statistically very unlikely but also a big
		// security problem if we do not detect it ...
		Query query = entityManager_
				.createQuery("select a_token from AuthToken a_token where value = :token_hash");
		query.setParameter("token_hash", token.getHash());
		if (query.getResultList().size() > 0)
			throw new RuntimeException("Token hash collision");

		// Prevent a user from piling up the database with tokens
		AuthInfo ai = find(user);
		if (ai.getAuthTokens().size() > maxAuthTokensPerUser_)
			return;

		AuthToken at = new AuthToken(token.getHash(), token.getExpirationTime()
				.getDate());
		ai.getAuthTokens().add(at);
		at.setAuthInfo(ai);
		entityManager_.persist(at);
	}
	
	
	public void removeAuthToken(User user, String hash) {
		String q = "delete from AuthToken a_token"
				+ "	where a_token.authInfo.id = :user_id"
				+ "		and a_token.value = :hash";

		Query query = entityManager_.createQuery(q);
		query.setParameter("user_id", user.getId());
		query.setParameter("hash", hash);
		query.executeUpdate();
	}

	
	public User findWithAuthToken(String hash) {
		String q = "select a_info " 
				+ " from AuthToken a_token,  AuthInfo a_info"
				+ "	where a_token.value = :hash"
				+ "		and a_token.expiryDate > :expiryDate"
				+ "	 	and a_token.authInfo.id = a_info.id";

		Query query = entityManager_.createQuery(q);
		query.setParameter("hash", hash);
		query.setParameter("expiryDate", WDate.getCurrentDate().getDate());
		List<AuthInfo> ai = (List<AuthInfo>) query.getResultList();

		if (ai.size() == 1)
			return new User(ai.get(0).getId() + "", this);
//			return new User(ai.get(0).getUser().getId() + "", this);
		else
			return new User();
	}

	
	public void setFailedLoginAttempts(User user, int count) {
		AuthInfo ai = findAuthInfo(user.getId());
		ai.setFailedLoginAttempts(count);
	}

	
	public int getFailedLoginAttempts(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return ai.getFailedLoginAttempts();
	}

	
	public void setLastLoginAttempt(User user, WDate d) {
		AuthInfo ai = findAuthInfo(user.getId());
		ai.setLastLoginAttempt(d.getDate());
	}


	public WDate getLastLoginAttempt(User user) {
		AuthInfo ai = findAuthInfo(user.getId());
		return new WDate(ai.getLastLoginAttempt());
	}
    //更新i-huo个人信息中的tel，真实姓名，性别
	public boolean setTNG(String loginId,String tel,String name,String gender){
		AbstractUser cur_user = findAbsUserById(loginId);
		if(cur_user!=null){
			AuthInfo ai = findAiByAbs(cur_user.getId());
			cur_user.setTel(tel);
            cur_user.setGender(Integer.valueOf(gender));
			cur_user.setName(name);
			if(ai!=null){
				ai.setUser(cur_user);
				entityManager_.persist(ai);
			}
			return true;
		}
		return false;
	}
    //查找AbstractUser
	public AbstractUser findAbsUserById(String abs_id){
		return entityManager_.find(AbstractUser.class, abs_id);
	}
	public AbstractUser findAbsUSerByAI(String ai_id){
		AuthInfo ai = findAuthInfo(ai_id);
		if(ai!=null)
		  return findAbsUserById(ai.getUser().getId());
		return null;
	}
	public AuthInfo findAiByAbs(String abs_id){
		String q = "select ai from AuthInfo ai where ai.user.id='"+abs_id+"'";
		Query query = entityManager_.createQuery(q);
		List<AuthInfo> ai = (List<AuthInfo>) query.getResultList();
		if(ai.size()>0)
			return (AuthInfo)ai.get(0);
		else 
	        return null;
	}
	public AuthIdentity findAidByAbs(String abs_id){
		AuthInfo authinfo = findAiByAbs(abs_id);
		Query query = entityManager_.createQuery("select ai from AuthIdentity ai where ai.authInfo.id=:auth_id");
		query.setParameter("auth_id",authinfo==null?"":authinfo.getId());
		List<AuthIdentity> authidentity = query.getResultList();
		if(authidentity.size()>0)
			return (AuthIdentity)authidentity.get(0);
		else 
	        return null;
	}*/
	
	//private EntityManager entityManager_;
	private int maxAuthTokensPerUser_;

	private static String TEL_VERIFICATION = "tel verification";
	private static String COMPANY_VERIFICATION = "company verification";
	private static String BIRTH_VERIFICATION = "birth verification";
	private static String GENDER_VERIFICATION = "gender verification";
	private static String NAME_VERIFICATION = "name verification";
	private static String EMAIL_VERIFICATION = "email verification";
	private static String AUTH_TOKEN = "authentication tokens";
	private static String PASSWORDS = "password handling";
	private static String THROTTLING = "password attempt throttling";
	private static String REGISTRATION = "user registration";
}
