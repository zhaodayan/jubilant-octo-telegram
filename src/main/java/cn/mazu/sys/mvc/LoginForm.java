/*package cn.mazu.sys.mvc;

import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class LoginForm extends FormView{
	private AccountPModel apModel;
	public LoginForm() {
		apModel = new AccountPModel();
		apModel.removeField(apModel.eid);
		apModel.removeField(apModel.menu);
		apModel.removeField(apModel.permission);
		apModel.removeField(apModel.staff);
	}
	@Override
	public Widget draw(Template parent, String... args) {
		this.setTemplateText(tr("login_form"),TextFormat.XHTMLUnsafeText);
		Anchor submitbtn = new Anchor(new Link(""), "提交");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				
			}
		});
		this.bindWidget("submitbtn", submitbtn);
		this.updateView(apModel);
		return this;
	}
	@Override
	protected Widget createFormWidget(String field) {
		Widget w = null;
		if(field.equals(apModel.username)){
			LineEdit le = new LineEdit();
			w = le;
		}
		if(field.equals(apModel.password)){
			LineEdit le = new LineEdit();
			le.setEchoMode(LineEdit.EchoMode.Password);
			w = le;
		}
		return w;
	}
	private void resetInput(){
		apModel.setValue(apModel.username, "");
		apModel.setValue(apModel.password, "");
		this.updateView(apModel);
	}
}
*/