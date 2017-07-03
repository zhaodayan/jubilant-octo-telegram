package cn.mazu.doc.mvc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Supplier;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.ui.BuyDocMgr;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.WFileUpload;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.workorder.entity.WorkOrder;

public class SignDocForm extends FormView
{
  private SignDoc sd;
  private PopBox pb;
  private BuyDocMgr bdMgr;
  private SignDocModel sdModel;
  private String title;
  private WFileUpload wf;
  private Map<String, File> fileMap = new HashMap();
  private ComboBox workorderSel = new ComboBox(); 
  private ComboBox typeSel = new ComboBox(); 
  private ComboBox supplierSel = new ComboBox();
  private ComboBox typeStatusSel = new ComboBox();
  private Template t = new Template("");
  private Widget le = null,slinkman = null,sfax = null,slinktel = null;
  private String techStr = "";

  public SignDocForm(String title, SignDoc sd, PopBox pb, BuyDocMgr bdMgr) {
    this.sd = sd;
    this.pb = pb;
    this.bdMgr = bdMgr;
    this.title = title;
    this.sdModel = new SignDocModel();
    if ((sd != null) && (!title.equals("新建")))
      updateClientView();
    else {
      this.sd = new SignDoc();
    }
    this.t.setTemplateText("${panel}${wf}", TextFormat.XHTMLUnsafeText);
    this.t.bindString("panel", "");
    this.t.setAttributeValue("style", "display: inline-block;");
  }

  public Widget draw(final Template parent, String[] args) {
    this.workorderSel.clear();

    List workorderList = this.bdMgr.getDBEntity().getSubListQBC(WorkOrder.class, 
    		new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"}, -1, -1);
    this.workorderSel = WidgetUtil.initSelFromList(workorderList, "orderno");

    List supplierList = this.bdMgr.getDBEntity().getSubListQBC(Supplier.class, 
    		new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"}, -1, -1);
    this.supplierSel = WidgetUtil.initSelFromList(supplierList, "name");

    this.typeSel = WidgetUtil.initSelFromEnum(Util.DOCType.class);
    this.typeStatusSel = WidgetUtil.initSelFromEnum(Util.WorkOrderStatus.class);
    
    setTemplateText(tr("purchase-form"), TextFormat.XHTMLUnsafeText);
    Anchor submitbtn = new Anchor(new Link(""), "保存");
    submitbtn.setStyleClass("close");
    submitbtn.clicked().addListener(this, new Signal.Listener()
    {
      public void trigger() {
        SignDocForm.this.updateModel(SignDocForm.this.sdModel);
        if (SignDocForm.this.validationPassed(SignDocForm.this.sdModel)) {
        	if(sdModel.saveOrUpdate(sd,bdMgr.getDBEntity())){
				if(bdMgr.getSelectTr()!=null&&!bdMgr.getSelectTr().getId().equals(sd.getId())||bdMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
					bdMgr.drawGrid();
					bdMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+bdMgr.getPaginationStart()*bdMgr.getPaginationMax()+"});");
				}else if(bdMgr.getSelectTr()!=null&&bdMgr.getSelectTr().getId().equals(sd.getId())){//选中某一行，确实执行的是【编辑】操作
					bdMgr.refreshSelTr(sd);
				}
			}else{
				Util.infoMessage(tr("Information Please").toString(), "失败");
			}
          SignDocForm.this.pb.setAttributeValue("style", "display:none");
        }
      }
    });
    Anchor closebtn = new Anchor(new Link(""), "关闭");
    closebtn.setStyleClass("close");
    closebtn.clicked().addListener(this, new Signal.Listener()
    {
      public void trigger() {
        SignDocForm.this.pb.setAttributeValue("style", "display:none");
      }
    });
    if (this.title.equals("查看"))
      bindString("submit", "");
    else
      bindWidget("submit", submitbtn);
    bindWidget("close", closebtn);
    updateView(this.sdModel);
    return this;
  }
  @Override
  protected Widget createFormWidget(String field)
  {
    if ((field.equals("orderno-field"))) {
      le = new LineEdit();
    }else if(field.equals("slinkman-field")){
    	slinkman = new LineEdit();
    	le = slinkman;
    }else if(field.equals("sfax-field")){
    	sfax = new LineEdit();
    	le = sfax;
    }else if(field.equals("stel-field")){
    	slinktel = new LineEdit();
    	le = slinktel;
    }else if((field.equals("address-field"))){
    	le = new LineEdit();
    	((LineEdit)le).setText(tr("address").toString());
  	}else if(field.equals("mylinkman-field")){
    	le = new LineEdit();
    	((LineEdit)le).setText(tr("mylinkman").toString());
    }else if(field.equals("mytel-field")){
    	le = new LineEdit();
    	((LineEdit)le).setText(tr("mylinktel").toString());
    }else if(field.equals("myfax-field")){
    	le = new LineEdit();
    	((LineEdit)le).setText(tr("myfax").toString());
    }else if (field.equals("worder-field")) {
      le = workorderSel;
	  workorderSel.changed().addListener(this, new Signal.Listener() {
		@Override
		public void trigger() {
			String workorderNo = WidgetUtil.getSelDisplayStr(workorderSel);
			if(!workorderNo.equals("请选择")){
				//getDBEntity().
				
				/*int dash1 = selStr.indexOf("-");
				int dash2 = selStr.substring(dash1).indexOf("-");*/
				((LineEdit)SignDocForm.this.resolveWidget("orderno-field")).setText(bdMgr.getDBEntity().getSDOrderno(workorderNo, sd.getId()));
			}
				
		}
	  });
    }else if(field.equals("typeStatus-field")){
    	le= typeStatusSel;
    }else if (field.equals("supplier-field")) {
      le = supplierSel;
      supplierSel.changed().addListener(this,new Signal.Listener() {
		@Override
		public void trigger() {
			Object selO = WidgetUtil.getSelEO(supplierSel);
			if(selO!=null){
			((LineEdit)slinkman).setText(((Supplier)selO).getLinkman());
			((LineEdit)slinktel).setText(((Supplier)selO).getLinktel());
			String lfax = ((Supplier)selO).getLinkfax();
			((LineEdit)sfax).setText((lfax==null||lfax.equals(""))?"":lfax);
			}
			
		}
	});
    }else if (field.equals("orderdate-field")) {
      DateEdit d = new DateEdit();
      d.setFormat("yyyy-MM-dd");
      le = d;
    }else if (field.equals("id-field")) {
      le = new LineEdit();
      le.hide();
    } else if (field.equals("type-field")) {
      le = typeSel;
    } else if (field.equals("tech-field")) {
	      ContainerWidget cw = new ContainerWidget()
	      {
	        public DomElementType getDomElementType() {
	          return DomElementType.DomElement_DIV;
	        }
	      };
	      cw.setStyleClass("labeldiv");
	      for (final Util.ElementTechnology et : Util.ElementTechnology.values()) {
	        final CheckBox ck = new CheckBox(et.getName());
	        if ((sd.getTechStr() != null) && (sd.getTechStr().contains(et.getName())))
	          ck.setChecked(true);
	        ck.changed().addListener(this, new Signal.Listener()
	        {
	          public void trigger() {
	            if (ck.isChecked())
	            {
	              SignDocForm tmp14_11 = SignDocForm.this; tmp14_11.techStr = (tmp14_11.techStr + et.getName() + ",");
	            } else {
	              SignDocForm.this.techStr = SignDocForm.this.techStr.replace(et.getName() + ",", "");
	            }
	          }
	        });
	        cw.addWidget(ck);
	      }
	      le = cw;
    }
    return le;
  }
  private void updateClientView() {
     sdModel.setValue("orderno-field", this.sd.getOrderno());
     if(sd.getOrderDate()!=null)sdModel.setValue("orderdate-field", Util.formatDateyyyyMMdd(this.sd.getOrderDate()));
     if (sd.getWorkOrder()!= null) sdModel.setValue("worder-field", sd.getWorkOrder().getOrderno());
     if (sd.getSupplier()!= null) sdModel.setValue("supplier-field", sd.getSupplier().getName());
     if (sd.getDocType()!= null) sdModel.setValue("type-field", sd.getDocType().name);
     if (sd.getWostatus()!= null) sdModel.setValue("typeStatus-field", sd.getWostatus().toString());
    sdModel.setValue("address-field", sd.getProAddr());
    sdModel.setValue("mylinkman-field", sd.getProContactor());
    sdModel.setValue("mytel-field", sd.getProduceTele());
    sdModel.setValue("myfax-field", sd.getProduceFax());

    sdModel.setValue("slinkman-field", sd.getSupContactor());
    sdModel.setValue("stel-field", sd.getSupTele());
    sdModel.setValue("sfax-field", sd.getSupFax());
    sdModel.setValue("id-field", sd.getId());
    techStr = sd.getTechStr();
  }

  public void updateModelField(FormModel model, String field) {
    if (field.equals("worder-field"))
      sdModel.setValue(field, WidgetUtil.getSelEO(workorderSel));
    else if (field.equals("type-field"))
      sdModel.setValue(field, WidgetUtil.getSelEO(typeSel));
    else if(field.equals("typeStatus-field"))
    	sdModel.setValue(field, WidgetUtil.getSelEO(typeStatusSel));
    else if (field.equals("tech-field"))
      sdModel.setValue(field, techStr);
    else if (field.equals("supplier-field"))
      sdModel.setValue(field, WidgetUtil.getSelEO(supplierSel));
    else
      super.updateModelField(model, field);
  }
}