package cn.mazu.base.mvc;

import java.text.SimpleDateFormat;

import cn.mazu.base.entity.Dept;
import cn.mazu.base.entity.Staff;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.util.Util.UserGender;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;

public class StaffModel extends FormModel{
    public static final  String name = "name-field";
    public static final  String nationID = "nationID-field";
    public static final String gender = "gender-field";
    public static final String birthDate = "birthDate-field";
    public static final String nativePlace = "nativePlace-field";
    public static final String education = "education-field";
    public static final String joinDate = "joinDate-field";
	public static final String linktel = "linktel-field";
	public static final String idfield = "id-field";
	public static final String deptfield = "dept-field";
	public static final String remark = "remark-field";
	public static final String email = "email-field";
//	private Entity entity;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	public StaffModel() {
//	  entity = new Entity();
	  Validator generalV = new Validator();

	  RegExpValidator telV = new RegExpValidator();
	  telV.setInvalidNoMatchText(tr("badform"));
	  telV.setRegExp("(\\d{3,4}-)?(\\d{7,8})|(13[0-9]{9}|15[0-9]{9}|18[0-9]{9})");
	  
	  this.addField(name);
	  this.setValidator(name, generalV);  
	  this.addField(nationID);
//	  this.setValidator(nationID, generalV);
	  this.addField(gender);
//	  this.setValidator(gender, generalV);
	  this.addField(birthDate);
	  this.addField(joinDate);
	  this.addField(nativePlace);
//	  this.setValidator(nativePlace, generalV);
	  this.addField(education);
//	  this.setValidator(education, generalV);
	  this.addField(remark);
	  this.addField(deptfield);
//	  this.setValidator(deptfield, generalV);
	  
	  this.addField(linktel);
//	  this.setValidator(linktel, telV);
	  
	  this.addField(email);
	  this.addField(idfield);
	}
	public boolean saveOrUpdate(Entity entity,Staff staff){
		 staff.setLinktel(this.valueText(linktel));
		 staff.setEmail(this.valueText(email));
		 staff.setName(this.valueText(name));
		 staff.setNationID(this.valueText(nationID));
		 staff.setEducation(this.valueText(education));
		 staff.setRemark(this.valueText(remark));
		 staff.setNativePlace(this.valueText(nativePlace));
		 if(this.getValue(gender)!=null)
		    staff.setGender((UserGender) this.getValue(gender));
		 if(this.getValue(deptfield)!=null)
		    staff.setDept((Dept)this.getValue(deptfield));
	 	   try{
	 		  if(this.valueText(birthDate)!=null&&!this.valueText(birthDate).equals(""))
	 			  staff.setBirthDate(sdf.parse(this.valueText(birthDate)));
	 		  if(this.getValue(joinDate)!=null&&!this.valueText(joinDate).equals(""))
	 			  staff.setJoinDate(sdf.parse(this.valueText(joinDate)));
	 	   }catch(Exception e){
	 		   e.printStackTrace();
	 	   }
	 	  return entity.saveOrUpdate(staff); 
	}
}
