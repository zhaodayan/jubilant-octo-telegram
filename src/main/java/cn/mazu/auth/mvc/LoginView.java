package cn.mazu.auth.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.auth.Session;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.util.HibernateUtil;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.event.Signal1;
import cn.mazu.widget.event.WMouseEvent;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.InteractWidget;
import cn.mazu.widget.kit.web.interact.Template;

public class LoginView extends FormView {
	private static Logger logger = LoggerFactory.getLogger(LoginView.class);
	private Session session_;
	//private AuthModel model_;
	private LoginModel model_;
	//private boolean registrationEnabled_;
	//private String form = "";
	private InteractWidget login;
	private Signal changed_;
	private AccountPermission ap ;
	
	
	public LoginView(){
		Session.configureAuth();
		session_ = new Session(HibernateUtil.getEntityManager());
		/*session_.getLogin().changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				LoginView.this.onLoginChange();
			}
		});*/
		model_ = new LoginModel();
		changed_ = new Signal(this);
	}
	/*protected void onLoginChange(){
		if(session_.getLogin().isLoggedIn()){
			this.createLoggedInView();
		}
	}*/
	private void createLoggedInView(){
		WApplication.getInstance().changedInternalPath("");
		WApplication.getInstance().setInternalPath("");
	}
	
	@Override
	public Widget draw(Template parent, String... args) {
		try {
			this.setTemplateText(tr("login_form"),TextFormat.XHTMLUnsafeText);

			this.update();
			//this.processEnvironment();	
			this.setStyleClass("wrapper");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	/*public void repaintAnchor(Anchor anchor){
		anchor.removeChild(anchor.getImage());
		anchor.setImage(new Image(new Link(new AuthCode())));
		this.bindWidget("img", anchor);
	}*/
	private void update(){
		//if(this.model_.getPasswordAuth()!=null){
			this.setCondition("if:user-password", true);
			this.updateView(this.model_);
			login = (InteractWidget) this.resolveWidget("auth-login");
			if(!(login!=null)){
				login = new PushButton("进入系统");
				login.setStyleClass("editcls btn-warning logbtn");
				login.clicked().addListener(login, new Signal1.Listener<WMouseEvent>() {
					@Override
					public void trigger(WMouseEvent arg) {
						LoginView.this.attemptPasswordLogin();
					}
				});
				this.bindWidget("auth-login", login);
				//this.model_.configureThrottling(login);
			}
			//this.model_.updateThrottling(login);
		//}/*else{
			//this.bindEmpty("login");
		//}
	}
	protected void attemptPasswordLogin(){
			this.updateModel(model_);
			this.bindString("user-name-info", "");
			this.bindString("user-password-info", "");
			
			Object validateRet = this.model_.loginValidate(session_);
			if(validateRet!=null&&validateRet.getClass().getSimpleName().equals("AccountPermission")){
				ap = (AccountPermission)validateRet;
				redirectLogin();
			}else if(validateRet!=null&&validateRet.getClass().getName().equals("java.lang.Integer")){
				int msgIndex = (Integer)validateRet;
				if(msgIndex==1){
					this.bindString("user-name-info", "<span class='span'>"+tr("wrong username")+"</span>");
				}else if(msgIndex==2){
					this.bindString("user-password-info","<span class='span'>"+ tr("wrong password")+"</span>");
				}
			}
	}
	//登陆后跳转
	private void redirectLogin(){
		WApplication.getInstance().setCookie("username", Util.URLEString(model_.getValue(model_.username).toString()),60*60);
		//WApplication.getInstance().setCookie("password",Util.URLEString(model_.getValue(model_.passwd).toString()),60*60);
		WApplication.getInstance().setCookie("loginid",Util.URLEString(ap.getId()),60*60);
		WApplication.getInstance().setCookie("menustr",Util.URLEString(ap.getMenuStr()),60*60);
	//	WApplication.getInstance().setCookie("menuper",Util.URLEString(ap.getMenuPermission()),60*60);
		WApplication.getInstance().setCookie("dataCode",Util.URLEString(ap.getDataCode()),60*60);
		WApplication.getInstance().setCookie("pDataCode",Util.URLEString(ap.getDataCode()),60*60);
		WApplication.getInstance().getBus().put("ap", ap);
		this.changed_.trigger();
		//need?need!
		//WApplication.getInstance().changedInternalPath("/index_page");
		Util.skipPage("/index_page");
	}
	/*public void processEnvironment(){
		WEnvironment env = WApplication.getInstance().getEnvironment();
		if(this.registrationEnabled_){
			
		}
		User user = this.model_.processAuthToken();
		if(user.isValid()){
			session_.getLogin().login(user, LoginState.StrongLogin);
		}
	}*/
	@Override
	protected Widget createFormWidget(String field) {
		FormWidget result = null;
		if(field.equals(model_.username)){
			result = new LineEdit();
			result.setFocus();
		}else if(field == model_.passwd){
			{
				LineEdit p = new LineEdit();
				p.enterPressed().addListener(p, new Signal.Listener() {
					@Override
					public void trigger() {
						LoginView.this.attemptPasswordLogin();
					}
				});
				p.setEchoMode(LineEdit.EchoMode.Password);
				result = p;
			}/*else{
				if(field == AuthModel.RememberMeField){
					result = new CheckBox();
					result.setAttributeValue("style", "margin-right:5px;");
					model_.setValue(AuthModel.RememberMeField, true);
				}
			}*/
		}else{
			result = new LineEdit();
			result.hide();
		}
		return result;
	}
    /*@Override
    public void updateModelField(FormModel model, String field) {
        
    	super.updateModelField(model, field);
    }*/
	
}
