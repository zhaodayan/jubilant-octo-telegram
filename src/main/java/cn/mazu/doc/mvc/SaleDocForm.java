package cn.mazu.doc.mvc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Client;
import cn.mazu.surface.entity.Surface;
import cn.mazu.ui.SaleDocMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.UploadedFile;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.composite.Panel;
import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.WFileUpload;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.AnchorTarget;
import cn.mazu.widget.validator.IntValidator;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.mvc.WorkOrderModel;
//销售合同用
public class SaleDocForm extends FormView {
	private WorkOrderModel woModel;
	private PopBox pb;
	private SaleDocMgr sdMgr;
	private WorkOrder workOrder;
	private String title;
	private ComboBox surfaceSel,clientSel;
	private LineEdit amtLe = new LineEdit();	
    private Widget le = null;
    
    
    public SaleDocForm(String title,WorkOrder workOrder,PopBox popBox,SaleDocMgr sdMgr) {
		this.workOrder = workOrder;
		this.title = title;
		this.woModel = new WorkOrderModel();
	    this.pb = popBox;
	    this.sdMgr = sdMgr;
	    if(workOrder!=null&&!title.equals("新建"))
	    	updateClientView();
	    else
	    	this.workOrder = new WorkOrder();	    
	}
	@Override
	public Widget draw(final Template parent, String... args) {
		List<EntityObject> surfaceList = sdMgr.getDBEntity().getSubListQBC(Surface.class, 
				new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"}, -1, -1);
		surfaceSel = WidgetUtil.initSelFromList(surfaceList, "name");
		
		List<EntityObject> clientList = sdMgr.getDBEntity().getSubListQBC(Client.class, 
				new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"}, -1, -1);
		clientSel = WidgetUtil.initSelFromList(clientList,"name");
		
		this.setTemplateText(tr("workorder-form"),TextFormat.XHTMLUnsafeText);
		Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				SaleDocForm.this.updateModel(woModel);
				if(validationPassed(woModel)){
					if(woModel.saveOrUpdate(sdMgr.getDBEntity(),workOrder)){
						if(sdMgr.getSelectTr()!=null&&!sdMgr.getSelectTr().getId().equals(workOrder.getId())||sdMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							sdMgr.drawGrid();
							sdMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+sdMgr.getPaginationStart()*sdMgr.getPaginationMax()+"});");
						}else if(sdMgr.getSelectTr()!=null&&sdMgr.getSelectTr().getId().equals(workOrder.getId())){//选中某一行，确实执行的是【编辑】操作
							sdMgr.refreshSelTr(workOrder);
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
		this.updateView(woModel);
		return this;
	}
	@Override
	protected Widget createFormWidget(String field) {
//		Widget le = null;
		  if(field.equals(woModel.name))
				le = new LineEdit();
		  else if(field.equals(woModel.orderno)){
			  le = new LineEdit();
			  if(workOrder.getOrderno()==null||
					  workOrder.getOrderno()!=null&&workOrder.getOrderno().equals("")){
				  ((LineEdit)le).setText(sdMgr.getDBEntity().getWOCount());
			  }
		  }
		  else if(field.equals(woModel.realDate)||field.equals(woModel.shipDate)||field.equals(woModel.deliveryDate)
				  ||field.equals(woModel.predictDate)||field.equals(woModel.orderDate)){
			  DateEdit d = new DateEdit();
			  d.setFormat("yyyy-MM-dd");
			  le = d;
		  }else if(field.equals(woModel.remark)){
			  TextArea ta = new TextArea();
			  le = ta;
		  }else if(field.equals(woModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(woModel.surface)){
			  le = new LineEdit(woModel.valueText(woModel.surface));
			  ((LineEdit)le).setReadOnly(true);
		  }/*else if(field.equals(woModel.wostatus)){
			 le = statusSel;  
		  }*/else if(field.equals(woModel.corpname)){
			  le = clientSel;
		  }else if(field.equals(woModel.amount)){
			  IntValidator iv = new IntValidator();
			  amtLe.setValidator(iv);
			  amtLe.setMaxLength(7);
			  le = amtLe;
			  
		  }else if(field.equals(woModel.additionC)){			 
			   le = createAdditonUpload("contract file");
		  }else if(field.equals(woModel.additionT)){
			  le = createAdditonUpload("design file");
		  }
	   return le;
	}
	
	
	private void updateClientView(){
		woModel.setValue(woModel.amount, workOrder.getAmount());

		woModel.setValue(woModel.eid, workOrder.getId());
		woModel.setValue(woModel.name, workOrder.getName());
		woModel.setValue(woModel.orderno, workOrder.getOrderno());
		woModel.setValue(woModel.remark, workOrder.getRemark());
		if(workOrder.getOrderDate()!=null)
			woModel.setValue(woModel.orderDate, Util.formatDateyyyyMMdd(workOrder.getOrderDate()));
		if(workOrder.getRealDate()!=null)
			woModel.setValue(woModel.realDate, Util.formatDateyyyyMMdd(workOrder.getRealDate()));
		if(workOrder.getPredictDate()!=null)
			woModel.setValue(woModel.predictDate,Util.formatDateyyyyMMdd(workOrder.getPredictDate()));
		if(workOrder.getShipDate()!=null)
			woModel.setValue(woModel.shipDate, Util.formatDateyyyyMMdd(workOrder.getShipDate()));
		if(workOrder.getDeliveryDate()!=null)
			woModel.setValue(woModel.deliveryDate,Util.formatDateyyyyMMdd(workOrder.getDeliveryDate()));
		if (workOrder.getSurface()!=null) woModel.setValue(woModel.surface, workOrder.getSurface().getName());
		if(workOrder.getClient()!=null)woModel.setValue(woModel.corpname, workOrder.getClient().getName());
	}
	@Override
	public void updateModelField(FormModel model, String field) {
	    /*if(field.equals(woModel.wostatus)){
	    	woModel.setValue(field, WidgetUtil.getSelEO(statusSel));
	    }else */if(field.equals(woModel.amount)){
	    	if(amtLe.getText().toString().trim().equals(""))
	    		woModel.setValue(field,0);
	    	else
	    	   woModel.setValue(field,amtLe.getText().toString().replace(",", ""));
	    }else if(field.equals(woModel.corpname)){
	    	woModel.setValue(field, WidgetUtil.getSelEO(clientSel));
	    }else
		   super.updateModelField(model, field);
	}
	
	private Widget createAdditonUpload(final String fileName){
		final Template t = new  Template("");
		final WFileUpload wf = new WFileUpload();
	    Map<String,File> fileMap = new HashMap<String,File>();   
		
		t.setTemplateText("${panel}${wf}",TextFormat.XHTMLUnsafeText);
		t.bindString("panel", "");
		t.setAttributeValue("style", "width:282px;background-color:#f1f1f2;height: 55px;display: inline-block;margin-bottom: 5px;");
		  
	    wf.uploaded().addListener(wf, new Signal.Listener() {
	    	@Override
			public void trigger() {
				try {
					Util.deleteFiles(tr("base.filepath")+"/workOrder/"+fileName+"/"+workOrder.getId()+"/","*");
					String fpath = tr("base.filepath")+"/workOrder/"+fileName+"/"+workOrder.getId()+"/";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					
					File dirFile = new File(fpath);
					if (!dirFile.exists()) {
						dirFile.mkdirs();
					}
					List<UploadedFile> ls = wf.getUploadedFiles();
					for(UploadedFile file : ls){
						File source = new File(file.getSpoolFileName());
						int suffix = file.getClientFileName().lastIndexOf(".");
						String fname = file.getClientFileName();
						fname = fname.substring(0,fname.lastIndexOf("."));
						String ymd = sdf.format(new Date());
						fname+=ymd;
						if(file.getContentType().contains("image/gif"))
							fname = fname+".gif";
						else if(file.getContentType().contains("image/jpeg"))
							fname = fname+".jpg";
						else if(file.getContentType().contains("image/png"))
							fname = fname+".png";
						else
							fname = fname+file.getClientFileName().substring(suffix);
						
						woModel.setValue(woModel.additionC, fname);
						
						File target = new File(fpath+"/"+fname);
						Util.copyFile(source, target);
						source.delete();
						
						String uploadUrl = tr("www.file").toString()+"/workOrder/"+fileName+"/"+workOrder.getId()+"/"+fname;
						Anchor uploadAnchor = new Anchor(new Link(uploadUrl),fname);
						uploadAnchor.setStyleClass("downUrl");
						uploadAnchor.setTarget(AnchorTarget.TargetNewWindow);
						uploadAnchor.setToolTip("下载附件");
						  /*  Panel p = new Panel();
						    p.setTitle(fname);*/
						t.bindWidget("panel", uploadAnchor);
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	    wf.changed().addListener(wf, new Signal.Listener() {
	    	@Override
			public void trigger() {
	    		wf.upload();
	    	}
		});
	   t.bindWidget("wf", wf);

	   String pathName = tr("base.filepath")+"/workOrder/"+fileName+"/"+workOrder.getId()+"/";
	   Util.findFiles(pathName,"*",fileMap);
		String downUrl = "";
		if(fileMap.size()>0){
			String entry = fileMap.keySet().iterator().next();
			downUrl = tr("www.file").toString()+"/workOrder/"+fileName+"/"+workOrder.getId()+"/" + entry;
			Anchor downSource = new Anchor(new Link(downUrl),fileMap.get(entry).getName());
			downSource.setStyleClass("downUrl");
			downSource.setTarget(AnchorTarget.TargetNewWindow);
			downSource.setToolTip("下载附件");
			t.bindWidget("panel", downSource);
		}
		
		return t;
	}
	
}	
	
	
