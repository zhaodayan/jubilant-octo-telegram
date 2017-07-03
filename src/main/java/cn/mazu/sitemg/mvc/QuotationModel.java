package cn.mazu.sitemg.mvc;

import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Contact;
import cn.mazu.sitemg.entity.Quotation;
import cn.mazu.widget.kit.FormModel;

public class QuotationModel extends FormModel{
	
	public static final String modTypeField = "modType";
	public static final String modNumField = "modNum";
	public static final String mobNumField = "mobNum";
	public static final String mxtNumField = "mxtNum";
	public static final String mdqNumField = "mdqNum";
	public static final String cnameField = "cname";
	public static final String cteleField = "ctele";
	public static final String clinkField = "clink";
	public static final String emailField = "cemail";
	public static final String caddrField = "caddr";
	public static final String timeEstField = "timeEst";
	public static final String costEstField = "costEst";
	
	public SitemgEntity smgentity = new SitemgEntity();
	
	public QuotationModel(){
		reset();
	}
	
	public void reset(){
		this.addField(modTypeField);
		this.addField(modNumField);
		this.addField(mobNumField);
		this.addField(mxtNumField);
		this.addField(mdqNumField);
		this.addField(cnameField);
		this.addField(cteleField);
		this.addField(clinkField);
		this.addField(emailField);
		this.addField(caddrField);
		this.addField(timeEstField);
		this.addField(costEstField);
	}

	/*public boolean saveOrUpdate(Quotation q){
		Transaction t = smgentity.getDatabase().startTransaction();
		q.setCaddr(this.valueText(caddrField));
		q.setClink(this.valueText(clinkField));
		q.setCmail(this.valueText(emailField));
		q.setCname(this.valueText(cnameField));
		q.setCtele(this.valueText(cteleField));
		q.setMdqNum(Integer.valueOf(this.valueText(mdqNumField)));
		q.setModNum(Integer.valueOf(this.valueText(modNumField)));
		q.setMxtNum(Integer.valueOf(this.valueText(mxtNumField)));
		q.setType();
		
		
		
	}*/
}
