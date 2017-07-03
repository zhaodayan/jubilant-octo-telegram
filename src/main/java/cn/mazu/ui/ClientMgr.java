package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Client;
import cn.mazu.base.mvc.ClientForm;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
//客户管理
public class ClientMgr extends MiddleTpl {
	//private MiddleTpl midTpl;
	
	public ClientMgr() {
	   super("cn.mazu.base.entity.Client",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
		//super("cn.mazu.base.entity.Client",new Object[]{"1","1"});
	}
	
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Client el = (Client) hb.getDeliveryObj();
		return new ClientForm(title,el, p,ClientMgr.this).draw(getMyParent(), null);
	}
	
	 @Override
    public boolean removeFromMT() {
		 Client selwo = (Client)getHeadButton().getDeliveryObj();
    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
    		Util.deleteFiles(tr("base.filepath")+"/client/"+selwo.getId()+"/","*");
    		return true;
    	}
    	return false;
    }
	
}
