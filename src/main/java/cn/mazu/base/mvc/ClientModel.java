package cn.mazu.base.mvc;

import cn.mazu.base.entity.Client;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;

public class ClientModel extends FormModel{
	public static final String ename="name-field";
	public static final String linkman="linkman-field";
	public static final String linktel="linktel-field";
	public static final String linkPhone="linkphone-field";
	public static final String linkFax="linkfax-field";
	public static final String email = "email-field";
	public static final String siteAddr="siteAddr-field";
    public static final String address = "addr-field";
	public static final String addrCode="addrCode-field";
	public static final String linkBank="linkBank-field";
	public static final String lbankNO="lbankNO-field";
    public static final String description = "description-field";
    public static final String addition = "addition-field";
    public static final String eid = "id-field";
    
//    private Entity entity;
    
    public ClientModel(){
//    	entity = new Entity();
    	this.addField(eid);
    	this.addField(ename);
    	Validator generalV = new Validator();
		this.setValidator(ename, generalV);
    	this.addField(linkman);
    	this.addField(linktel);
    	this.addField(linkPhone);
    	this.addField(linkFax);
    	this.addField(email);
    	this.addField(siteAddr);
		this.addField(address);
		this.addField(addrCode);
		this.addField(linkBank);
		this.addField(lbankNO);
		this.addField(description);
		this.addField(addition);
	/*	RegExpValidator telV = new RegExpValidator();
		telV.setInvalidNoMatchText(tr("badform"));
		telV.setRegExp("(\\d{3,4}-)?(\\d{7,8})|(13[0-9]{9}|15[0-9]{9}|18[0-9]{9})");*/
//		this.setValidator(linktel, telV);
    }
    public boolean saveOrUpdate(Entity entity,Client c){
 	   c.setName(this.valueText(ename));
 	   c.setLinkman(this.valueText(linkman));
 	   c.setLinktel(this.valueText(linktel));
 	   c.setLinkphone(this.valueText(linkPhone));
 	   c.setLinkfax(this.valueText(linkFax));
 	   c.setEmail(this.valueText(email));
 	   c.setSiteAddr(this.valueText(siteAddr));
 	   c.setAddress(this.valueText(address));
 	   c.setAddrCode(this.valueText(addrCode));
 	   c.setLinkbank(this.valueText(linkBank));
 	   c.setLbankNO(this.valueText(lbankNO));
       c.setDescription(this.valueText(description));
 	   return entity.saveOrUpdate(c);
    }
}
