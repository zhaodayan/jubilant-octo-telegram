package cn.mazu.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.auth.db.UserDatabase;
import cn.mazu.mysql.AbstractDatabase;

public final class HibernateUtil {
	private static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	private static final ThreadLocal threadLocal = new ThreadLocal();
	public static EntityManagerFactory factory;
	private static UserDatabase database;
	
	static{
		try{
			factory = Persistence.createEntityManagerFactory("enite");
		}catch(Exception e){
			logger.error(e.getMessage(),e.getCause());
		}
	}
	
	public static EntityManager getEntityManager() {
		
		EntityManager em = (EntityManager) threadLocal.get();
		if(em == null || !em.isOpen()){
			em = (factory != null) ? factory.createEntityManager() : null;
			threadLocal.set(em);
			database = new UserDatabase(em);
		}
		return em;
	}
	
	public static AbstractDatabase getDatabase(){
		EntityManager em = (EntityManager) threadLocal.get();
		if(em == null || !em.isOpen()){
			em = (factory != null) ? factory.createEntityManager() : null;
			threadLocal.set(em);
			database = new UserDatabase(em);
		}
		return database;
	}
}
