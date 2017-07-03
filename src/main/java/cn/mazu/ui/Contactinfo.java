package cn.mazu.ui;

import cn.mazu.sitemg.mvc.ContactView;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.WidgetBox;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;

public class Contactinfo extends HtmlTemplate{
	public Contactinfo(){
		
	}

	@Override
	public Widget draw(Template parent, String... args) {
		ContactView cv = new ContactView();
		
		String text = "${contact}";
		this.bindWidget("contact", cv.draw(parent, args));
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
		
		return this;
	}
	
	

}
