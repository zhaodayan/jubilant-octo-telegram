package cn.mazu.ui;

import java.lang.reflect.Field;
//import java.lang.reflect.Method;
import java.util.List;

import cn.mazu.WApplication;
import cn.mazu.storage.entity.InventoryBook;
import cn.mazu.storage.mvc.InventoryBookForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Text;

public class InventoryBookMgr extends MiddleTpl {
	private InventoryInitialMgr iiM;
    public InventoryBookMgr() {
	   super("cn.mazu.storage.entity.InventoryBook",new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}
   
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		iiM = new InventoryInitialMgr();
		HeadButton hb = getHeadButton();
    	InventoryBook ib = (InventoryBook) hb.getDeliveryObj();
    	if(title.equals("编辑")&&ib!=null&&(ib.getSc()!=null&&!ib.getSc().getName().equals(StoreCategory.WORKORDER.name)||ib.getSc()==null)){//非工单仓位不能编辑
    		Util.infoMessage(tr("Information Please").toString(), "非工单仓位不能编辑！");
    		p.setAttributeValue("style", "display:none;");//编辑框弹出调用在前，所以这里要手动隐藏下
    		return null;
    	}else{
    		return new InventoryBookForm(title, ib, p, this,iiM).draw(getMyParent(), null);
    	}
	}
    @Override
    public Widget getSpecialTextWidget(EntityObject val, Field field) {
    	Text retText = new Text();
	    if(field.getType().getName().equals("cn.mazu.workorder.entity.WorkOrder")){//工单，显示工单号
				if(val!=null){
					retText.setText(WidgetUtil.getValFromProperty(val, "orderno").toString());
				}
	    }
	    return retText;
    } 
    @Override
    public boolean removeFromMT() {
    	InventoryBook ib = (InventoryBook)getHeadButton().getDeliveryObj();
    	List<EntityObject> booklist = getDBEntity().getSubListQBC(InventoryBook.class, new Object[]{"like","dataCode","___","and"}, 0, 1);
    	if(booklist!=null&&booklist.size()>0
    			&&booklist.get(0).getId().equals(ib.getId())){
    		return getDBEntity().removeEO(ib);
    	}else
    		Util.infoMessage(tr("Information Please").toString(), "只能删除最新的一条记录!");
    		return false;
    }
}
