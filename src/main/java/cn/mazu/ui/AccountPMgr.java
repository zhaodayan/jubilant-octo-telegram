package cn.mazu.ui;

import java.lang.reflect.Field;

import cn.mazu.WApplication;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.sys.mvc.AccountPForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Text;

public class AccountPMgr extends MiddleTpl{
   public AccountPMgr() {
      //super("cn.mazu.sys.entity.AccountPermission",new Object[]{"1","1"});
      super("cn.mazu.sys.entity.AccountPermission",new Object[]{"like","dataCode",
    		  Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
    /*  try {
		setHiddenField(curCls.getField("dataCode"));
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchFieldException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
   }
   @Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		AccountPermission ap = (AccountPermission) hb.getDeliveryObj();
   	    return new AccountPForm(title, ap, p, this).draw(getMyParent(), null);
	}
   @Override
   public Widget getSpecialTextWidget(EntityObject val, Field field) {
   	Text retText = new Text();
		    if(field.getType().getName().equals("cn.mazu.base.entity.Staff")){//显示图面名称
				if(val!=null){
					retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
				}
		    }
	    return retText;
   }
}
