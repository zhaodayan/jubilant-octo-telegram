/*package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.doc.mvc.SignDocForm;
import cn.mazu.flow.entity.WOTraceFlow;
import cn.mazu.flow.mvc.WOTFlowForm;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

//基础流程库
public class FlowMgr extends MiddleTpl {
	public FlowMgr() {
	  super("cn.mazu.flow.entity.WOTraceFlow",new Object[]{"like","dataCode",
			  WApplication.getInstance().getEnvironment().getCookie("dataCode"),"and"});
	}
   @Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args);
	}
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		WOTraceFlow el = (WOTraceFlow) hb.getDeliveryObj();
		return new WOTFlowForm(title,el, p, FlowMgr.this).draw(getMyParent(), null);
	}
    
}

*/