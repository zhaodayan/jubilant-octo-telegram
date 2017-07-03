package cn.mazu.auth.mvc;

import cn.mazu.auth.Session;
import cn.mazu.auth.serv.AbstractPasswordService;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.widget.kit.FormModel;

public class LoginModel extends FormModel{
   public static final String username = "user-name";
   public static final String passwd = "user-password";
   public static final String loginid = "loginid";
   //public static final String 
   private AbstractPasswordService auth_;
   public LoginModel() {
	  this.addField(username);
	  this.addField(passwd);
	  this.addField(loginid);
   }
   //验证用户名和密码是否匹配
   public Object loginValidate(Session session_){
	   Entity entity = new Entity();
	   return entity.validateLoginPass(AccountPermission.class,
			                           this.getValue(username).toString(), this.getValue(passwd).toString(),session_);
   }
}
