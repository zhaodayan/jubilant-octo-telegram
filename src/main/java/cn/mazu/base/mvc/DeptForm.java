package cn.mazu.base.mvc;

import java.util.List;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Dept;
import cn.mazu.mysql.Entity;
import cn.mazu.ui.DeptMgr;
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
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class DeptForm extends FormView{
	private DeptModel dModel;
    private Dept dept;
    private PopBox popBox;
    private DeptMgr dMgr;
    private ComboBox deptCB = new ComboBox();
    private String title;
    public DeptForm(String title,Dept dept,PopBox popBox,DeptMgr dMgr) {	   
       this.dept = dept;
	   this.popBox = popBox;
	   this.dMgr = dMgr;
	   this.title = title;
	   dModel = new DeptModel();
	   //this.dept = (dept==null?new Dept():dept);
	   //updateClientView();	   
	   if(dept!=null&&!title.equals("新建"))
		   updateClientView();	
	   else
		   this.dept = new Dept();
	   
	   
	   /*deptCB.changed().addListener(this, new Signal.Listener() {
		@Override
		public void trigger() {
			System.out.println("currentindexdept:"+deptCB.getCurrentIndex());
		}
	  });*/
	}
    @Override
    public Widget draw(final Template parent, String... args) {
    	Entity entity = dMgr.getDBEntity();
    	
    	List<EntityObject> deptList = entity.getSubListQBC(Dept.class, 
    			                              new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and",
			                                "notEqual","id",dept.getId(),"and"},
			                                -1,-1);//新建则所有的，编辑要排除自己;
    	
    	deptCB = WidgetUtil.initSelFromList(deptList,"name");
    	
    	/*deptDropModel.addString("请选择");
    	deptDropModel.setData(0,0,null,ItemDataRole.UserRole);
    	
		int i = 1;
    	for (Object o:deptList){
    		Dept dept = (Dept)o;
    		deptDropModel.addString(dept.getName());
    		deptDropModel.setData(i++,0,dept,ItemDataRole.UserRole);
    	}
    	
    	deptCB.setModel(deptDropModel);*/
    	
    	
    	this.setTemplateText(tr("dept-form"),TextFormat.XHTMLUnsafeText);
    	Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				DeptForm.this.updateModel(dModel);
				if(validationPassed(dModel)){
					/*if(dModel.saveOrUpdate(dMgr.getDBEntity(),dept)){
						dMgr.draw(parent, null);
						dMgr.doJavaScript("$('#bctable').datagrid();");
					}else{
						Util.infoMessage("Information Please", "失败");
					}*/
					if(dModel.saveOrUpdate(dMgr.getDBEntity(),dept)){
						if(dMgr.getSelectTr()!=null&&!dMgr.getSelectTr().getId().equals(dept.getId())||dMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							dMgr.drawGrid();
							dMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+dMgr.getPaginationStart()*dMgr.getPaginationMax()+"});");
						}else if(dMgr.getSelectTr()!=null&&dMgr.getSelectTr().getId().equals(dept.getId())){//选中某一行，确实执行的是【编辑】操作
							dMgr.refreshSelTr(dept);
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
		this.updateView(dModel);
		return this;
    }
    
    @Override
    public void updateModelField(FormModel model, String field) {
    	if(field.equals(dModel.pdept)){
    		//updatePDeptModelField();
    		dModel.setValue(field,WidgetUtil.getSelEO(deptCB));
    	}else 
    	   super.updateModelField(model, field);
    }
    
    @Override
    protected Widget createFormWidget(String field) {
    	FormWidget fw = null;
        if(field.equals(dModel.code)||field.equals(dModel.name))
        	fw = new LineEdit();
        if(field.equals(dModel.pdept)){
        	fw = deptCB;
        }
        if(field.equals(dModel.eid)){fw = new LineEdit();fw.hide();}
        	
    	return fw;
    }
    private void updateClientView(){
    	dModel.setValue(dModel.code, dept.getCode());
    	dModel.setValue(dModel.name, dept.getName());
    	dModel.setValue(dModel.eid, dept.getId());
    	if(dept.getParent()!=null)dModel.setValue(dModel.pdept, dept.getParent().getName());
    }
    /*private void updatePDeptModelField(){
    	int row = deptCB.getCurrentIndex();
    	Dept pdept = (Dept)deptDropModel.getData(deptDropModel.getIndex(row,0), ItemDataRole.UserRole);
    	dModel.setValue(dModel.pdept, pdept);
    	
    }*/
}
