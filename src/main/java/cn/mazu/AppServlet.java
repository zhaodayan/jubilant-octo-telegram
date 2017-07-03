package cn.mazu;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import cn.mazu.util.Util;
import cn.mazu.utils.WString;
import cn.mazu.utils.WXmlLocalizedStrings;
import cn.mazu.webkit.html.MetaHeaderType;
import cn.mazu.webkit.servlet.WEnvironment;
import cn.mazu.webkit.servlet.WebSession;
import cn.mazu.webkit.servlet.WtServlet;
import cn.mazu.widget.Body;
import cn.mazu.widget.InitTemplate;
import cn.mazu.widget.event.Signal1;
import cn.mazu.widget.kit.Link;

public class AppServlet extends WtServlet {
	private static final long serialVersionUID = -4795892036751538746L;
	private Map<String,Boolean> usedcMap = new HashMap<String,Boolean>();
	public AppServlet() {
	}
	
	@Override//ҳ��ˢ��
	public WApplication createApplication(WEnvironment env) {
		WApplication app = new WApplication(env);
		InitTemplate it = new InitTemplate();
//		��ʼ��session
		//Session.configureAuth();
		//Session session_ = new Session(HibernateUtil.getEntityManager());
		//session_.getAuth().setEmailVerificationEnabled(true);
		//app.getBus().put("session", session_);
		app.getBus().put("usedcMap", usedcMap);
//		��ʼ��AuthModel
	//	AuthModel authModel = new AuthModel(session_.getAuth(),  (UserDatabase) session_.getUserDatabase());
		//app.getBus().put("auth", authModel);

//		processEnvironment();
		app.addMetaHeader(MetaHeaderType.MetaHttpHeader,"content", "text/html;charset=utf-8");
//		���body
		WXmlLocalizedStrings resourceBundle = new WXmlLocalizedStrings();
		app.setLocalizedStrings(resourceBundle);
	
		app.useStyleSheet(new Link("style/ImageMgrList.css"));
		app.useStyleSheet(new Link("style/demo.css"));
		app.useStyleSheet(new Link("style/common.css"));
		app.useStyleSheet(new Link("style/easyui.css"));
		app.useStyleSheet(new Link("style/icon.css"));
		app.useStyleSheet(new Link("style/pagi.css"));
		app.useStyleSheet(new Link("style/tagview.css"));
		app.useStyleSheet(new Link("style/ImageMgrList.css"));
		app.useStyleSheet(new Link("style/rollbar.css"));
		Body body = new Body();
		try {
			filterServlet(app.getInternalPath());
			String[] arr = URLDecoder.decode(app.getInternalPath(),"UTF-8").split("/");
			if(arr.length>0){
				String substr = arr[arr.length-1];
				if(substr.indexOf("?")>0){
					searchPath(substr);
					substr = substr.substring(0, substr.indexOf("?"));
				}
				body.setTpl(substr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		app.getRoot().addWidget(body.create());
		body.setParent(app.getRoot());
		//ҳ����ת
		app.internalPathChanged().addListener(null, new Signal1.Listener<String>() {
			@Override
			public void trigger(String path) {
				try {
					if(!filter(path))return;
					path = URLDecoder.decode(path,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				WApplication app = WApplication.getInstance();
				app.getRoot().removeWidget(app.getRoot().getWidget(0));
				Body body = new Body();
				path = path.substring(1);
				if(path.indexOf("?")>0){
					searchPath(path);
					path = path.substring(0, path.indexOf("?"));
				}
				if(!path.trim().equals(""))
					body.setTpl(path);
				body.create();
				app.getRoot().addWidget(body);
			}

			
		});
		if(app.getEnvironment().getAgent().name().equals("IE7")||(app.getEnvironment().getAgent().name().contains("Chrome")))
			app.getRoot().setAttributeValue("style", "overflow-y:hidden;");
		else
			app.getRoot().setAttributeValue("style", "overflow-y:auto;");
		return app;
	}
	
	private void searchPath(String path) {
			if(!path.contains("?"))
				return ;
			String[] search = path.split("\\?")[1].split("\\&");
			for(int i =0; i<search.length; i++){
				String[] a = new String[1];
				a[0] = search[i].split("=")[1];						
				WebSession.Handler.getInstance().getRequest().getParameterMap().put(search[i].split("\\=")[0], a);
				WebSession.Handler.getInstance().getRequest().getQueryString();
			}
	}

	private boolean filter(String path){//
		String loginid =  Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("loginid"));
		if(path.startsWith("/"))path = path.substring(1);
		if(WString.tr("filter").toString().contains(","+path+",")&&!path.equals("")){
				if(loginid.equals("")){
					if(WApplication.getInstance().changedInternalPath("/login_page")){
						WApplication.getInstance().setInternalPath("/login_page");
						return false;
					}
				}
		}else{
			
		}
		return true;
	}
	
	private void filterServlet(String path){
		String loginid =  Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("loginid"));
		if(path.startsWith("/"))path = path.substring(1);
			if(loginid.equals("")){
					WApplication.getInstance().setInternalPath("/login_page");
			}
	}
	
	
	/*public void processEnvironment(){
		try {
			WEnvironment env = WApplication.getInstance().getEnvironment();
			User user = ((AuthModel)WApplication.getInstance().getBus().get("auth")).processAuthToken();
			if(user.isValid()){
				AuthInfo authInfo =  user.getDatabase().findAuthInfo(user.getId());
				((Session)WApplication.getInstance().getBus().get("session")).getLogin().login(user,LoginState.StrongLogin);
				WApplication.getInstance().setCookie("loginid", Util.URLEString(authInfo.getUser().getId()),60*60);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
