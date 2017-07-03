package cn.mazu.ui;

import cn.mazu.WApplication;
import cn.mazu.sitemg.entity.Notice;
import cn.mazu.sitemg.mvc.NoticeView;
import cn.mazu.util.Util;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;

public class CommentInfo extends MiddleTpl{
	
	public CommentInfo(){
		super("cn.mazu.sitemg.entity.Notice",new Object[]{"like","dataCode",
				 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
	}

	
	/*@Override
	public Widget draw(Template parent, String... args) {
		return super.draw(parent, args);
	}*/


	@Override
	public Widget generateRelevantForm(String title, PopBox p) {
		HeadButton hb = getHeadButton();
		Notice nt = (Notice) hb.getDeliveryObj();
		return new NoticeView(nt, p, this, title).draw(getMyParent(), null);
	}

	@Override
	public boolean removeFromMT() {
		Notice nt = (Notice) getHeadButton().getDeliveryObj();
		if(getDBEntity().removeEO(nt)){
			return true;
		}
		return false;
	}
	
	

}
