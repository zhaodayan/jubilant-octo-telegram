package cn.mazu.sitemg.mvc;

import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Contact;
import cn.mazu.sitemg.entity.QuotLibrary;
import cn.mazu.util.Util.QuotaType;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.Validator;

public class QuolibModelm extends FormModel{
	
	public static final String modcycField = "modcyc";
	public static final String modpriceField = "modprice";
	public static final String mobcycField = "mobcyc";
	public static final String mobpriceField = "mobprice";
	public static final String mxtcycField = "mxtcyc";
	public static final String mxtpriceField = "mxtprice";
	public static final String mdqcycField = "mdqcyc";
	public static final String mdqpriceField = "mdqprice";
	
	public SitemgEntity smgentity = new SitemgEntity();
	
	public QuolibModelm(){
		reset();
	}
	
	public void reset(){
		this.addField(modcycField);
		DoubleValidator generalV = new DoubleValidator();
		this.setValidator(modcycField, generalV);
		this.addField(modpriceField);
		this.setValidator(modpriceField, generalV);
		this.addField(mobcycField);
		this.setValidator(mobcycField, generalV);
		this.addField(mobpriceField);
		this.setValidator(mobpriceField, generalV);
		this.addField(mxtcycField);
		this.setValidator(mxtcycField, generalV);
		this.addField(mxtpriceField);
		this.setValidator(mxtpriceField, generalV);
		this.addField(mdqcycField);
		this.setValidator(mdqcycField, generalV);
		this.addField(mdqpriceField);
		this.setValidator(mdqpriceField, generalV);
	}
	
	public boolean saveOrUpdate(QuotLibrary qtl,QuotaType qt){
		Transaction t = smgentity.getDatabase().startTransaction();
		
		try {
			qtl.setMdqpcycle(Double.valueOf(this.valueText(mdqcycField)));
			qtl.setMdqunitPrice(Double.valueOf(this.valueText(mdqpriceField)));
			qtl.setModpcycle(Double.valueOf(this.valueText(modcycField)));
			qtl.setModunitPrice(Double.valueOf(this.valueText(modpriceField)));
			qtl.setMxtpcycle(Double.valueOf(this.valueText(mxtcycField)));
			qtl.setMxtunitPrice(Double.valueOf(this.valueText(mxtpriceField)));
			qtl.setMobpcycle(Double.valueOf(this.valueText(mobcycField)));
			qtl.setMobunitPrice(Double.valueOf(this.valueText(mobpriceField)));
			qtl.setType(qt);
			smgentity.getEntityManager().merge(qtl);
			t.commit();
			return true;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}

}
