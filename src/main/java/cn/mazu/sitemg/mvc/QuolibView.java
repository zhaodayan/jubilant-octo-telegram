package cn.mazu.sitemg.mvc;

import java.util.List;

import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.QuotLibrary;
import cn.mazu.util.Util;
import cn.mazu.util.Util.QuotaType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.FormView.FieldData;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.web.form.AbstractToggleButton;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;

public class QuolibView extends HtmlTemplate{
	private QuotLibrary qtra;
	private QuotLibrary qmgp;
	private SitemgEntity smgEntity;
	private QuolibModel qlModel;
	private QuolibModelm qlModelm;
	public QuolibView(){
		qlModel = new QuolibModel();
		qlModelm = new QuolibModelm();
		smgEntity = new SitemgEntity();
		List<QuotLibrary> qlst = smgEntity.getQuotLibrary(QuotaType.TRADI);
		if(qlst.size()!=0){
			qtra = qlst.get(0);
			updateQuolibView(qtra);
		}else
			this.qtra = new QuotLibrary();
		
		List<QuotLibrary> qlsm = smgEntity.getQuotLibrary(QuotaType.MGP);
		if(qlsm.size()!=0){
			qmgp = qlsm.get(0);
			updateQuolibViewM(qmgp);
		}else
			this.qmgp = new QuotLibrary();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		String text="<div class=\"wrap\" style='width:730px;'>" +
				"<h3>传统模具报价</h3>${form-trad}" +
				"<h3>MGP模具报价</h3>${form-mgp}" +
				"</div>";
		final FormView formtrad = new FormView(){
			@Override
			protected Widget createFormWidget(String field) {
				// TODO Auto-generated method stub
				return createFormWidgetT(field);
			}
			/*@Override
			public void updateModelField(FormModel model, String field) {
				model.setValue(field, model.getValue(field).toString().replace(",", ""));
			}*/
		};
		final FormView formmgp = new FormView(){
			@Override
			protected Widget createFormWidget(String field) {
				// TODO Auto-generated method stub
				return createFormWidgetM(field);
			}
			/*@Override
			public void updateModelField(FormModel model, String field) {
			   model.setValue(field, model.getValue(field).toString().replace(",", ""));
			}*/
		};
		formtrad.setTemplateText(tr("form-quoLib"),TextFormat.XHTMLUnsafeText);
		PushButton savebtn = new PushButton("保  存");
		savebtn.setStyleClass("btn-primary editcls");
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				formtrad.updateModel(qlModel);
				if(formtrad.validationPassed(qlModel)){
					if(qlModel.saveOrUpdate(qtra,QuotaType.TRADI)){
						Util.infoMessage(tr("Information please").toString(), "保存成功");
						QuolibView.this.draw(parent, args);
					}
				}
			}
		});
		formtrad.bindWidget("savebtn", savebtn);
		formtrad.updateView(qlModel);
		
		formmgp.setTemplateText(tr("form-quoLib"), TextFormat.XHTMLUnsafeText);
		PushButton savebtn2 = new PushButton("保  存");
		savebtn2.setStyleClass("btn-primary editcls");
		savebtn2.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				formmgp.updateModel(qlModelm);
				if(formmgp.validationPassed(qlModelm)){
					if(qlModelm.saveOrUpdate(qmgp,QuotaType.MGP)){
						Util.infoMessage(tr("Information please").toString(), "保存成功");
						QuolibView.this.draw(parent, args);
					}
				}
			}
		});
		formmgp.bindWidget("savebtn", savebtn2);
		formmgp.updateView(qlModelm);
		this.bindWidget("form-trad", formtrad);
		this.bindWidget("form-mgp", formmgp);
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);		
		return this;
	}

	//@Override
	protected Widget createFormWidgetT(String field) {
		FormWidget result = null;
		if(field.equals(QuolibModel.mdqcycField)||field.equals(QuolibModel.mdqpriceField)
				||field.equals(QuolibModel.mobcycField)||field.equals(QuolibModel.mobpriceField)
				||field.equals(QuolibModel.modcycField)||field.equals(QuolibModel.modpriceField)
				||field.equals(QuolibModel.mxtcycField)||field.equals(QuolibModel.mxtpriceField))
			result = new LineEdit();
		return result;
	}
	protected Widget createFormWidgetM(String field) {
		FormWidget result = null;
		if(field.equals(QuolibModelm.mdqcycField)||field.equals(QuolibModelm.mdqpriceField)
				||field.equals(QuolibModelm.mobcycField)||field.equals(QuolibModelm.mobpriceField)
				||field.equals(QuolibModelm.modcycField)||field.equals(QuolibModelm.modpriceField)
				||field.equals(QuolibModelm.mxtcycField)||field.equals(QuolibModelm.mxtpriceField))
			result = new LineEdit();
		return result;
	}
	
	private void updateQuolibView(QuotLibrary qt){
		qlModel.setValue(qlModel.mdqcycField,qt.getMdqpcycle());
		qlModel.setValue(qlModel.mdqpriceField,qt.getMdqunitPrice() );
		qlModel.setValue(qlModel.modcycField,qt.getModpcycle());
		qlModel.setValue(qlModel.modpriceField,qt.getModunitPrice());
		qlModel.setValue(qlModel.mobcycField, qt.getMobpcycle());
		qlModel.setValue(qlModel.mobpriceField, qt.getMobunitPrice());
		qlModel.setValue(qlModel.mxtcycField,qt.getMxtpcycle());
		qlModel.setValue(qlModel.mxtpriceField,qt.getMxtunitPrice());
	}
	private void updateQuolibViewM(QuotLibrary qt){
		qlModelm.setValue(qlModelm.mdqcycField,qt.getMdqpcycle());
		qlModelm.setValue(qlModelm.mdqpriceField,qt.getMdqunitPrice() );
		qlModelm.setValue(qlModelm.modcycField,qt.getModpcycle());
		qlModelm.setValue(qlModelm.modpriceField,qt.getModunitPrice());
		qlModelm.setValue(qlModelm.mobcycField, qt.getMobpcycle());
		qlModelm.setValue(qlModelm.mobpriceField, qt.getMobunitPrice());
		qlModelm.setValue(qlModelm.mxtcycField,qt.getMxtpcycle());
		qlModelm.setValue(qlModelm.mxtpriceField,qt.getMxtunitPrice());
	}
}
