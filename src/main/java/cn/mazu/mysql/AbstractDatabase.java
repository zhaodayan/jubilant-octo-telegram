package cn.mazu.mysql;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.util.EntityObject;

public abstract class AbstractDatabase {
	private static Logger logger = LoggerFactory.getLogger(AbstractDatabase.class);
	
	public EntityManager entityManager_;
	
	
	public Entity findEntity(Class<?> clazz, String id){
		EntityObject o = (EntityObject) entityManager_.find(clazz, id);
		if(o != null)
			return new Entity(o.id, this);
		else
			return new Entity();
	}
	
	public abstract AbstractDatabase.Transaction startTransaction();
	public static interface Transaction {
		public void commit();
		public void rollback();
	}
	
}