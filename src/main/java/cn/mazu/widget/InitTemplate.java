package cn.mazu.widget;

import cn.mazu.WApplication;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.kit.web.interact.Template;

public class InitTemplate extends HtmlTemplate {
	//pay attention to the order!
    private String[] uiArr = new String[]{"tooltip","panel","resizable","linkbutton","pagination",
    		"tabs","validatebox","textbox", "spinner","numberbox","window",
    		"draggable","datagrid","parser"};
    
    public InitTemplate() {
    	initJs();
    }
    
    @Override
    public Widget draw(Template parent, String... args) {
    	this.setTemplateText("<div></div>",TextFormat.XHTMLUnsafeText);
    	this.hide();
    	initJs();
    	//return super.draw(parent, args);
    	return this;
    }
	
	private void initJs(){
		WApplication app = WApplication.getInstance();
		loadJs();
		for(String uiname:uiArr){
			this.setJavaScriptMember(uiname.toUpperCase(), "new Wt3_3_1."+uiname.toUpperCase()+"("+app.getJavaScriptClass()+","
					+this.getJsRef()+");");
		}
	}
	private void loadJs(){
		WApplication app = WApplication.getInstance();
		for(String uiname:uiArr){
			String logical_url = "js/"+uiname+".js";
			String physical_url = WApplication.getRelativeResourcesUrl()+"jquery."+uiname+".js";
			
			if (!app.isJavaScriptLoaded(logical_url)) {
				if (app.getEnvironment().hasAjax()) {
					app.doJavaScript("window."+uiname.toUpperCase()+"_GZ = { loaded: true };",
							false);
				}
				
				app.require(physical_url, "window['"+uiname.toUpperCase()+"']");
				app.loadJavaScript(logical_url, wtjs(uiname));
			}
			
		}
	}
	static WJavaScriptPreamble wtjs(String uiname){
		return new WJavaScriptPreamble(JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor,uiname.toUpperCase(),
				"function(p,d){jQuery.data(d,\"obj\",this);}");
	}
}
