package cn.mazu.widget;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class AddressSelect extends HtmlTemplate {
	private static Logger logger = LoggerFactory.getLogger(AddressSelect.class);
	private String prov="北京",city="东城区",dist="";
	private LineEdit linea,lineb,linec;

	public AddressSelect(){
		this("北京","东城区","");
	}
	
	public AddressSelect(String prov,String city,String dist){
		this.prov = prov;
		this.city = city;
		this.dist = dist;
		linea = generateLine("linea_hidden",prov);
		lineb = generateLine("lineb_hidden",city);
		linec = generateLine("linec_hidden",dist);
		initJS();
		draw();
	}
	
	public void draw(){
		String text ="${div}" ;
		ContainerWidget div_hidden = new ContainerWidget();
		ContainerWidget div = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_DIV;
			}
		};
		div.setId("city_select");
		
		ContainerWidget selecta = generateSelect("selecta");
		
		ContainerWidget selectb = generateSelect("selectb");

		ContainerWidget selectc = generateSelect("selectc");
		
		selecta.addStyleClass("prov");
		selecta.setAttributeValue("style","display:inline-block;margin-right:2px;");
		div.addChild(selecta);
		
		selectb.addStyleClass("city");
		selectb.setAttributeValue("style","margin-right:2px;");
		div.addChild(selectb);
		
		selectc.addStyleClass("dist");
		div.addChild(selectc);
		
		
		
		div_hidden.addChild(linea);	
		div_hidden.addChild(lineb);
		div_hidden.addChild(linec);
		div_hidden.setStyleClass("hiddendiv");
		div.addChild(div_hidden);
		
		this.bindWidget("div", div);
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);	
	}
	
	private ContainerWidget generateSelect(String id){
		ContainerWidget select = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_SELECT;
			}
		};
		select.setId(id);
		return select;
	}
	
	private LineEdit generateLine(String id,String text){
		final LineEdit line = new LineEdit(text);
		line.setId(id);
		line.setAttributeValue("style", "width:0px;height:0px;border:0;");
		line.focussed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
			}
		});
		return line;
	}
	public String[] getAddr(){
		return new String[]{linea.getValueText(),lineb.getValueText(),linec.getValueText()};
	}
	public String getAddrStr(){
		return linea.getValueText().concat(",").concat(lineb.getValueText()).concat(",").concat(linec.getValueText());
	}
	
	public void initJS(){
		WApplication app = WApplication.getInstance();
		initSlide();
		this.setJavaScriptMember(" AddressSelect", "new Wt3_3_1.AddressSelect("+app.getJavaScriptClass() +","
				+this.getJsRef()+");");
	}
	public void initSlide(){
		String THIS_JS = "js/AddressSelect.js";
		WApplication app = WApplication.getInstance();
		
		if(!app.isJavaScriptLoaded(THIS_JS)){
			if (app.getEnvironment().hasAjax()) {
				app.doJavaScript("window.AddressSelect_GZ = { loaded: true };", false);
			}
			String slideURL = WApplication.getRelativeResourcesUrl()+"jquery.cityselect.js";
			app.require(slideURL,"window['AddressSelect']");
			app.loadJavaScript(THIS_JS, wtjs());
		}
	}
	
	static WJavaScriptPreamble wtjs(){
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor, 
				"AddressSelect",
				"function(p,d){jQuery.data(d,\"obj\",this);}");
		
	}
	@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		element.callJavaScript("$(function(){" +
				"$('#city_select').citySelect({prov:'"+prov+"',city:'"+city+"',dist:'"+dist+"',nodata:'none'});" +
				"$('#selecta').change(function(){" +
				"var checkText=$('#selecta').find('option:selected').text();" +
					"$('#linea_hidden').val(checkText);" +
					"$('#linea_hidden').focus().select();" +
					"var checkText=$('#selectb').find('option:selected').text();" +
					"$('#lineb_hidden').val(checkText);" +
					"$('#lineb_hidden').focus().select();" +
					"var checkText=$('#selectc').find('option:selected').text();" +
					"$('#linec_hidden').val(checkText);" +
					"$('#linec_hidden').focus().select();" +
				"}); " +
				"$('#selectb').change(function(){" +
					"var checkText=$('#selectb').find('option:selected').text();" +
					"$('#lineb_hidden').val(checkText);" +
					"$('#lineb_hidden').focus().select();" +
					"var checkText=$('#selectc').find('option:selected').text();" +
					"$('#linec_hidden').val(checkText);" +
					"$('#linec_hidden').focus().select();" +
				"}); " +
				"$('#selectc').change(function(){" +
					"var checkText=$('#selectc').find('option:selected').text();" +
					"$('#linec_hidden').val(checkText);" +
					"$('#linec_hidden').focus().select();" +
				"}); " +
				"});");
	}
	
	
}
