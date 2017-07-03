package cn.mazu.sitemg.mvc;

import java.util.List;

import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Consult;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class ConsultView extends FormView{
	private Consult c;
	private ConsultModel cModel;
	private PopBox pb;
	public ConsultView(Consult c,PopBox pb){
		cModel = new ConsultModel();
		this.c = c;
		this.pb = pb;
		updateConsltView();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		this.setTemplateText(tr("form-cslt"), TextFormat.XHTMLUnsafeText);
		
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		this.bindWidget("close", closebtn);
		this.bindString("submit", "");
		this.updateView(cModel);
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		FormWidget result = null;
		if(field.equals(ConsultModel.addrfield)||field.equals(ConsultModel.companyfield)
				||field.equals(ConsultModel.emailfield)
				||field.equals(ConsultModel.faxfield)||field.equals(ConsultModel.namefield)
				||field.equals(ConsultModel.telefield)||field.equals(ConsultModel.titlefield)){
			result = new LineEdit();
			result.setAttributeValue("style", "border:none;background: #f1f2f3;");
		}else if(field.equals(ConsultModel.contentfield)){
			result = new TextArea();
			result.setAttributeValue("style", "border:none;background: #f1f2f3;");
		}
		return result;
	}
	
	private void updateConsltView(){
		cModel.setValue(cModel.addrfield,c.getCsaddr());
		cModel.setValue(cModel.companyfield,c.getCscompany());
		cModel.setValue(cModel.contentfield,c.getContent());
		cModel.setValue(cModel.emailfield,c.getCsemail());
		cModel.setValue(cModel.faxfield,c.getCsfax());
		cModel.setValue(cModel.namefield,c.getCsname());
		cModel.setValue(cModel.telefield,c.getCstele());
		cModel.setValue(cModel.titlefield,c.getTitle());
	}
	
}
