/*package cn.mazu.auth.mvc;

import cn.mazu.WApplication;
import cn.mazu.auth.Session;
import cn.mazu.auth.User;
import cn.mazu.auth.db.AbstractUserDatabase;
import cn.mazu.auth.db.UserDatabase;
import cn.mazu.util.HibernateUtil;
import cn.mazu.util.Util;
import cn.mazu.widget.FormView;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.event.Signal1;
import cn.mazu.widget.event.WMouseEvent;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;

public class RegisterView extends FormView {
	private Session session_;
    private RegistrationModel regModel;
    public boolean created_ = false;
    PushButton regButton = new PushButton("ע ��"tr("ok-button"));
    PushButton logButton = new PushButton("�� ¼");
    PushButton cancelButton = new PushButton("ȡ��");
    public RegisterView() {
    	Session.configureAuth();
		session_ = new Session(HibernateUtil.getEntityManager());
		session_.getLogin().changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				RegisterView.this.onLoginChange();
			}
		});
		this.regModel = new RegistrationModel(session_.getAuth(),  (UserDatabase) session_.getUserDatabase(), session_.getLogin(), this);
		this.regModel.addPasswordAuth(Session.getPasswordAuth());
	}
	@Override
	public Widget draw(Template parent, String... args) {
		for(String str:args){
			if(str.contains("form"))
				this.setTemplateText(tr(str));
		}
		this.update();
		this.setStyleClass("wrapper");
		return this;
	}
	FormWidget pass;
	@Override
	protected Widget createFormWidget(String field) {
		Widget result = null;
	    if(field.equals(regModel.LoginNameField)){
	    	result = new LineEdit();
	    	((FormWidget)result).changed().addListener(result,new Signal.Listener() {
				@Override
				public void trigger() {
				   RegisterView.this.checkLoginName();
				   ((LineEdit)pass).setFocus();
				}
			});
	    }else if(field.equals(regModel.UserPasswordField)||field.equals(regModel.RepeatPasswordField)){
	    	pass = new LineEdit();
	    	((LineEdit) pass).setEchoMode(LineEdit.EchoMode.Password);
	    	result = pass;
	    }else if(field.equals(regModel.EmailField)){
	    	result = new LineEdit();
	    }
		return result;
	}
	
	protected void onLoginChange() {
		if(session_.getLogin().isLoggedIn()){
			this.createLoggedInView();
		}
	}
	//��¼�ɹ�����ʲô�ط�?
	private void createLoggedInView() {
		if(WApplication.getInstance().changedInternalPath("/index_page")){		
			WApplication.getInstance().setInternalPath("/index_page");
		}
		
	}
	private void update() {
		if(this.regModel.getPasswordAuth() != null)
			this.bindString("password-description", tr("password-description"));
		else
			this.bindEmpty("password-description");
		this.updateView(this.regModel);
		
		if(!this.created_){
			LineEdit password = (LineEdit) this.resolveWidget(regModel.UserPasswordField);
			LineEdit password2 = (LineEdit) this.resolveWidget(regModel.RepeatPasswordField);
			Text password2Info = (Text) this.resolveWidget(regModel.RepeatPasswordField+"-info");
			if(password != null && password2 != null && password2Info != null){
				this.regModel.validatePasswordsMatchJS(password, password2, password2Info);
			}
		}
		if(!this.created_){
			regButton.setStyleClass("input_btn");
			logButton.setStyleClass("input_btn");
			cancelButton.setStyleClass("input_btn");
			this.bindWidget("regbtn", regButton);
			this.bindWidget("logbtn", logButton);
			this.bindWidget("canbtn", cancelButton);
			regButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
				@Override
				public void trigger(WMouseEvent arg) {
					RegisterView.this.doRegister();
				}
				
			});
			
			logButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
				@Override
				public void trigger(WMouseEvent arg) {
					Util.skipPage("/login_page");
				}
			});
		}
	}
	protected void doRegister(){
		AbstractUserDatabase.Transaction t = this.regModel.getUsers().startTransaction();
		this.updateModel(regModel);
		if(this.regModel.validate()){
			User user = this.regModel.doRegister();
			if(user.isValid()){
				this.regModel.registerUserDetails(user);
				this.regModel.getLogin().login(user);
			}else{
				this.update();
			}
		}else{
			this.update();
		}
		if(t != null)
			t.commit();
	}
	private void checkLoginName(){
		this.updateModelField(this.regModel, RegistrationModel.LoginNameField);
		this.regModel.validateField(RegistrationModel.LoginNameField);
		this.regModel.setValidated(RegistrationModel.LoginNameField, false);
		this.update();
	}
}
*/