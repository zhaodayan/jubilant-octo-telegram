package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.sitemg.entity.Quotation;
import cn.mazu.sitemg.mvc.QuotationView;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

public class Askpriceinfo extends MiddleTpl{
	
	public String args[];
	public Template parent;
	public Askpriceinfo(){
		super("cn.mazu.sitemg.entity.Quotation",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
		 //this.btnstr = "filtrate,view,del";
	}

	@Override
	public Widget draw(Template parent, String... args) {
		this.args = args;
		this.parent = parent;
		return super.draw(parent, args);
	}
	
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Quotation qt = (Quotation) hb.getDeliveryObj();
		return new QuotationView(qt,p,Askpriceinfo.this).draw(null, null);
	}
}
