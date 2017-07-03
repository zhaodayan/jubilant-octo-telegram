package cn.mazu.ui;

import java.lang.reflect.Field;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Dept;
import cn.mazu.base.mvc.DeptForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;

public class DeptMgr extends MiddleTpl {
	public DeptMgr() {
	   super("cn.mazu.base.entity.Dept",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}
    @Override
    public Widget draw(Template parent, String... args) {
    	setPaginationMax(10);
        return super.draw(parent, args);
    }
    @Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Dept el = (Dept) hb.getDeliveryObj();
		return new DeptForm(title,el, p,DeptMgr.this).draw(getMyParent(), null);
	}
    @Override
	public Widget getSpecialTextWidget(EntityObject val,Field field) {
		Text retText = new Text();
	    if(field.getType().getName().equals("cn.mazu.base.entity.Dept")){//父级部门
	    	//Method m ;
	    	//try {
				//m = field.getType().getMethod("getName");
				if(val!=null){
					//Object textName = m.invoke(val);
					retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
				}
			/*} catch (Exception e) {
				e.printStackTrace();
			}*/ 
	    }
	    return retText;
	}
}
