/*package cn.mazu.ui;

import java.lang.reflect.Field;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.doc.mvc.SignDocForm;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.DOCType;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.workorder.entity.WorkOrder;
//订购单
public class ReserveDocMgr extends MiddleTpl{
	private BuyDocMgr bdm;
	public ReserveDocMgr() {
		   super("cn.mazu.doc.entity.SignDoc",new Object[]{"like","dataCode",
				   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and",
					                                       "equal","docType",DOCType.BOOKING,"and"});
		   bdm = new BuyDocMgr();
		}
	   @Override
	    public Widget draw(Template parent, String... args) {
	        return super.draw(parent, args);
	    }
	   
	   @Override
	    public boolean removeFromMT() {
	    	SignDoc selwo = (SignDoc)getHeadButton().getDeliveryObj();
	    	if(getDBEntity().removeEO(selwo)){//删除记录后删除附件
	    		Util.deleteFiles(tr("base.filepath")+"/contract/"+selwo.getId()+"/","*");
	    		return true;
	    	}
	    	return false;
	    }
	   
	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		SignDoc sd = (SignDoc) hb.getDeliveryObj();
		return new SignDocForm(DOCType.BOOKING, title, sd, p, ReserveDocMgr.this).draw(getMyParent(), null);
	}
	@Override
	public Widget getSpecialTextWidget(EntityObject eo, Field field) {
		return bdm.getSpecialTextWidget(eo, field);
	}
	@Override
	public Widget generateDetailWidget(Field field,Map fMap,EntityObject deo,EntityObject eo){
		return bdm.generateDetailWidget(field,fMap,deo,eo);
	}
	
	@Override
	public boolean setPropertyFromWidget(EntityObject deo,Map fieldWMap,EntityObject eo) {
		return bdm.setPropertyFromWidget(deo,fieldWMap,eo);
	}
}
*/