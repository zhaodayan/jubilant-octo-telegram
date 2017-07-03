package cn.mazu.ui;

import java.util.EnumSet;

import cn.mazu.WApplication;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Device;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.sitemg.entity.Repair;
import cn.mazu.sitemg.mvc.DeviceView;
import cn.mazu.sitemg.mvc.ProductView;
import cn.mazu.util.Util;
import cn.mazu.utils.WLength;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.HtmlTemplate;
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

public class Deviceinfo extends MiddleTpl{
	public DeviceView pView;
	
	public Deviceinfo(){
		super("cn.mazu.sitemg.entity.Device",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}

	/*@Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args);
	}*/

	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Device dv = (Device) hb.getDeliveryObj();
		return new DeviceView(dv, p, Deviceinfo.this, title).draw(getMyParent(), null);
	}
	@Override
    public boolean removeFromMT() {
		Device selwo = (Device)getHeadButton().getDeliveryObj();
    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
    		String fpath = tr("base.picpath")+"/device/"+selwo.getId()+"/";
    		Util.deleteFiles(fpath,selwo.getId()+"*");
    		return true;
    	}
    	return false;
    }

}
