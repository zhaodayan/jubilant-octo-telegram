package cn.mazu.doc;

import java.util.List;

import javax.persistence.Query;

import cn.mazu.doc.entity.Goods;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;

public class SignDocEntity extends Entity {
	//获取采购单明细列表
		public List<Goods> getGoodsList(SignDoc purchase){
			Query query = this.getEntityManager().createQuery("select goods from Goods goods where goods.signDoc =:purchase order by goods.generateDate desc");
			query.setParameter("purchase", purchase);
			return query.getResultList();
		}
		//更新采购单goods
		public boolean updateGoods(Goods goods){
			Transaction t = this.getDatabase().startTransaction();
			try{
				this.getEntityManager().merge(goods);
				t.commit();
				return true;
			}catch(Exception e){
				t.rollback();
				e.printStackTrace();
				return false;
			}
		}
		
		// 删除记录
		public synchronized boolean removeGoods(Goods g) {
			Transaction t = (Transaction) this.getDatabase().startTransaction();
			try {
			   Goods delGood = this.getEntityManager().find(Goods.class, g.getId());
	           this.getEntityManager().remove(delGood);
	           t.commit();
	           return true;
			} catch (Exception e) {
				t.rollback();
				e.printStackTrace();
				return false;
			}
		}
}
