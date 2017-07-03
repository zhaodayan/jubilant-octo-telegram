package cn.mazu.widget;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.utils.WString;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.event.JSignal;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.SimpleHtmlEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class SimpleEditor extends TextArea {
	private static Logger logger = LoggerFactory.getLogger(SimpleHtmlEdit.class);
	private JSignal onChange_;
	
	
	public SimpleEditor(){
		this(null);
	}
	public SimpleEditor(ContainerWidget parent){
		this("", parent);
	}
	public SimpleEditor(String text, ContainerWidget parent){
		super(text,parent);
		this.setAttributeValue("name", this.getId());
		this.onChange_ = new JSignal(this, "change");
		this.init();
		
	}

	private void init() {
		WApplication app = WApplication.getInstance();
		this.setInline(false);
		initXHtml();
		this.setJavaScriptMember(" XHtmlEditL", " new Wt3_3_1.XHtmlEditL("+app.getJavaScriptClass() +","
				+this.getJsRef()+");");
		this.onChange_.addListener(this, new Signal.Listener() {
			public void trigger() {
				SimpleEditor.this.propagateOnChange();
			}
		});
	}
	private void propagateOnChange() {
		this.changed().trigger();
	}
	private static void initXHtml(){
		String THIS_JS = "js/XHtmlEditL.js";
		WApplication app = WApplication.getInstance();
		if(!app.isJavaScriptLoaded(THIS_JS)){
			if(app.getEnvironment().hasAjax()){
				app.doJavaScript("window.XHtmlEditL_GZ = {loaded: true};", true);
			}
			String kindEditorURL = WApplication.getRelativeResourcesUrl()+"kindeditor/kindeditor.js";
			app.require(kindEditorURL,"window['XHtmlEditL']");
			//app.useStyleSheet(new Link("xhtmledit/themes/default/default.css"));
			app.loadJavaScript(THIS_JS, wtjs());
		}
	}
	
	static WJavaScriptPreamble wtjs(){
		String base = WString.tr("base.path").getValue();
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope, 
				JavaScriptObjectType.JavaScriptConstructor, 
				"XHtmlEditL", 
				" function(p, d) {" +
				"jQuery.data(d, 'objx', this);"+
						"var n, o, q = this, b = p.WT;" +
						"this.render = function(name) {" +
						"d.ed = KindEditor.create('textarea[name='+name+']', {resizeType:0,themeType:'simple',"+
						"items : [" +
								"'emoticons','about']});" +
						"};}");
		
	}
	@Override
	public void updateDom(DomElement element, boolean all) {
		super.updateDom(element, all);
		String name = element.getAttribute("name");
		
		String str = "(function(){" +
				"var obj  = $('#"+ this.getId() + "').data('objx');" +
				"obj.render('"+name+"');" +
		"})();";
		//System.out.println(str);
		element.callJavaScript(str);
	}
	@Override
	public void getDomChanges(List<DomElement> result, WApplication app) {
		// TODO Auto-generated method stub
		super.getDomChanges(result, app);
	}
	protected String renderRemoveJs() {
		if (this.isRendered()) {
			return this.getJsRef() + ".ed.remove();Wt3_3_1.remove('"
					+ this.getId() + "');";
		} else {
			return super.renderRemoveJs();
		}
	}

}
