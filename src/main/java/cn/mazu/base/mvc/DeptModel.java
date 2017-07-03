package cn.mazu.base.mvc;


import cn.mazu.base.entity.Dept;
import cn.mazu.mysql.Entity;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

public class DeptModel extends FormModel{
   public static final String name = "name-field";
   public static final String code = "code-field";
   public static final String pdept = "pdept-field";
   public static final String eid = "id-field";
//   private Entity entity;
   
   public DeptModel() {
//	   entity = new Entity();
	   Validator generalV = new Validator();
	   
	   this.addField(name);
	   this.setValidator(name, generalV);
	   
	   this.addField(code);
//	   this.setValidator(code, generalV);
	   
	   this.addField(eid);
	   this.addField(pdept);
  }
  
   public boolean saveOrUpdate(Entity entity,Dept dept){
	   
	   dept.setCode(this.valueText(code));
	   dept.setName(this.valueText(name));
       if(this.getValue(pdept)!=null)
    	   dept.setParent((Dept) this.getValue(pdept));
       else
    	   dept.setParent(null);
       return entity.saveOrUpdate(dept);
   }
}
