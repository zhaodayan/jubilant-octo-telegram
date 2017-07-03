/**
 * 图纸管理
 */
package cn.mazu.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.mazu.WApplication;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.drawing.entity.Drawing;
import cn.mazu.drawing.mvc.DrawingForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;

public class DrawingMgr extends MiddleTpl{
    public DrawingMgr() {
	   super("cn.mazu.drawing.entity.Drawing",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}
    @Override
    public Widget draw(Template parent, String... args) {
    	return super.draw(parent, args);
    }
    
    @Override
    public boolean removeFromMT() {
    	Drawing selwo = (Drawing)getHeadButton().getDeliveryObj();
    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
    		Util.deleteFiles(tr("base.filepath")+"/drawing/"+selwo.getId()+"/","*");
    		return true;
    	}
    	return false;
    }
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
    	Drawing drawing = (Drawing) hb.getDeliveryObj();
    	return new DrawingForm(title,drawing, p, DrawingMgr.this).draw(getMyParent(), null);
	}
	@Override
	public Widget getSpecialTextWidget(EntityObject val, Field field) {
		Text retText = new Text();
		//Method m ;
		//try {
		    if(field.getType().getName().equals("cn.mazu.surface.entity.Surface")||//图面
		    		field.getType().getName().equals("cn.mazu.base.entity.GeneralElement")){//零件
					//m = field.getType().getMethod("getName");
					if(val!=null){
						//Object textName = m.invoke(val);
						retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
					}
		    }/*else if(field.getType().getName().equals("cn.mazu.base.entity.GeneralElement")){
				if(val!=null){
					retText.setText(WidgetUtil.getValFromProperty(val, "getName").toString());
				}
		    }*/
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/ 
	    return retText;
	}
    
}
