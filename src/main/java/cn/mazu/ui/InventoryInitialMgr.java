package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.storage.mvc.InventoryInitialForm;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

public class InventoryInitialMgr extends MiddleTpl {
    public InventoryInitialMgr() {
    	super("cn.mazu.storage.entity.Inventory",new Object[]{"like","dataCode",
    			 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}
  
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
     	Inventory inventory = (Inventory) hb.getDeliveryObj();
    	return new InventoryInitialForm(title, inventory, p, InventoryInitialMgr.this).draw(getMyParent(), null);
	}
    
    
}
