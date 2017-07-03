package cn.mazu.mysql;

import cn.mazu.util.HibernateUtil;

public class DbFactory {
	public static AbstractDatabase createDatabase(){
		return HibernateUtil.getDatabase();
	}
}
