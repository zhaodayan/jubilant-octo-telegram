package cn.mazu.sitemg.mvc;

import cn.mazu.widget.kit.FormModel;

public class ConsultModel extends FormModel{
	
	public static final String titlefield = "title";
	public static final String namefield = "name";
	public static final String addrfield = "addr";
	public static final String companyfield = "company";
	public static final String emailfield = "email";
	public static final String telefield = "tele";
	public static final String faxfield = "fax";
	public static final String contentfield = "content";
	
	public ConsultModel(){
		reset();
	}
	public void reset(){
		this.addField(addrfield);
		this.addField(companyfield);
		this.addField(contentfield);
		this.addField(emailfield);
		this.addField(faxfield);
		this.addField(namefield);
		this.addField(telefield);
		this.addField(titlefield);
	}

}
