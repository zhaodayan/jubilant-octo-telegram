package cn.mazu.drawing.mvc;

import cn.mazu.base.entity.GeneralElement;
import cn.mazu.drawing.entity.Drawing;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.surface.entity.Surface;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;

public class DrawingModel extends FormModel {
    public static final String eid = "id-field";
    public static final String name = "name-field";//零件名称-零件编号
    public static final String code = "code-field";
    public static final String author = "author-field";
    public static final String drawing = "drawing-field";
    public static final String surface = "surface-field";
    public static final String element = "element-field";
    
    public DrawingModel() {
	   this.addField(eid);
	   this.addField(name);
	   Validator generalV = new Validator();
	   this.addField(code);
	   this.setValidator(code, generalV);
	   this.addField(author);
	   this.addField(drawing);
	   this.addField(surface);
	   this.setValidator(surface, generalV);
	   this.addField(element);
	   this.setValidator(element, generalV);
	}
    public boolean saveOrUpdate(Entity entity,Drawing drawing){
    	if(this.getValue(element)!=null)
    		drawing.setGeneralElement((GeneralElement)this.getValue(element));
        if(this.getValue(surface)!=null)    	
        	drawing.setSurface((Surface)this.getValue(surface));
        if(this.getValue(author)!=null)
        	drawing.setAuthor(this.getValue(author).toString());
        
    	drawing.setCode(this.getValue(code).toString());
    	return entity.saveOrUpdate(drawing);
    }
}
