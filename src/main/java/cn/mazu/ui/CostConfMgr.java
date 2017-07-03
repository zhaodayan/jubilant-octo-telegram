package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.cost.entity.CostConf;
import cn.mazu.cost.mvc.CostConfForm;
import cn.mazu.util.Util;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;

public class CostConfMgr extends MiddleTpl{
   
	public CostConfMgr() {
	   super("cn.mazu.cost.entity.CostConf",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
    }
	
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
	    CostConf cc = (CostConf)getHeadButton().getDeliveryObj();
	    return new CostConfForm(this,title,p,cc).draw(getMyParent(),null);
	}
}
