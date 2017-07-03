package cn.mazu.sitemg.mvc;


import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Quotation;
import cn.mazu.ui.Askpriceinfo;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class QuotationView extends FormView{
	private Quotation q;
	private SitemgEntity smgEntity;
	private QuotationModel qModel;
	private PopBox pb;
	private Askpriceinfo gt;
	
	public QuotationView(Quotation q,PopBox pb,Askpriceinfo gt){
		qModel = new QuotationModel();
		smgEntity = new SitemgEntity();
		this.q = q;
		this.pb = pb;
		this.gt = gt;
		if(q!=null)
			updateQuoView();
		else
			this.q = new Quotation();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		this.setTemplateText(tr("form-quot"), TextFormat.XHTMLUnsafeText);
		
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		
		this.bindString("submit", "");//只能查看，不能编辑，新建
		
		this.bindWidget("close", closebtn);    	
		this.updateView(qModel);
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		FormWidget result = null;
		if(field.equals(QuotationModel.caddrField)||field.equals(QuotationModel.clinkField)
				||field.equals(QuotationModel.cnameField)||field.equals(QuotationModel.cteleField)
				||field.equals(QuotationModel.emailField)||field.equals(QuotationModel.mdqNumField)
				||field.equals(QuotationModel.modNumField)||field.equals(QuotationModel.mxtNumField)
				||field.equals(QuotationModel.modTypeField)||field.equals(QuotationModel.timeEstField)
				||field.equals(QuotationModel.costEstField)||field.equals(QuotationModel.mobNumField)){
			result = new LineEdit();
			result.setAttributeValue("style", "border:none;background: #f1f2f3;");
		}
		return result;
	}
	
	private void updateQuoView(){
		qModel.setValue(qModel.caddrField,q.getCaddr());
		qModel.setValue(qModel.clinkField,q.getClink());
		qModel.setValue(qModel.cnameField,q.getCname());
		qModel.setValue(qModel.cteleField,q.getCtele());
		qModel.setValue(qModel.emailField,q.getCmail());
		qModel.setValue(qModel.mdqNumField,q.getMdqNum());
		qModel.setValue(qModel.modNumField,q.getModNum());
		qModel.setValue(qModel.mobNumField,q.getMobNum());
		qModel.setValue(qModel.modTypeField,q.getType().name);
		qModel.setValue(qModel.mxtNumField,q.getMxtNum());
		qModel.setValue(qModel.timeEstField, q.getTotalDay());
		qModel.setValue(qModel.costEstField, q.getTotalCost());
	}
	

}
