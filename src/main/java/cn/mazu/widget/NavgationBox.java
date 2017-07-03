package cn.mazu.widget;

import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.kit.web.WebWidget;
import cn.mazu.widget.kit.web.interact.Template;

/*页脚*/
public class NavgationBox extends HtmlTemplate {
	private WebWidget container;
	public NavgationBox(){
//		initJs();
	}
	
	public Widget draw(Template parent, String... args) {
		this.container = parent;
		String text =
 "" +
"<div class=\"copyright\">益耐特生产管理系统 V1.0  © 2014/8/1  服务热线：0512-57868316</div>"/*+
"<div  id=\"p-tools\" class=\"search_all\">"+
 " <div class=\"search_textbox\"><input type=\"text\" class=\"textbox_style\" value=\"请输入关键字\"/></div>"+
"  <div class=\"search_button\"><input name=\"\" type=\"button\" class=\"button_style\"/></div>"+
"</div>"*/;
		
        this.setTemplateText(text,TextFormat.XHTMLUnsafeText);
     
		return this;
	}
	/*@Override
	public void updateDom(DomElement element, boolean all) {
//		System.out.println("updatedom oooo:"+all);
		super.updateDom(element, all);
		//项目搜索条目的显示/隐藏
		element.callJavaScript("$(document).ready(function() {" +
				"if(!window.easyloader&&$.parser.auto){$.parser.parse();}" +
//				"$('#tabs').tabs({"+
//				"		onClose:function() {"+
//				"			index --;"+
//				"		}"+
//				"	});" +
	            "});");
	}*/

}
