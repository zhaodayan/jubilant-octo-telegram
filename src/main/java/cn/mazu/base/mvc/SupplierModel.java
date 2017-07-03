package cn.mazu.base.mvc;

import cn.mazu.base.entity.Supplier;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;

public class SupplierModel extends FormModel {
    public static final String name = "name-field";
    public static final String linkman = "linkman-field";
    public static final String linktel = "linktel-field";
    public static final String eid = "id-field";
    public static final String fax = "fax-field";
    public static final String description = "desc-field";
    public static final String addition = "addition-field";
    public static final String email = "email-field";
    public SupplierModel() {
	   this.addField(name);
	   Validator generalV = new Validator();
		this.setValidator(name, generalV);
		
	   this.addField(linkman);
//	   this.setValidator(linkman, generalV);
	   
	   this.addField(fax);
//	   this.setValidator(fax, generalV);
	   
	   this.addField(linktel);
	   RegExpValidator telV = new RegExpValidator();
	   telV.setInvalidNoMatchText(tr("badform"));
	   telV.setRegExp("(\\d{3,4}-)?(\\d{7,8})|(13[0-9]{9}|15[0-9]{9}|18[0-9]{9})");
//	   this.setValidator(linktel, telV);
	   this.addField(email);
	   this.addField(eid);
	   this.addField(description);
	   this.addField(addition);
	}
    
    public boolean saveOrUpdate(Entity entity,Supplier supplier){
//    	Transaction t = entity.getDatabase().startTransaction();
        supplier.setLinkman(this.getValue(linkman).toString());
        supplier.setLinktel(this.getValue(linktel).toString());
        supplier.setName(this.getValue(name).toString());
        supplier.setLinkfax(this.getValue(fax).toString());
        supplier.setDescription(this.getValue(description).toString());
        supplier.setLinkemail(this.getValue(email).toString());
      /*  try{
        	entity.getEntityManager().merge(supplier);
        	t.commit();
        	return true;
        }catch(Exception e){
        	t.rollback();
        	e.printStackTrace();
        }*/
        return entity.saveOrUpdate(supplier);
    }
}
