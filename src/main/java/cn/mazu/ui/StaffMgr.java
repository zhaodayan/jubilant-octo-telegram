package cn.mazu.ui;

import java.lang.reflect.Field;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Staff;
import cn.mazu.base.mvc.StaffForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Text;

public class StaffMgr extends MiddleTpl {
	//private MiddleTpl midTpl;
	public StaffMgr() {
		super("cn.mazu.base.entity.Staff",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}
    /*@Override
    public Widget draw(Template parent, String... args) {
    	return super.draw(parent, args);
    }*/
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
     	Staff el = (Staff) hb.getDeliveryObj();
    	return new StaffForm(title, el, p, StaffMgr.this).draw(getMyParent(), null);
	}
	@Override
	public Widget getSpecialTextWidget(EntityObject val,Field field) {
		Text retText = new Text();
	    if(field.getType().getName().equals("cn.mazu.base.entity.Dept")){
	    	//Method m ;
	    	//try {
				//m = field.getType().getMethod("getName");
				if(val!=null){
				//	Object textName = m.invoke(val);
					retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
				}
			/*} catch (Exception e) {
				e.printStackTrace();
			}*/ 
	    }
	    return retText;
	}
}
