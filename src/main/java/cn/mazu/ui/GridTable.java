package cn.mazu.ui;

import cn.mazu.sitemg.mvc.QuolibView;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.interact.Template;



public class GridTable extends HtmlTemplate{
	public GridTable(){
	}

	@Override
	public Widget draw(Template parent, String... args) {
		QuolibView qv = new QuolibView();
		return qv.draw(null, null);
	}
	
	

}
