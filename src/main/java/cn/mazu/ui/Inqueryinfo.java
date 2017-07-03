package cn.mazu.ui;

import java.util.EnumSet;

import cn.mazu.WApplication;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Consult;
import cn.mazu.sitemg.entity.Quotation;
import cn.mazu.sitemg.mvc.ConsultView;
import cn.mazu.sitemg.mvc.QuotationView;
import cn.mazu.util.Util;
import cn.mazu.utils.WLength;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;


public class Inqueryinfo extends MiddleTpl{
	
	public Inqueryinfo(){
		super("cn.mazu.sitemg.entity.Consult",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
		// this.btnstr = "filtrate,view,del";
	}

	@Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args);
	}

	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Consult cs = (Consult) hb.getDeliveryObj();
		return new ConsultView(cs,p).draw(getMyParent(), null);
	}
	
	

	
}
