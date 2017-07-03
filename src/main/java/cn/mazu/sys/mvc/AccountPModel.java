package cn.mazu.sys.mvc;


import cn.mazu.WApplication;
import cn.mazu.auth.PasswordHash;
import cn.mazu.auth.Session;
import cn.mazu.base.entity.Staff;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

public class AccountPModel extends FormModel{
   public static final String username = "username-field";
   //public static final String password = "pwd-field";
   public static final String menu = "menu-field";//菜单
   public static final String menuPermission = "mp-field";//菜单的权限
   public static final String eid = "id-field";
   public static final String staff = "staff-field";
   public static final String datacode = "dc-field";
   //public static final String passwordMethod = "BCrypt";
   public AccountPModel() {
	  this.addField(eid);
	  this.addField(menu);
	  this.addField(username);
	  Validator generalV = new Validator();
	  this.setValidator(username, generalV);
	  this.addField(menuPermission);
	  /*this.addField(password);
	  this.setValidator(password, generalV);*/
	  this.addField(datacode);
	  this.setValidator(datacode, generalV);
	  this.addField(staff);
	  this.setValidator(staff, generalV);
   }
   public boolean saveOrUpdate(Entity entity,AccountPermission ap,Session session_){
	   /*System.out.println("this.getValue(username)："+this.getValue(username)+"\tthis.getValue(password)："+
			   "\tthis.getValue(menu):"+this.getValue(menu)+"\tthis.getValue(staff):"+this.getValue(staff)+"\tthis.getValue(menuPermission):"+
			   this.getValue(menuPermission)+"\tthis.getValue(datacode):"+this.getValue(datacode));*/
	   ap.setUsername(this.getValue(username).toString());
	   //ap.setPassword(this.getValue(password).toString());
	   ap.setMenuStr(this.getValue(menu).toString());
	   ap.setStaff((Staff)this.getValue(staff));
	   ap.setMenuPermission(this.getValue(menuPermission).toString());
	   ap.setDataCode(this.getValue(datacode).toString());
	   if(!entity.isPersist(ap)){
		   PasswordHash hp = session_.getPasswordAuth().getVerifier().hashPassword(tr("defaultPwd").toString());
		   ap.setPasswordHash(hp.getValue());
		   ap.setPasswordSalt(hp.getSalt());
		   ap.setPasswordMethod(hp.getFunction());
	   }
	  Transaction t = entity.getDatabase().startTransaction();
	   try{
		  entity.getEntityManager().merge(ap);
		  t.commit();
//		  WApplication.getInstance().setCookie("dataCode",this.getValue(datacode).toString(),0);
		  return true;
	   }catch(Exception e){
		   t.rollback();
		   e.printStackTrace();
	   }
	   return false;
	   //return entity.saveOrUpdate(ap);
   }

}
