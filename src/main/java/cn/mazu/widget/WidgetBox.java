package cn.mazu.widget;

import cn.mazu.WApplication;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Image;

public class WidgetBox extends HtmlTemplate {
	private Widget wbcontent; 
	public WidgetBox(Widget wcontent){
		String text=
//				"<div class='widgetbox'>" +
				"<div class='widbox_title'>" +
				"${wicon}" +
//				"<img class='widbox_title_img' src='../images/w_icon1.png'></img>" +
						"<span>" +
						"${wtitle}" +
						"</span>" +
						"<div class='refresh'>" +
						"${refreshbtn}" +
//						"<a href='#'><img src='images/refresh_m.png'></img></a>" +
						"</div></div>" +
						"" +
						"${wcontent}" +
						"";
//						"</div>";
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
		this.wbcontent = wcontent;
		//initJS();
	}

	public Widget draw(Widget wicon,String title) {
		wicon.setStyleClass("widbox_title_img");
		this.bindWidget("wicon", wicon);
		
		PushButton refreshbtn = new PushButton();
		refreshbtn.setStyleClass("refreshbtn");
		refreshbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				
			}
		});
		this.bindWidget("refreshbtn", refreshbtn);

		this.bindString("wtitle", title);
		this.bindWidget("wcontent", wbcontent);
		this.setStyleClass("widgetbox");
		return this;
	}
	public void initJS(){
		WApplication app = WApplication.getInstance();
		initSlide();
		this.setJavaScriptMember(" WidgetBox", "new Wt3_3_1.WidgetBox("+app.getJavaScriptClass() +","
				+this.getJsRef()+");");
	}
	public void initSlide(){
		String THIS_JS = "js/WidgetBox.js";
		WApplication app = WApplication.getInstance();
		
		if(!app.isJavaScriptLoaded(THIS_JS)){
			if (app.getEnvironment().hasAjax()) {
				app.doJavaScript("window.WidgetBox_GZ = { loaded: true };", false);
			}
			String slideURL = WApplication.getRelativeResourcesUrl()+"jquery.rollbar.js";
			app.require(slideURL,"window['WidgetBox']");
			app.loadJavaScript(THIS_JS, wtjs());
		}
	}
	
	static WJavaScriptPreamble wtjs(){
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor, 
				"WidgetBox",
				"function(p,d){jQuery.data(d,\"obj\",this);}");
		
	}
	/*@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		element.callJavaScript("$(document).ready(function(){$('#"+wbcontent.getId()+"').rollbar({zIndex:80,pathPadding:'5px'}); });");
	}*/
	
}
