package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.sitemg.mvc.ProductView;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

public class Productinfo extends MiddleTpl{
	
	public Productinfo(){
		super("cn.mazu.sitemg.entity.Product",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}

	/*@Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args);
	}*/

	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Product pd = (Product) hb.getDeliveryObj();
		return new ProductView(pd, p, Productinfo.this, title).draw(getMyParent(), null);
	}

	 @Override
	    public boolean removeFromMT() {
		 Product selwo = (Product)getHeadButton().getDeliveryObj();
	    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
	    		String fpath = tr("base.picpath")+"/product/"+selwo.getId()+"/";
	    		Util.deleteFiles(fpath,selwo.getId()+"*");
	    		return true;
	    	}
	    	return false;
	    }
	   

}
