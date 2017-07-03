package cn.mazu.widget;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;

public class TabContent extends HtmlTemplate {
	private Class[] parameterTypes = new Class[1];
	private List args_;
	private String selid;
	public TabContent(){}
	public TabContent(String selid){
		this.selid = selid;
	}
	@Override
   public Widget draw(Template parent, String... args) {
//		System.out.println("tabcontent:"+this);
		setChildWidget("selid", new Text(selid));
		String pkcls = "cn.mazu.ui."+args[0];
		try {
			Class<?> claz = Class.forName(pkcls);
			parameterTypes = args.length>0? new Class[2]:new Class[1];
			if (args_ == null) {
				args_ = new ArrayList<Object>();
			} else {
				args_.clear();
			}
			args_.add(this);
			parameterTypes[0] = Template.class;
			
			if (args.length > 0) {
				parameterTypes[1] = Array.newInstance(String.class, 0).getClass();
				int i = 0;
				Object ss = Array.newInstance(String.class, args.length);
				while (i < args.length) {
					((String[])ss)[i]=args[i];
					i++;
				}
				args_.add(ss);

			}
			Method method = claz.getMethod("draw", parameterTypes);
			Object clso = claz.newInstance();
			return (Widget) method.invoke(clso, args_.toArray());
		} catch (Exception e) {
			//return new Template("hello,I'm "+args[0]);
			e.printStackTrace();
			return null;
		}
   }
  

}
