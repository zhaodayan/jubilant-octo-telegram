package cn.mazu.sitemg.mvc;

import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.util.Util.RapidOrNot;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

public class ProductModel extends FormModel{
	
	public static final String namefield = "pname";
	public static final String introfield = "pintro";
	public static final String imguploadfield = "imgupload";
	public static final String rapidfield = "pRapid";
	
	public SitemgEntity smg = new SitemgEntity();
	
	public ProductModel(){
		reset();
	}
	public void reset(){
		this.addField(namefield);
		Validator generalV = new Validator();
		this.setValidator(namefield, generalV);
		this.addField(introfield);
//		this.setValidator(introfield, generalV);
		this.addField(rapidfield);
		this.setValidator(rapidfield, generalV);
		
		this.addField(imguploadfield);
	}
	public boolean saveOrUpdate(Entity entity,Product p){
		
		p.setPname(this.valueText(namefield));
		p.setPintro(this.valueText(introfield));
		p.setRapidOrNot((RapidOrNot)(this.getValue(rapidfield)));
		
		return entity.saveOrUpdate(p);
	}

}
