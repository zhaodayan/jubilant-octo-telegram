package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.base.mvc.GeneralElementForm;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;
/*零件通用字典表管理*/
public class GeneralElementMgr extends MiddleTpl {
	//private MiddleTpl mt;
	public GeneralElementMgr() {
	   super("cn.mazu.base.entity.GeneralElement",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
//	   setPaginationMax(10);
	}
   
		@Override
		public Widget generateRelevantForm(String title, PopBox p) {
			HeadButton hb = getHeadButton();
        	GeneralElement el = (GeneralElement) hb.getDeliveryObj();
	    	return new GeneralElementForm(title,el,p,GeneralElementMgr.this).draw(getMyParent(), null);
		}
	
	 
}
