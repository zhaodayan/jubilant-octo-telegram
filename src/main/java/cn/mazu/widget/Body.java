package cn.mazu.widget;

import cn.mazu.WApplication;
import cn.mazu.utils.WXmlLocalizedStrings;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.kit.web.interact.Template;

public class Body extends HtmlTemplate{
	private String xml ="/cn/mazu/widget/Body" ;
	private String tpl = "index_page";
//	private String star = "margin:0px; padding:0px;font-size: 12px; font-family: \"微软雅黑\";";
	public Body(){
		super();
		WXmlLocalizedStrings resourceBundle = new WXmlLocalizedStrings();
		resourceBundle.use(xml);
		resourceBundle.use("/cn/mazu/wf");
		WApplication.getInstance().setLocalizedStrings(resourceBundle);
//		this.addCssRule("*", star, "star");
	}
	public Template create() {
		this.setTemplateText(tr(tpl),TextFormat.XHTMLUnsafeText);
		return this;
	}
	
	public void setTpl(String tpl){
		this.tpl = tpl;
	}
}
