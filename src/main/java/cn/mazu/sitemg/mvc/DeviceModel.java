package cn.mazu.sitemg.mvc;

import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Device;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

public class DeviceModel extends FormModel{
	
	public static final String namefield = "dname";
	public static final String introfield = "dintro";
	public static final String imguploadfield = "imgupload";
	
	public DeviceModel(){
		reset();
	}
	public void reset(){
		this.addField(namefield);
		Validator generalV = new Validator();
		this.setValidator(namefield, generalV);
		this.addField(introfield);
//		this.setValidator(introfield, generalV);
		this.addField(imguploadfield);
		
	}

	public boolean saveOrUpdate(Entity entity,Device p){
		p.setDname(this.valueText(namefield));
		p.setDintro(this.valueText(introfield));
		
		return entity.saveOrUpdate(p);
	}

}
