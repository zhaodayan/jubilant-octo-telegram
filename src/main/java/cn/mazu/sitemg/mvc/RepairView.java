package cn.mazu.sitemg.mvc;


import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Repair;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class RepairView extends FormView{
	private Repair r;
	private SitemgEntity smgEntity;
	private RepairModel rModel;
	private PopBox pb;
	public RepairView(Repair r,PopBox pb){
		rModel = new RepairModel();
		smgEntity = new SitemgEntity();
		this.r = r;
		this.pb = pb;
		updateRepairView();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		this.setTemplateText(tr("form-repair"), TextFormat.XHTMLUnsafeText);
		
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
		this.updateView(rModel);
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		FormWidget result = null;
		if(field.equals(RepairModel.applicantfield)||field.equals(RepairModel.maternamefield)||
				field.equals(RepairModel.modnamefield)||field.equals(RepairModel.modnumfield)||
				field.equals(RepairModel.pronamefield)||field.equals(RepairModel.pronumfield)){
			result = new LineEdit();
			result.setAttributeValue("style", "border:none;background: #f1f2f3;");
		}else if(field.equals(RepairModel.orequestfield)||field.equals(RepairModel.rcausefield)){
			result = new TextArea();
			result.setAttributeValue("style", "border:none;background: #f1f2f3;");
		}else if(field.equals(RepairModel.adatefield)||field.equals(RepairModel.cdatefield)){
			DateEdit de = new DateEdit();
			de.setFormat("yyyy-MM-dd");
			result = de;
		}
		
		return result;
	}
	
	private void updateRepairView(){
		rModel.setValue(rModel.adatefield,Util.formatDateyyyyMMdd(r.getApplyDate()));
		rModel.setValue(rModel.applicantfield,r.getApplicant());
		rModel.setValue(rModel.cdatefield,Util.formatDateyyyyMMdd(r.getCompleteDate()));
		rModel.setValue(rModel.maternamefield,r.getMatername());
		rModel.setValue(rModel.modnamefield,r.getModname());
		rModel.setValue(rModel.modnumfield,r.getModid());
		rModel.setValue(rModel.orequestfield,r.getOtherRequest());
		rModel.setValue(rModel.pronamefield,r.getProname());
		rModel.setValue(rModel.pronumfield,r.getProid());
		rModel.setValue(rModel.rcausefield,r.getRepairCause());
	}
	
}
