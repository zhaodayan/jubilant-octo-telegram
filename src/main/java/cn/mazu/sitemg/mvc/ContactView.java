package cn.mazu.sitemg.mvc;

import java.util.List;

import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Contact;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;

public class ContactView extends FormView{
	private Contact c;
	private SitemgEntity smgEntity;
	private ContactModel cModel;
	
	public ContactView(){
		cModel = new ContactModel();
		smgEntity = new SitemgEntity();
		List<Contact> cls = smgEntity.getContactList();
		if(cls.size()!=0){
			c = cls.get(0);
			updateContactView(c);
		}else
			this.c = new Contact();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		this.setTemplateText(tr("form-link"), TextFormat.XHTMLUnsafeText);
		this.updateView(cModel);
		PushButton savebtn = new PushButton("保  存");
		savebtn.setStyleClass("btn-primary editcls");
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				ContactView.this.updateModel(cModel);
				if(validationPassed(cModel)){
					if(cModel.saveOrUpdate(c)){
						Util.infoMessage(tr("Information please").toString(), "保存成功");
						ContactView.this.draw(parent, args);
					}
					
				}
			}
		});
		this.bindWidget("savebtn", savebtn);
		
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		FormWidget result = null;
		if(field.equals(ContactModel.addrField)||field.equals(ContactModel.emailField)
				||field.equals(ContactModel.faxField)||field.equals(ContactModel.manField)
				||field.equals(ContactModel.QQField)||field.equals(ContactModel.siteField)
				||field.equals(ContactModel.teleField))
			result = new LineEdit();
		return result;
	}
	
	private void updateContactView(Contact c){
		cModel.setValue(cModel.addrField, c.getAddress());
		cModel.setValue(cModel.emailField, c.getEmail());
		cModel.setValue(cModel.faxField, c.getFax());
		cModel.setValue(cModel.manField, c.getCname());
		cModel.setValue(cModel.QQField, c.getcQQ());
		cModel.setValue(cModel.siteField, c.getSiteaddr());
		cModel.setValue(cModel.teleField, c.getTele());
	}

}
