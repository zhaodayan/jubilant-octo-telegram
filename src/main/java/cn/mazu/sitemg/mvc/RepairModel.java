package cn.mazu.sitemg.mvc;

import cn.mazu.widget.kit.FormModel;

public class RepairModel extends FormModel{
	
	public static final String pronamefield = "product-name";
	public static final String pronumfield = "product-num";
	public static final String maternamefield = "material-name";
	public static final String modnamefield = "module-name";
	public static final String modnumfield = "module-num";
	public static final String cdatefield = "complete-date";
	public static final String rcausefield = "repair-cause";
	public static final String orequestfield = "other-request";
	public static final String applicantfield = "applicant";
	public static final String adatefield = "apply-date";
	
	public RepairModel(){
		reset();
	}
	
	public void reset(){
		this.addField(pronamefield);
		this.addField(pronumfield);
		this.addField(maternamefield);
		this.addField(modnamefield);
		this.addField(modnumfield);
		this.addField(cdatefield);
		this.addField(rcausefield);
		this.addField(orequestfield);
		this.addField(applicantfield);
		this.addField(adatefield);
	}

}
