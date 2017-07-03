package cn.mazu.sitemg.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.mazu.mysql.Entity;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Notice;
import cn.mazu.util.Util.NoticeType;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.Validator;

public class NoticeModel extends FormModel{
	
	public static final String notitleField = "notitle";
	public static final String notypeField = "notype";
	public static final String nodateField = "nodate";
	public static final String nocontentField = "nocontent";
	
//	public SitemgEntity smg = new SitemgEntity();
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	public NoticeModel(){
		reset();
	}
	public void reset(){
		Validator generalV = new Validator();
		this.addField(notitleField);
		this.setValidator(notitleField, generalV);
		this.addField(notypeField);
		this.setValidator(notypeField, generalV);
		this.addField(nodateField);
		DateValidator dateV = new DateValidator();
//		this.setValidator(nodateField, dateV);
		this.addField(nocontentField);
		this.setValidator(nocontentField, generalV);
	}
	
	public boolean saveOrUpdate(Entity entity,Notice nt){
		
		try {
			nt.setContent(this.valueText(nocontentField));
			nt.setPublishDate(sdf.parse(this.valueText(nodateField)));
			nt.setType((NoticeType)(this.getValue(notypeField)));
			nt.setTitle(this.valueText(notitleField));
//			smg.getEntityManager().merge(nt);
//			t.commit();
//			return true;
		} catch (ParseException e) {
			e.printStackTrace();
//			return false;
		}
		return entity.saveOrUpdate(nt);
		
	}

}
