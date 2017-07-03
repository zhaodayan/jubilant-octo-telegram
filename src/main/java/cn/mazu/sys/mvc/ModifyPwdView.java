package cn.mazu.sys.mvc;

import cn.mazu.WApplication;
import cn.mazu.auth.PasswordHash;
import cn.mazu.auth.Session;
import cn.mazu.auth.serv.PasswordService;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.util.HibernateUtil;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.AnchorTarget;

public class ModifyPwdView extends HtmlTemplate{
   private LineEdit previousP,newP,repeatP;
   private String previousPwdTxt,newPwdTxt,repeatPwdTxt;
   private Entity entity = new Entity();
   private Session session_;// = new Session(HibernateUtil.getEntityManager());
   private AccountPermission ap  = entity.getCurrentAP();
   private PasswordHash hp = new PasswordHash(ap.getPasswordMethod(),ap.getPasswordSalt(),ap.getPasswordHash());
   private Boolean flag = true;
   
   public ModifyPwdView(){
	   Session.configureAuth();
	   session_ = new Session(HibernateUtil.getEntityManager());
   }
   
   @Override
	public Widget draw(final Template parent, final String... args) {
	    this.setTemplateText("" +
	    		"<div class='btn-group'>" +
	    		"<button class='btn  btn-warning'>欢迎登录，${logname}！</button>" +
	    		"<button class='btn  dropdown-toggle'><span class='caret'></span></button>" +
	    		"<ul class='dropdown-menu pull-right'>" +
	    		"<li>${helpBtn}</li>" +
	    		"<li>${pswdResetBtn}</li>" +
	    		"<li>${logOut}</li>" +
	    		"</ul></div>" +
					"" +
					"<div class='PopupLayer'>" +
					"<div id='PopTitle'>修改密码</div>" +
					"<ul>" +
					"<li><label>原密码<font color='#ff0000'>*</font></label>" +
					"<div class='fl'>${previousP}" +
					"${pp-info}</div>" +
					"</li>" +
					"<li><label>新密码<font color='#ff0000'>*</font></label>" +
					"<div class='fl'>${newP}" +
					"${newp-info}</div>" +
					"</li>" +
					"<li><label>确认密码<font color='#ff0000'>*</font></label>" +
					"<div class='fl'>${repeatP}" +
					"${rp-info}</div>" +
					"</li>" +
					"<li>${saveBtn}" +
					"</li>" +
					"</ul>" +
					"<a id='closeLayer' title='关闭'>×</a>" +
					"</div>" +
					"",TextFormat.XHTMLUnsafeText);
	    this.setStyleClass("nav_setUp");
	    
//	    PushButton helpBtn = new PushButton("系统帮助");
	    Anchor helpBtn = new Anchor(new Link(Link.Type.Url,tr("base.path")+"/app/help_page"),"系统帮助");
	    helpBtn.setTarget(AnchorTarget.TargetNewWindow);
	    helpBtn.setStyleClass("setButton");
	    this.bindWidget("helpBtn", helpBtn);
	    helpBtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				String url="/help_page";
				//Util.skipPage(url);
			}
		});
	    
	    String logname =  Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("username"));
	    this.bindString("logname", logname);
	    
	    PushButton pswdResetBtn = new PushButton(tr("pswdReset"));
	    pswdResetBtn.setStyleClass("setButton");
	    pswdResetBtn.setId("pswdResetBtn");
		this.bindWidget("pswdResetBtn", pswdResetBtn);
		
	    PushButton logOut = new PushButton(tr("logOut"));	
	    logOut.setStyleClass("setButton");
	    logOut.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				String url="/login_page";
				Util.skipPage(url);
				WApplication.getInstance().setCookie("username", Util.URLEString(""),-1);
				WApplication.getInstance().setCookie("password",Util.URLEString(""),-1);
				WApplication.getInstance().setCookie("loginid",Util.URLEString(""),-1);
				WApplication.getInstance().setCookie("menustr",Util.URLEString(""),-1);
				//WApplication.getInstance().setCookie("menuper",Util.URLEString(""),-1);
				WApplication.getInstance().setCookie("dataCode",Util.URLEString(""),-1);
				WApplication.getInstance().setCookie("pDataCode",Util.URLEString(""),-1);
			}
		});
	    
		this.bindWidget("logOut", logOut);
	    
	    previousP = new LineEdit();
	    previousP.setId("prevP");
	    newP = new LineEdit();
	    newP.setId("newP");
	    repeatP = new LineEdit();
	    repeatP.setId("repeatP");
	    
	    previousP.setEchoMode(LineEdit.EchoMode.Password);
	    newP.setEchoMode(LineEdit.EchoMode.Password);
	    repeatP.setEchoMode(LineEdit.EchoMode.Password);
	    this.bindWidget("previousP", previousP);
	    this.bindWidget("newP", newP);
	    this.bindWidget("repeatP", repeatP);
	    this.bindString("pp-info", "");
	    this.bindString("newp-info", "");
	    this.bindString("rp-info", "");
	    Anchor saveBtn = new Anchor(new Link(""),tr("save"));
	    saveBtn.setStyleClass("editcls submitBtn");
	    saveBtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
                if(validateInput()){				
                	PasswordService.AbstractVerifier pa = session_.getPasswordAuth().getVerifier();
                	hp = pa.hashPassword(newPwdTxt);
                	ap.setPasswordHash(hp.getValue());
					ap.setPasswordSalt(hp.getSalt());
					ap.setPasswordMethod(hp.getFunction());
					//ap.setPassword(newPwdTxt);
					if(entity.saveOrUpdate(ap)){
						ModifyPwdView.this.doJavaScript("$('.PopupLayer').animate({top:$(document).height(),opacity:0},1000);");
						flag = true;
						ModifyPwdView.this.doJavaScript(getJavaScript());
					}else
						Util.infoMessage("提示", "保存失败");
                }else{
                	ModifyPwdView.this.doJavaScript("$('.PopupLayer').css({'top':75+'px','opacity':1});");
                	flag= false;
                }				
			}
		});
	    this.bindWidget("saveBtn", saveBtn);
	    
		return this;
	}

    @Override
public void updateDom(DomElement element, boolean all) {
	super.updateDom(element, all);
	element.callJavaScript(getJavaScript());
}

	private String getJavaScript() {
		return "$(function(){" +
				"$('.dropdown-toggle').click(function(){" +
				"$('.dropdown-menu').toggle();" +
				"});" +
				""+(flag?
"	if($.browser.msie && $.browser.version<=6)"+
"	{$('.PopupLayer').css('position','absolute');}"+
"	$('.PopupLayer').css({'top':$('.PopupLayer').outerHeight()*-1+'px','opacity':0});":"")+

"	function notice_show()"+
"	{"+
"		var browser_visible_region_height=document.documentElement.clientHeight;"+
"		var element_height=$('.PopupLayer').outerHeight();"+
"		var element_show_top=(browser_visible_region_height-element_height)/2;"+
"		$('.PopupLayer').stop(true).animate({top:75,opacity:1},1000);"+
"	}"+

"	function notice_hidden()"+
"	{"+
"		var element_height=$('.PopupLayer').outerHeight();"+
"		var ee=-element_height;"+
"		$('.PopupLayer').stop(true).animate({top:ee,opacity:0},1000);"+
"	}"+

"	$('#closeLayer').click(function(){"+
"		$('.PopupLayer').animate({top:$(document).height(),opacity:0},1000);"+
"	});"+

"	$('#pswdResetBtn').click(function(){"+
"		notice_show();"+
"	});"+
"	function check()"+
"	{"+
"		var kk=$('.PopupLayer').outerHeight()*-1;"+
"		var ww=$(document).height();"+
"		var qq=parseInt($('.PopupLayer').css('top'));"+
		
"		if(qq == kk || qq == ww)"+
"		{"+
"			return;"+
"		}"+
"		else"+
"		{"+
"			var browser_visible_region_height=document.documentElement.clientHeight;"+
"			var element_height=$('.PopupLayer').outerHeight();"+
"			var element_show_top=(browser_visible_region_height-element_height)/2;"+
"			$('.PopupLayer').stop(true).animate({top:element_show_top},1500);"+
"		}"+
"	}"+

"$(window).resize(function(){check();});" +
"});";
	}

	private boolean validateInput(){
    	boolean validateFlag = true;
    	this.bindString("pp-info", "");
		this.bindString("newp-info", "");
		this.bindString("rp-info", "");
		previousPwdTxt = previousP.getText().trim();
		newPwdTxt = newP.getText().trim();
		repeatPwdTxt = repeatP.getText().trim();
		if(newPwdTxt==null||newPwdTxt.equals("")){
			ModifyPwdView.this.bindString("newp-info", tr("inputMust"));
			validateFlag = false;
		}
		if(repeatPwdTxt==null||repeatPwdTxt.equals("")){
			ModifyPwdView.this.bindString("rp-info", tr("inputMust"));
			validateFlag = false;
		}
		if(newPwdTxt!=null&&!newPwdTxt.equals("")&&repeatPwdTxt!=null&&!repeatPwdTxt.equals("")
				&&!newPwdTxt.equals(repeatPwdTxt)){
			ModifyPwdView.this.bindString("rp-info", tr("inconsistentInput"));
			validateFlag = false;
		}
		if(previousPwdTxt==null||previousPwdTxt.equals("")){
			ModifyPwdView.this.bindString("pp-info", tr("inputMust"));
			validateFlag = false;
		}else{
			if(session_.getPasswordAuth()!=null&&session_.getPasswordAuth().getVerifier()!=null){
				if(!session_.getPasswordAuth().getVerifier().verify(previousPwdTxt, hp)){
					ModifyPwdView.this.bindString("pp-info", tr("wrongPreviousPwd"));
					validateFlag = false;
				}
			}/*else{
				ModifyPwdView.this.bindString("pp-info", tr("wrongPreviousPwd"));
				validateFlag = false;
			}*/
		}
    	return validateFlag;
    }
}
