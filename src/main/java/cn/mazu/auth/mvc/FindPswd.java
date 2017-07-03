/*package cn.mazu.auth.mvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;

import cn.mazu.WApplication;
import cn.mazu.WObject;
import cn.mazu.auth.Identity;
import cn.mazu.auth.Session;
import cn.mazu.auth.User;
import cn.mazu.auth.db.AbstractUserDatabase;
import cn.mazu.auth.db.UserDatabase;
import cn.mazu.auth.serv.AuthService;
import cn.mazu.util.HibernateUtil;
import cn.mazu.util.Util;
import cn.mazu.utils.MailUtils;
import cn.mazu.utils.WLength;
import cn.mazu.utils.WString;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.WebSession;
import cn.mazu.widget.FormView;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.validator.Validator;

public class FindPswd extends FormView {
	
	private String style="";
	private FindPswdModel fpModel;
	private Session session_;
	public FindPswd(){
		Session.configureAuth();
		session_ = new Session(HibernateUtil.getEntityManager());
		session_.getAuth().setEmailVerificationEnabled(true);
		fpModel = new FindPswdModel(session_.getAuth(),  (UserDatabase) session_.getUserDatabase(), this);;
		this.fpModel.addPasswordAuth(Session.getPasswordAuth());
	}

	@Override
	public Widget draw(Template parent, String... args) {
		for(String str:args){
			if(str.contains("form"))
				tr(str).toString().replace("${base.path}", tr("base.path").toString());
			    this.setTemplateText(tr(str),TextFormat.XHTMLUnsafeText);
		}
		final PushButton sendMail = new PushButton(tr("send"));
		sendMail.setStyleClass("button");
		this.bindWidget("sendMail", sendMail);
		sendMail.clicked().addListener(sendMail, new Signal.Listener() {
			@Override
			public void trigger() {
				FindPswd.this.attemptSendMail();
			}
		});
		final PushButton cancelBtn = new PushButton(tr("cancel"));
		cancelBtn.setStyleClass("button");
		cancelBtn.setAttributeValue("style", "margin-left:20px;background-color:#999;");
		this.bindWidget("cancelBtn", cancelBtn);
		cancelBtn.clicked().addListener(cancelBtn,new Signal.Listener() {
			@Override
			public void trigger() {
				// TODO Auto-generated method stub
				Util.skipPage("login_page");
			}
		});
		final Anchor anchor = new Anchor(new Link("javascript:void(0);"));
		anchor.setImage(new Image(new Link(new AuthCode())));
		this.bindWidget("img", anchor);
		anchor.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				FindPswd.this.repaintAnchor(anchor);
			}
		});
		
		this.setAttributeValue("style", style);
		this.updateView(fpModel);
		return this;
	}
	@Override
	protected Widget createFormWidget(final String field) {
		FormWidget result = null;
		if(field.equals(fpModel.emailField)){
			result = new LineEdit();
			result.setWidth(new WLength(150));
			result.enterPressed().addListener(result, new Signal.Listener() {
				@Override
				public void trigger() {
					FindPswd.this.attemptSendMail();
				}
			});
		}
		if(field.equals(fpModel.authCodeField)){
			result = new LineEdit();
			result.setWidth(new WLength(150));
			result.enterPressed().addListener(result, new Signal.Listener() {
				@Override
				public void trigger() {
					FindPswd.this.attemptSendMail();
				}
			});
		}
		return result;
	}
	
	public void repaintAnchor(Anchor anchor){
		anchor.removeChild(anchor.getImage());
		anchor.setImage(new Image(new Link(new AuthCode())));
		this.bindWidget("img", anchor);
		
	}
	public void attemptSendMail(){
		this.updateModel(fpModel);
		if(fpModel.validate()){
			boolean b = fpModel.sendMail();
			WString mailinfo = b?tr("havesent"):tr("sendError");
			fpModel.setValidation(fpModel.emailField,new Validator.Result(Validator.State.Invalid,mailinfo));
		}
		this.updateView(fpModel);
	}
	
	public class FindPswdModel extends FormBaseModel{
		public static final String emailField = "email";
		public static final String authCodeField = "authcode";
		
		
		public FindPswdModel(AuthService baseAuth, UserDatabase users,
				WObject parent) {
			super(baseAuth, users, parent);
			//this.throttlingDelay_ = 0;
			this.reset();
		}
		
		public void reset(){
			this.addField(emailField,WString.tr("Auth.email-info"));
			this.addField(authCodeField,WString.tr("auth-code-info"));
		}
		
		@Override
		public boolean validateField(String field) {
		    if(field.equals(emailField)){//�����ʽ��֤
		    	String email = this.valueText(field).trim();
		    	if ((int) email.length() < 3
						|| email.indexOf('@') == -1) {
		    		this.setValidation(field,new Validator.Result(Validator.State.Invalid,tr("email-invalid")));
		    		return false;
				}else if(email.length()==0){
					this.setValidation(field,new Validator.Result(Validator.State.Invalid,tr("email-require")));
				    return false;	
				}
		    	return true;
		    }
		    if(field.equals(authCodeField)){
		    	String inputacode = this.valueText(field);
		    	String code_ = WebSession.Handler.getInstance().getRequest().getSession().getAttribute(AuthCode.RANDOMCODEKEY).toString();
		    	if(inputacode == null || inputacode.trim().equals("")){
					this.setValidation(authCodeField, new Validator.Result(Validator.State.Invalid,tr("auth-code-require")));
					return false;
				}else if(!inputacode.toUpperCase().equals(code_)){
					this.setValidation(authCodeField, new Validator.Result(Validator.State.Invalid,tr("auth-code-error")));
					return false;
				}
		    	return true;
		    }
			return super.validateField(field);
		}
		
		@Override
		public boolean validate(){
			//UserDatabase.Transaction t = this.getUsers().startTransaction();
			boolean result = super.validate();
			if(t!=null)
				t.commit();
			return result;
		}
		
		public boolean sendMail(){
		   String emailAddress = this.valueText(emailField);
		   AbstractUserDatabase.Transaction t = this.getUsers().startTransaction();
			try {
				User user = this.getUsers().findWithEmail(emailAddress);
				if(user.isValid()){
					String password = Util.randomPassword();
					this.getPasswordAuth().updatePassword(user, password);
					//System.out.println(user.getEmail()+"----"+password);
					this.sendLostPasswordMail(emailAddress,user,password);
				}
				t.commit();
				if(user.isValid())
					return true;
			} catch (Exception e) {
				t.rollback();
				e.printStackTrace();
			}
			return false;
		}
		
		protected void sendLostPasswordMail(String address,User user,String password)
				throws javax.mail.MessagingException, UnsupportedEncodingException,
				IOException {
			Authenticator auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("services@i-huo.com",
							"qwe123!@#");
				}
			};
			Message message = new MimeMessage(
					javax.mail.Session.getDefaultInstance(
							MailUtils.getDefaultProperties(), auth));
			String url = "http://www.lookkey.com.cn/wkey/login_page";
			sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			//message.set
			message.addRecipient(javax.mail.Message.RecipientType.TO,
					new javax.mail.internet.InternetAddress(address));
			//msg.setSubject("=?GB2312?B?"+enc.encode(subject.getBytes())+"?=");
			message.setSubject("=?GB2312?B?"+enc.encode(WString.tr("lostpasswordmail.subject").toString().getBytes())+"?=");
			MailUtils.setBody(message,WString.tr("lostpasswordmail.body").arg(user.getIdentity(Identity.LoginName)).arg(password).arg(url));
			MailUtils.addHtmlBody(message,WString.tr("lostpasswordmail.htmlbody").arg(user.getIdentity(Identity.LoginName)).arg(password).arg(url));
			this.sendMail(message);
		}
		public void sendMail(javax.mail.Message message)
				throws javax.mail.MessagingException, UnsupportedEncodingException,
				IOException {
			javax.mail.Message m = message;
			if (MailUtils.isEmpty(m.getFrom())) {
				String senderName = WString.tr("Auth.service").toString();
				String senderAddress = "services@i-huo.com";
				senderName = WApplication.readConfigurationProperty(
						"auth-mail-sender-name", senderName);
				senderAddress = WApplication.readConfigurationProperty(
						"auth-mail-sender-address", senderAddress);
				m.setFrom(new javax.mail.internet.InternetAddress(senderAddress,
						senderName));
			}
			//m.writeTo(System.out);
			MailUtils.sendMail(m);
		}
	}
	
	



}
*/