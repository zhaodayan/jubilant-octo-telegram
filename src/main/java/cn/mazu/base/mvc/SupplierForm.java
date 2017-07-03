package cn.mazu.base.mvc;


import cn.mazu.base.entity.Supplier;
import cn.mazu.ui.SupplierMgr;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FileMgrList;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class SupplierForm extends FormView {
    private Supplier supplier;
    private SupplierMgr supMgr;
    private PopBox popBox;
    private String title;
    private SupplierModel supModel;
    
    public SupplierForm(String title,Supplier supplier,PopBox popBox,SupplierMgr supMgr) {
    	this.supplier = supplier;
    	this.popBox = popBox;
    	this.supMgr = supMgr;
    	this.title = title;
    	supModel = new SupplierModel();
    	if(supplier!=null&&!title.equals("新建"))
    		updateClientView();
    	else
    		this.supplier = new Supplier();
	}
    @Override
    public Widget draw(final Template parent, String... args) {
        this.setTemplateText(tr("supplier-form"), TextFormat.XHTMLUnsafeText);
        Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				SupplierForm.this.updateModel(supModel);
				if(validationPassed(supModel)){
					if(supModel.saveOrUpdate(supMgr.getDBEntity(),supplier)){
						if(supMgr.getSelectTr()!=null&&!supMgr.getSelectTr().getId().equals(supplier.getId())||supMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							supMgr.drawGrid();
							supMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+supMgr.getPaginationStart()*supMgr.getPaginationMax()+"});");
						}else if(supMgr.getSelectTr()!=null&&supMgr.getSelectTr().getId().equals(supplier.getId())){//选中某一行，确实执行的是【编辑】操作
							supMgr.refreshSelTr(supplier);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}
					popBox.setAttributeValue("style", "display:none");
				}
			}
		});
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				popBox.setAttributeValue("style", "display:none");
			}
		});
		if(title.equals("查看"))
			this.bindString("submit", "");
		else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(supModel);
    	return this;
    }
    @Override
    protected Widget createFormWidget(String field) {
    	Widget le = null;
		  if(field.equals(supModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(supModel.description)){
			le = new TextArea();
		  }else if(field.equals(supModel.addition)){
			 le = new FileMgrList("supplier",supplier.getId(),!title.equals("查看"));
		  }else{
			  le = new LineEdit();  
		  }
			return le;
    }
    private void updateClientView(){
    	supModel.setValue(supModel.eid, supplier.getId());
    	supModel.setValue(supModel.linkman, supplier.getLinkman());
    	supModel.setValue(supModel.linktel, supplier.getLinktel());
    	supModel.setValue(supModel.name, supplier.getName());
    	supModel.setValue(supModel.fax, supplier.getLinkfax());
    	supModel.setValue(supModel.email, supplier.getLinkemail());
    }
}
