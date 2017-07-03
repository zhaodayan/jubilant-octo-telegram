package cn.mazu.mysql;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database extends AbstractDatabase{
	static public Logger logger = LoggerFactory.getLogger(Database.class);
	private int openTransactions = 0;
	private boolean commitTransaction = true;
	
	public Database(EntityManager entityManager){
		this.entityManager_ = entityManager;
	}

	@Override
	public Transaction startTransaction() {
		return new TransactionImpl(this);
	}
	
	public class TransactionImpl implements Transaction {
		private Database db_;
		
		public TransactionImpl(Database userDatabase) {
			this.db_ = userDatabase;
			
			if (userDatabase.openTransactions == 0) 
				beginTransaction();
			
			++userDatabase.openTransactions;
		}

		public void commit() {
			--db_.openTransactions;
			
			if (db_.openTransactions == 0) 
				endTransaction();
		}

		public void rollback() {
			--db_.openTransactions;
			db_.commitTransaction = false;
			System.out.println("db_.openTransactions in rollback:"+db_.openTransactions);
			if (db_.openTransactions == 0) 
				endTransaction();
		}
		
		private void beginTransaction() {
			entityManager_.getTransaction().begin();
			db_.commitTransaction = true;
		}
		
		private void endTransaction() {
			if (db_.commitTransaction){
				try {
					entityManager_.getTransaction().commit();
				} catch (Exception e) {
					beginTransaction();
					entityManager_.getTransaction().rollback();
				}
			}else
				entityManager_.getTransaction().rollback();
		}
	}

	
	
}
