package cn.mazu.storage.mvc;



import cn.mazu.storage.entity.Inventory;
import cn.mazu.ui.InventoryInitialMgr;
import cn.mazu.util.Util;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.validator.DoubleValidator;

public class InventoryInitialForm extends FormView{
   private PopBox pb;
   private InventoryInitialMgr iiMgr;
   private Inventory inventory;
   private InventoryInitialModel iiModel;
   private String title;
//   private ComboBox storeSel;
   
  public InventoryInitialForm(String title,Inventory inventory,PopBox pb,InventoryInitialMgr iiMgr) {
	this.inventory = inventory;
	this.pb = pb;
	this.iiMgr = iiMgr;
	this.title = title;
	iiModel = new InventoryInitialModel();
	if(inventory!=null&&!title.equals("新建")){
		updateClientView();
	}else
		this.inventory = new Inventory();
   }
   @Override
	public Widget draw(final Template parent, String... args) {
//	   storeSel = WidgetUtil.initSelFromEnum(StoreCategory.class);
	   this.setTemplateText(tr("inventoryi-form"),TextFormat.XHTMLUnsafeText);
		Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				InventoryInitialForm.this.updateModel(iiModel);
				if(validationPassed(iiModel)){
					if(iiModel.saveOrUpdate(iiMgr.getDBEntity(),inventory)){
						if(iiMgr.getSelectTr()!=null&&!iiMgr.getSelectTr().getId().equals(inventory.getId())||iiMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							iiMgr.drawGrid();
							iiMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+iiMgr.getPaginationStart()*iiMgr.getPaginationMax()+"});");
						}else if(iiMgr.getSelectTr()!=null&&iiMgr.getSelectTr().getId().equals(inventory.getId())){//选中某一行，确实执行的是【编辑】操作
							iiMgr.refreshSelTr(inventory);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}
					pb.setAttributeValue("style", "display:none");
				}
			}
		});
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		if(title.equals("查看")){
			this.bindString("submit", "");
		}else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(iiModel);
		return this;
	}
    @Override
	protected Widget createFormWidget(String field) {
	FormWidget le = null;
	  if(field.equals(iiModel.itemunit)
			  ||field.equals(iiModel.specification)||field.equals(iiModel.itemname)
			  ||field.equals(iiModel.supplier)
			  ||field.equals(iiModel.texture)||field.equals(iiModel.itemcode)){
		  LineEdit input = new LineEdit();
	      if(!title.equals("新建"))input.setReadOnly(true);
	      le = input;
	 /* }else if(field.equals(iiModel.storecategory)){
		  le = storeSel;*/
	  }else if(field.equals(iiModel.itemnum)){
		  LineEdit input = new LineEdit();
		  DoubleValidator iv = new DoubleValidator();
		  input.setMaxLength(7);
		  input.setValidator(iv);
		  input.setPlaceholderText("请填写数字");
		  if(!title.equals("新建"))input.setReadOnly(true);
		  le = input;
	  }else if(field.equals(iiModel.threshold)){
		  LineEdit input = new LineEdit();
		  DoubleValidator dv = new DoubleValidator();
		  input.setMaxLength(7);
		  input.setValidator(dv);
		  input.setPlaceholderText("请填写数字");
		  le = input;
	  }else if(field.equals(iiModel.checkdate)){
		  DateEdit d = new DateEdit();
		  d.setFormat("yyyy-MM-dd");
		  if(!title.equals("新建"))d.setReadOnly(true);
		  le = d;
	  }else if(field.equals(iiModel.eid)){
		  le =new LineEdit();
		  le.hide();}
		return le;
	}
   private void updateClientView(){
	  iiModel.setValue(iiModel.eid, inventory.getId());
	  //iiModel.setValue(iiModel.allocation, inventory.getAllocation());
	  iiModel.setValue(iiModel.itemname, inventory.getName());
	  iiModel.setValue(iiModel.itemnum, inventory.getAmount());
	  iiModel.setValue(iiModel.itemunit, inventory.getUnit());
	  iiModel.setValue(iiModel.checkdate, Util.formatDateyyyyMMdd(inventory.getCheckDate()));
	  iiModel.setValue(iiModel.specification, inventory.getSpec());
	  iiModel.setValue(iiModel.supplier, inventory.getSupplier());
	  iiModel.setValue(iiModel.texture, inventory.getTexture());
	  iiModel.setValue(iiModel.itemcode, inventory.getCode());
	  iiModel.setValue(iiModel.threshold, inventory.getThreshold());
	  /*if(inventory.getStoreCategory()!=null){
		  iiModel.setValue(iiModel.storecategory, inventory.getStoreCategory().name);
	  }*/
   }
   /* @Override
	public void updateModelField(FormModel model,String field) {
	    if(field.equals(iiModel.storecategory))
	    	iiModel.setValue(field, WidgetUtil.getSelEO(storeSel));
	    else
		   super.updateModelField(model, field);
	}*/
}
