package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Client;
import cn.mazu.base.entity.Supplier;
import cn.mazu.base.mvc.SupplierForm;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

public class SupplierMgr extends MiddleTpl{
   
	public SupplierMgr() {
	   super("cn.mazu.base.entity.Supplier",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
   }
	
	@Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args); 
	}
	
	@Override
	public Widget generateRelevantForm(String title,PopBox p) {
		HeadButton hb = getHeadButton();
		Supplier supplier = (Supplier) hb.getDeliveryObj();
		return new SupplierForm(title, supplier, p, SupplierMgr.this).draw(getMyParent(),null);
	}
	
	 @Override
    public boolean removeFromMT() {
		 Supplier selwo = (Supplier)getHeadButton().getDeliveryObj();
    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
    		Util.deleteFiles(tr("base.filepath")+"/supplier/"+selwo.getId()+"/","*");
    		return true;
    	}
    	return false;
    }
	
}
