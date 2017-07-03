package cn.mazu.sitemg.mvc;

import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Contact;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;

public class ContactModel extends FormModel{
	
	public static final String addrField = "link-addr";
	public static final String teleField = "link-tele";
	public static final String faxField = "link-fax";
	public static final String manField = "link-man";
	public static final String QQField = "link-QQ";
	public static final String emailField = "link-email";
	public static final String siteField = "link-site";
	
//	public SitemgEntity smgentity = new SitemgEntity();
	public Entity entity;
	public ContactModel(){
		entity = new Entity();
		reset();
	}
	
	public void reset(){
		this.addField(QQField);
		Validator generalV = new Validator();
//		this.setValidator(QQField, generalV);
		this.addField(addrField);
//		this.setValidator(addrField, generalV);
		this.addField(emailField);
//		this.setValidator(emailField, generalV);
		this.addField(faxField);
//		this.setValidator(faxField, generalV);
		this.addField(manField);
//		this.setValidator(manField, generalV);
		this.addField(siteField);
//		this.setValidator(siteField, generalV);
		RegExpValidator telV = new RegExpValidator();
		telV.setInvalidNoMatchText(tr("badform"));
		telV.setRegExp("(\\d{3,4}-)?(\\d{7,8})|(13[0-9]{9}|15[0-9]{9}|18[0-9]{9})");
		this.addField(teleField);
//		this.setValidator(teleField, telV);
	}
	
	public boolean saveOrUpdate(Contact c){
		c.setAddress(this.valueText(addrField));
		c.setCname(this.valueText(manField));
		c.setcQQ(this.valueText(QQField));
		c.setEmail(this.valueText(emailField));
		c.setFax(this.valueText(faxField));
		c.setSiteaddr(this.valueText(siteField));
		c.setTele(this.valueText(teleField));
		return entity.saveOrUpdate(c);
	}

}
