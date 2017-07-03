package cn.mazu.surface.mvc;

import java.util.List;

import cn.mazu.base.entity.Staff;
import cn.mazu.mysql.Entity;
import cn.mazu.surface.entity.Surface;
import cn.mazu.ui.SurfaceMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.validator.IntValidator;
import cn.mazu.workorder.entity.WorkOrder;

public class SurfaceForm extends FormView {
   private PopBox pb ;
   private SurfaceMgr sMgr;
   private SurfaceModel sModel;
   private Surface surface;
   private String title;
   private ComboBox copySel,workorderSel;
   private LineEdit compName;
   private Entity dbEntity ;
   private WorkOrder wo;
   
   private ComboBox listerSel = new ComboBox();
   private ComboBox checkerSel = new ComboBox();
   private ComboBox approverSel = new ComboBox();
   
   public SurfaceForm(String title,Surface surface,PopBox pb,SurfaceMgr sMgr) {
	  this.surface = surface;
	  this.pb = pb;
	  this.sMgr = sMgr;
	  this.dbEntity = sMgr.getDBEntity();
	  this.title = title;
	  sModel = new SurfaceModel();
	  compName = new LineEdit();
	  if(surface!=null&&!title.equals("新建"))
		  updateClientView();
	  else{
		  this.surface = new Surface();
		  sModel.setValue(sModel.code,dbEntity.getMaxCode());
	  }
	  //System.out.println("title:"+title);
   }
   @Override
	public Widget draw(final Template parent, String... args) {
	   List<EntityObject> copyList = dbEntity.getSubListQBC(Surface.class, new Object[]{"notEqual","id",surface.getId(),"and"}, -1, -1);
	   copySel = WidgetUtil.initSelFromList(copyList, "name");
	   
	   //关联的工单
	   List<EntityObject> wolist = dbEntity.getSubListQBC(WorkOrder.class, new Object[]{"like","dataCode","___","and"}, -1, -1);
	   workorderSel = WidgetUtil.initSelFromList(wolist, "orderno");
	   workorderSel.changed().addListener(this, new Signal.Listener() {
		@Override
		public void trigger() {
			if(WidgetUtil.getSelEO(workorderSel)!=null){
				String cpname = ((WorkOrder)WidgetUtil.getSelEO(workorderSel))
						.getClient().getName();
				compName.setText(cpname);
			}else
				compName.setText("");
		}
	});
	   List<EntityObject> staffList = sMgr.getDBEntity().getSubListQBC(Staff.class, new Object[]{"like","dataCode","___","and"},-1,-1);
	   
	   //制表、检查、核准
	   approverSel = WidgetUtil.initSelFromList(staffList, "name");
	   listerSel = WidgetUtil.initSelFromList(staffList, "name");
	   checkerSel = WidgetUtil.initSelFromList(staffList, "name");
	   
	   this.setTemplateText(tr("surface-form"),TextFormat.XHTMLUnsafeText);
		Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				SurfaceForm.this.updateModel(sModel);
				if(validationPassed(sModel)){
					if(sModel.saveOrUpdate(sMgr.getDBEntity(),surface)){
						if(sMgr.getSelectTr()!=null&&!sMgr.getSelectTr().getId().equals(surface.getId())||sMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							sMgr.drawGrid();
							sMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+sMgr.getPaginationStart()*sMgr.getPaginationMax()+"});");
						}else if(sMgr.getSelectTr()!=null&&sMgr.getSelectTr().getId().equals(surface.getId())){//选中某一行，确实执行的是【编辑】操作
							sMgr.refreshSelTr(surface);
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
		if(title.equals("查看"))
			this.bindString("submit", "");
		else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(sModel);
		return this;
    }
   @Override
	protected Widget createFormWidget(String field) {
	   FormWidget le = null;
		 if(field.equals(sModel.name)){
			le = new LineEdit();  
		  }else if(field.equals(sModel.chart)){
			  le = listerSel;
		  }else if(field.equals(sModel.check)){
			  le = checkerSel;
		  }else if(field.equals(sModel.approval)){
			  le = approverSel;
		  }else if(field.equals(sModel.code)){//显示图号只能输入数字
			  le = new LineEdit();
			  IntValidator iv = new IntValidator();
			  ((LineEdit)le).setValidator(iv);
			  ((LineEdit)le).setText(surface.getCode()==null||surface.getCode().equals("")?dbEntity.getMaxCode():surface.getCode().toString());
		  }else if(field.equals(sModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(sModel.copyface)){
			  if(title.equals("编辑"))
				  copySel.setDisabled(true);
			  le = copySel;
		  }else if(field.equals(sModel.workorder)){
			  if(title.equals("编辑"))
				  workorderSel.setDisabled(true);
			  le = workorderSel;
		  }else  if(field.equals(sModel.companyname)){
			  le =compName;
		  }
			return le;
	}
   private void updateClientView(){
	   sModel.setValue(sModel.eid, surface.getId());
	   sModel.setValue(sModel.approval, surface.getApprover());
	   sModel.setValue(sModel.code,surface.getCode());
	   sModel.setValue(sModel.check, surface.getChecker());
	   sModel.setValue(sModel.companyname, surface.getCompanyName());
	   sModel.setValue(sModel.name, surface.getName());
	   sModel.setValue(sModel.chart, surface.getLister());
	   if(surface.getCopySurface()!=null)sModel.setValue(sModel.copyface, surface.getCopySurface().getName());
	   //查询图面的工单
	   List<EntityObject> linkwo = dbEntity.getSubListQBC(WorkOrder.class, new Object[]{"equal","surface",surface,"and"}, -1, -1);
	   if(linkwo.size()>0) {
		   wo = (WorkOrder)linkwo.get(0);
		   sModel.setValue(sModel.workorder, wo.getOrderno());
	   }
	   
   }
	@Override
	public void updateModelField(FormModel model, String field) {
	    if(field.equals(sModel.copyface)){
	    	sModel.setValue(field, WidgetUtil.getSelEO(copySel));
	    }else if(field.equals(sModel.workorder)){
	    	sModel.setValue(field, WidgetUtil.getSelEO(workorderSel));
	    }else if(field.equals(sModel.chart)){
	    	if(WidgetUtil.getSelEO(listerSel)!=null)
	    	   sModel.setValue(field, ((Staff)WidgetUtil.getSelEO(listerSel)).getName());
	    	else
	    		sModel.setValue(field,"");
	    }else if(field.equals(sModel.check)){
	    	if(WidgetUtil.getSelEO(checkerSel)!=null)
	    	   sModel.setValue(field, ((Staff)WidgetUtil.getSelEO(checkerSel)).getName());
	    	else
	    		sModel.setValue(field, "");
	    }else if(field.equals(sModel.approval)){
	    	if(WidgetUtil.getSelEO(approverSel)!=null)
	    	   sModel.setValue(field, ((Staff)WidgetUtil.getSelEO(approverSel)).getName());
	    	else
	    		sModel.setValue(field, "");
	    }else{
	    	super.updateModelField(model, field);
	    }
	}
	
	/*protected ComboBox createSel(ComboBox cb,StringListModel cbModel){
		ComboBox authorSel = cb;
		StringListModel authorDropModel = cbModel;
	  	
	   List<EntityObject> staffList = sMgr.getDBEntity().getSubListQBC(Staff.class, new Object[]{"like","dataCode","___","and"},-1,-1);
	   authorDropModel.addString("请选择");
	   authorDropModel.setData(0,0,null,ItemDataRole.UserRole);   	
	   int i = 1;
	   for (Object o:staffList){
	   		Staff staff = (Staff)o;
	   		authorDropModel.addString(staff.getName());
	   		authorDropModel.setData(i++,0,staff.getName(),ItemDataRole.UserRole);
	   }
	   authorSel.setModel(authorDropModel);
	   return authorSel;
	}*/
	/*public void setModelField(ComboBox cb,StringListModel cbModel,String field) {		
		int row = cb.getCurrentIndex();
    	String st = (String) cbModel.getData(cbModel.getIndex(row,0), ItemDataRole.UserRole);
    	sModel.setValue(field, st!=null?st:"");
	}*/
}
