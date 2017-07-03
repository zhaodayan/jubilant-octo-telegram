package cn.mazu.base.mvc;

import cn.mazu.base.entity.GeneralElement;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.IntValidator;
import cn.mazu.widget.validator.Validator;

public class GeneralElementModel extends FormModel{
   public static final String ename="name-field";//零件名称
   public static final String ecode="code-field";//零件编码
   public static final String eperiod="period-field";//零件加工周期
   public static final String etech="tech-field";//零件加工工艺
   public static final String eid = "id-field";
   
//   public Entity geEntity;
   
   public GeneralElementModel(){
//	   geEntity = new Entity();
	   Validator generalV = new Validator();
	   IntValidator dv = new IntValidator();
	   
	   this.addField(ename);
	   this.setValidator(ename, generalV);
	   
	   this.addField(ecode);
//	   this.setValidator(ecode, generalV);
	   
	   this.addField(eperiod);
	   this.setValidator(eperiod, dv);
	   
	   this.addField(etech);
//	   this.setValidator(etech, generalV);
	   this.addField(eid);
   }
   public boolean saveOrUpdate(Entity entity,GeneralElement gem){
	   
	   gem.setCode(this.valueText(ecode));
	   gem.setName(this.valueText(ename));
	   gem.setPeriod(Short.valueOf(this.valueText(eperiod)));
	   gem.setTechnologyStr(this.valueText(etech));
	   return entity.saveOrUpdate(gem);
   }
}
