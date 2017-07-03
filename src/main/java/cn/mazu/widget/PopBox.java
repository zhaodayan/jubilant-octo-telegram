package cn.mazu.widget;

import java.util.Date;

import cn.mazu.WApplication;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class PopBox extends HtmlTemplate {
	
	public PopBox() {
		initJS();
	}

	public PopBox(String title) {
		this(title,"950");
	}

	public PopBox(String title,String width){
		initJS();
		draw(title,width);
	}
	public void draw(String title){
		draw(title,"950");
	}
	public void draw(String title,String width) {
		String text = "<div class=\"background\" style=\"display:none;\"></div>"
				+ "<div class=\"webox\" style=\"width:"+width+"px;height:auto;display:none;\">"//display:none;
				+ "<div id=\"inside\" style=\"height:auto;\">"
				+ "<h1 id=\"locked\" >${title}"+
				"<a class='enlarge ad' title='最大化' href='javascript:void(0);'></a>" +
				"<a class='collapse ad' _num='1' href='javascript:void(0);'></a>" +
				"${closeX}" +
//				"<a class=\"span\" href=\"javascript:void(0);\"></a>" +
				"</h1>"+
				"${form_tbl}" +
				"</div></div>";
		Anchor closeX = new Anchor();
		closeX.setStyleClass("span");
		closeX.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				PopBox.this.setAttributeValue("style", "display:none");
			}
		});
		this.bindWidget("closeX", closeX);
		this.bindString("title",title);
		this.bindWidget("form_tbl", createFormTbl(title,PopBox.this));
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
	}
	public Widget createFormTbl(String title,PopBox wbx){
		return null;
	}
	
	public void initJS() {
		WApplication app = WApplication.getInstance();
		initSlide();
		this.setJavaScriptMember(
				" WeBox",
				"new Wt3_3_1.WeBox(" + app.getJavaScriptClass() + ","
						+ this.getJsRef() + ");");
	}

	public void initSlide() {
		String THIS_JS = "js/webox.js";
		WApplication app = WApplication.getInstance();

		if (!app.isJavaScriptLoaded(THIS_JS)) {
			if (app.getEnvironment().hasAjax()) {
				app.doJavaScript("window.WeBox_GZ = { loaded: true };", true);
			}
			String slideURL = WApplication.getRelativeResourcesUrl()
					+ "jquery.webox.js";
			app.require(slideURL, "window['WeBox']");
			app.loadJavaScript(THIS_JS, wtjs());
		}
	}

	static WJavaScriptPreamble wtjs() {
		return new WJavaScriptPreamble(JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor, "WeBox",
				"function(p,d){jQuery.data(d,\"obj\",this);}");

	}

	public void drawParent(){
		
	}
	
	@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		element.callJavaScript("$.webox({height:218,width:950,bgvisibel:true,title:'',html:''});");
	}
}
