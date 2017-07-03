package cn.mazu.widget;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.annotation.Bus;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.utils.WString;
import cn.mazu.webkit.html.WCssStyleSheet;
import cn.mazu.webkit.html.WCssTextRule;
import cn.mazu.webkit.servlet.WebSession;
import cn.mazu.widget.kit.web.interact.Template;

public class HtmlTemplate extends Template {
	static public Logger logger = LoggerFactory.getLogger(HtmlTemplate.class);
	
	public String id_=UUID.randomUUID().toString();
	private Map<String, Widget> modules_=null;
	private List args_;
	private Class[] parameterTypes = new Class[1];
	public HtmlTemplate(){
//		System.out.println("usedcMap.getClass in ht:"+usedcMap.toString());
	}
	
	public String getId(){
		return id_;
	}
	/**
	 * Add the system module
	 * @param clsName
	 * @param template
	 */
	public void addModule(String clsName, Template template){
	}
	public Widget draw(Template parent, String ...args){
		return this;
	}
	@Override
	public void resolveString(String varName, List<WString> args, Writer result)
			throws IOException {
		parameterTypes = args.size()>0 ? new Class[2] : new Class[1];
		if (args_ == null) {
			args_ = new ArrayList<Object>();
		} else {
			args_.clear();
		}
		args_.add(this);
		parameterTypes[0] = Template.class;
		if (args.size() > 0) {
			parameterTypes[1] = Array.newInstance(String.class, 0).getClass();
			int i = 0;
			Object ss = Array.newInstance(String.class, args.size());
			while (i < args.size()) {
				((String[])ss)[i]=args.get(i).getValue();
				i++;
			}
			args_.add(ss);

		}
		if (varName.equals("base.path")) {
			String basePath = tr("base.path").getValue();
			result.append(basePath);
		} else
			super.resolveString(varName, args, result);
	}
	/**
	 * Decomposition of HTML, according to the variable name to generate the corresponding component
	 * @see cn.mazu.widget.kit.web.interact.Template#resolveWidget(java.lang.String)
	 * @param varName 
	 * 		Naming rules: class_methodname
	 */
	@Override
	public Widget resolveWidget(String varName) {
		try {
			String[] arr = varName.split("_");
			if((arr.length>=1)&&(varName.contains("cn.mazu")||varName.contains("com.alipay"))){
				Class<?> cls = Class.forName(arr[0]);//Class.forName("cn.mazu.factory."+arr[0]);
				Method method = cls.getMethod(arr[1],(Class[]) parameterTypes);
				Object clso = cls.newInstance();
				ArrayList<String> params = new ArrayList<String>();
				for(Method m : cls.getMethods()){
					Annotation[][] an = m.getParameterAnnotations();
					if(an.length>0){
						for(Annotation[] a : an){
							for(Annotation b : a){
								params.add(getUrlParam(((Bus)b).value()));
							}
						}
						if(params.size()>0){
							m.invoke(clso, params.toArray());
							params.clear();
						}
					}
				}
				for(Field f : cls.getDeclaredFields()){
					if(f.isAnnotationPresent(Bus.class)){
						Bus b = f.getAnnotation(Bus.class);
						String bus = getUrlParam(((Bus)b).value());
						if(bus!=null){
							f.set(clso, bus);
						}
					}
				}
				for(Object o : args_.toArray()){
					String str = String.valueOf(o);
					if(str.contains("id"))
						id_ = str.replace("id-", "");
				}
				return (Widget) method.invoke(clso,args_.toArray());
			}
		} catch (Exception e) {
		}
		return super.resolveWidget(varName);
	}
	
	public String getUrlParam(String name){
		return WebSession.Handler.getInstance().getRequest().getParameter(name);
	}
	public Widget getChildWidget(String name){
		if(modules_!=null)
			return modules_.get(name);
		return null;
	}
	public void setChildWidget(String name, Widget widget){
		if(modules_==null)
			modules_=new HashMap<String, Widget>();
		modules_.put(name, widget);
	}
	@Override
	protected WCssTextRule addCssRule(String selector, String declarations,
			String ruleName) {
		WCssStyleSheet sheet = WApplication.getInstance().getStyleSheet();
		if(!sheet.isDefined(ruleName))
			return super.addCssRule(selector, declarations, ruleName);
		else 
			return null;
	}
	
	public void updateUI(){
		
	}
}
