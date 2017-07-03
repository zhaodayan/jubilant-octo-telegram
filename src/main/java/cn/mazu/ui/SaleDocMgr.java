package cn.mazu.ui;

import java.lang.reflect.Field;

import cn.mazu.WApplication;
import cn.mazu.doc.mvc.SaleDocForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.workorder.entity.WorkOrder;
//销售单（即工单）
public class SaleDocMgr extends MiddleTpl {
    public SaleDocMgr() {
    	super("cn.mazu.workorder.entity.WorkOrder",new Object[]{"like","dataCode",
    			 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
    	try {
			getHiddenList().add(getCurCls().getField("oper"));//销售合同处，不显示【编辑明细】、【查看明细】
			getHiddenList().add(getCurCls().getField("viewDetail"));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

    @Override
    public Widget generateRelevantForm(String title,PopBox p) {
    	HeadButton hb = getHeadButton();
    	WorkOrder wo = (WorkOrder) hb.getDeliveryObj();
    	return new SaleDocForm(title, wo, p, SaleDocMgr.this).draw(getMyParent(),null);
    }
    @Override
    public boolean removeFromMT() {
    	WorkOrder selwo = (WorkOrder)getHeadButton().getDeliveryObj();
    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
    		Util.deleteFiles(tr("base.filepath")+"/workOrder/design file/"+selwo.getId()+"/","*");
    		Util.deleteFiles(tr("base.filepath")+"/workOrder/contract file/"+selwo.getId()+"/","*");
    		return true;
    	}
    	return false;
    }

	@Override
    public Widget getSpecialTextWidget(EntityObject val, Field field) {
    	Text retText = new Text();
	    if(field.getType().getName().equals("cn.mazu.surface.entity.Surface")||
	    		field.getType().getName().equals("cn.mazu.base.entity.Client")){//显示图面名称,客户名称
				if(val!=null){
					retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
				}
	    }
	    return retText;
    }   
}
